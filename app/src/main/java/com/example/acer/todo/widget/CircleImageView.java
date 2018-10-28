package com.example.acer.todo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.acer.todo.R;

public class CircleImageView extends ImageView {

    private static final String TAG="CircleImageView";

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private int mBorderWidth=DEFAULT_BORDER_WIDTH;
    private int mBorderColor=DEFAULT_BORDER_COLOR;
    private boolean mReady;                  //默认是false
    private boolean mSetupPending;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(SCALE_TYPE);

        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.CircleImageView,defStyleAttr,0);
        mBorderWidth=array.getDimensionPixelSize(R.styleable.CircleImageView_border_width,DEFAULT_BORDER_WIDTH);
        mBorderColor=array.getColor(R.styleable.CircleImageView_border_color,DEFAULT_BORDER_COLOR);
        array.recycle();

        mReady=true;
        if (mSetupPending){
            setUp();
            mSetupPending=false;
        }

    }
    private void setUp(){
        if (!mReady){
            mSetupPending=true;
            return;
        }
        mBitmapWidth=mBitmap.getWidth();
        mBitmapHeight=mBitmap.getHeight();

        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mBitmapShader=new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderRect.set(0,0,getWidth(),getHeight());
        mDrawableRect.set(mBorderWidth,mBorderWidth,mBorderRect.width()-mBorderWidth,mBorderRect.height()-mBorderWidth);

        //Log.d(TAG, "setUp: DrawableRect 的宽高："+mDrawableRect.width()+"//"+mDrawableRect.height());
        mDrawableRadius=Math.min(mDrawableRect.width(),mDrawableRect.height())/2;
        mBorderRadius=Math.min((mBorderRect.width()-mBorderWidth),(mBorderRect.height()-mBorderWidth))/2;

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix(){

        mBitmapWidth=mBitmap.getWidth();
        mBitmapHeight=mBitmap.getHeight();

        float scale=1;
        float dx=0;
        float dy=0;

        if (mBitmapWidth*mDrawableRect.height()>mDrawableRect.width()*mBitmapHeight){
            scale=mDrawableRect.height()/(float)mBitmapHeight;
            dx=(mDrawableRect.width()-mBitmapWidth*scale)*0.5f;
        }else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale,scale);
        mShaderMatrix.postTranslate((int)(dx+0.5f)+mBorderWidth,(int)(dy+0.5f)+mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap=getBitmapFromDrawable(drawable);
        setUp();
    }

    //    尺寸改变时调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.d(TAG, "onSizeChanged: 调用");
        setUp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable()==null){
            return;
        }
        canvas.drawCircle(getWidth()/2,getHeight()/2,mBorderRadius,mBorderPaint);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mDrawableRadius,mBitmapPaint);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable){
        if (drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable){
//                如果是 colordrawable 的话，， 直接创建一个宽高均为1，黑色的bitmap
                bitmap=Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,COLORDRAWABLE_DIMENSION,BITMAP_CONFIG);
            }else {
//                啥也不是的话，，就创建一个和 这个图片一样大的 黑色图片 代替
                bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
            }
            Canvas canvas=new Canvas(bitmap);
            drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
//            将drawable 绘制到  bitmap--canvas 的画布上去
            drawable.draw(canvas);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
