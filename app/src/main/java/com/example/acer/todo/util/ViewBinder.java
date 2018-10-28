package com.example.acer.todo.util;

import android.app.Activity;
import android.view.View;

import com.example.acer.todo.annotation.Bind;

import java.lang.reflect.Field;

public class ViewBinder {

    /*
    在 activity 中实现绑定用这个
     */
    public static void bind(Activity activity){
        bind(activity,activity.getWindow().getDecorView());
    }

    /**
     * 在其他地方  如fragment 时候用这个
     */
    public static void bind(Object target, View source){
        Field[]fields=target.getClass().getDeclaredFields();
        if (fields!=null&&fields.length>0){
            for (Field field:fields){
                try {
                    field.setAccessible(true);
                    //如果之前绑定过了就不用了?
                    if (field.get(target)!=null){
                        continue;
                    }
                    Bind bind=field.getAnnotation(Bind.class);
                    if (bind!=null){
                        int viewId=bind.value();
                        field.set(target,source.findViewById(viewId));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
