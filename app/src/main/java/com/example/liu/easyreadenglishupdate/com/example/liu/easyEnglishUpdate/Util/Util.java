package com.example.liu.easyreadenglishupdate.com.example.liu.easyEnglishUpdate.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/9/5.
 */
public class Util {
    public static boolean LOG_TAG = true;

    /**
     * 屏幕中动态提示。参数为String类型
     * @param context 上下文
     * @param message 内容
     */
    public static void showToast(Activity context,String message){
        if (null != Toast.makeText(context,message,Toast.LENGTH_SHORT)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 屏幕中动态提示。参数为int类型
     * @param context 上下文
     * @param message 内容
     */
    public static void showToast(Context context,int message){
        if (null != Toast.makeText(context,message,Toast.LENGTH_SHORT)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用于调试程序
     * @param tag  标志位
     * @param message 内容
     */
    public static void d(String tag,String message){
        if(LOG_TAG){
            Log.d(tag, message);
        }
    }

    /**
     * 用于捕获异常
     * @param tag  标志位
     * @param message  内容
     */
    public static void e(String tag,String message){
        if(LOG_TAG){
            Log.e(tag,message);
        }
    }
}
