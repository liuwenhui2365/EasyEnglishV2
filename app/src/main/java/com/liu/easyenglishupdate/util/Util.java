package com.liu.easyenglishupdate.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (null!= context && null!=message &&null != Toast.makeText(context,message,Toast.LENGTH_SHORT)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 屏幕中动态提示。参数为int类型
     * @param context 上下文
     * @param message 内容
     */
    public static void showToast(Context context,int message){
        if (null!= context &&null != Toast.makeText(context,message,Toast.LENGTH_SHORT)) {
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
            Log.e(tag, message);
        }
    }

    /**
     * 从网络获取标记词性后的文章内容
     * @param content
     * @return
     */
    public static String post(String content) {
        String contentTag = null;
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost("http://nactem7.mib.man.ac.uk/geniatagger/a.cgi");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        try {
            // 为httpPost设置HttpEntity对象
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("paragraph", content));
            HttpEntity entity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(entity);
            // httpClient执行httpPost表单提交
            HttpResponse response = httpClient.execute(httpPost);
            // 得到服务器响应实体对象
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuilder temp = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null){
//                    Log.d("文章内容",line);
                    temp.append(line);
                }
//                System.out.println(EntityUtils
//                        .toString(responseEntity, "utf-8"));
                contentTag = temp.toString();
                StringBuilder tempTag = new StringBuilder();
                Pattern expression = Pattern.compile("<table .*?>(.*?)</table>",Pattern.MULTILINE|Pattern.DOTALL);
                Matcher matcher = expression.matcher(contentTag);
                while (matcher.find()){
                    String tag = matcher.group();
                    tempTag.append(tag);
                }
//               二次过滤
                expression = Pattern.compile("<tr>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?</tr>",Pattern.MULTILINE|Pattern.DOTALL);
                matcher = expression.matcher(tempTag);
//                清空上次内容
                temp.setLength(0);
                while (matcher.find()){
                    String tag = matcher.group(1);
//                    Log.d("捕获到",tag);
                    temp.append(tag+"_");
                    tag = matcher.group(3);
//                    Log.d("捕获到词性",tag);
                    temp.append(tag+" ");
                }

                contentTag = temp.toString();
            } else {
                Log.d("Util报告","获取文章内容词性，服务器无响应！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpClient.getConnectionManager().shutdown();
        }

        return contentTag;
    }

}
