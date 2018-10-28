package com.example.acer.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    public static final String FILE_NAME="share_data";

    public static void put(Context context,String key,Object object){

        SharedPreferences sharedPreferences=context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        if (object instanceof Boolean){
            editor.putBoolean(key,(Boolean)object);
            editor.apply();
        }
    }

    public static Object get(Context context,String key,Object defaultObject){
        SharedPreferences sharedPreferences=context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof Boolean){
            return sharedPreferences.getBoolean(key,(boolean)defaultObject);
        }
        return null;
    }
}
