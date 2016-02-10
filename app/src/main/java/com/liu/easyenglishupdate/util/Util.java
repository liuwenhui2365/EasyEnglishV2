package com.liu.easyenglishupdate.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.liu.easyreadenglishupdate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/9/5.
 */
public class Util {
    /**
     * 存储个人信息
     */
    public final static String USER_INFO = "user_info";
    /**
     * 昵称
     */
    public final static String USER_NICK = "user_nick";
    /**
     * 页面跳转标志
     */
    public final static String SHIFT_FLAG = "shift_flag";
    /**
     * 类标志位
     */
    private final String TAG = Util.class.getSimpleName();
    public static boolean LOG_TAG = true;

    /**
     * 屏幕中动态提示。参数为String类型
     *
     * @param context 上下文
     * @param message 内容
     */
    public static void showToast(Activity context, String message) {
        if (null != context && null != message && null != Toast.makeText(context, message, Toast.LENGTH_SHORT)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 屏幕中动态提示。参数为int类型
     *
     * @param context 上下文
     * @param message 内容
     */
    public static void showToast(Context context, int message) {
        if (null != context && null != Toast.makeText(context, message, Toast.LENGTH_SHORT)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取当前的类名
     *
     * @return
     */
    public static String getClassName() {
        String className = "";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[0];
        className = thisMethodStack.getClassName();
        return className;
    }

    /**
     * log里面的方法实现超链接到具体类中的方法
     */
    public static String callMethodAndLink() {
        String result = "";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[1];
        result += thisMethodStack.getClassName() + ".";
        result += thisMethodStack.getMethodName();
        result += "(" + thisMethodStack.getFileName();
        result += ":" + thisMethodStack.getLineNumber() + ") ";
        return result;
    }

    /**
     * 用于调试程序
     *
     * @param tag     标志位
     * @param message 内容
     */
    public static void d(String tag, String message) {
        if (LOG_TAG) {
            Log.d(tag, message);
        }
    }

    /**
     * 用于捕获异常
     *
     * @param tag     标志位
     * @param message 内容
     */
    public static void e(String tag, String message) {
        if (LOG_TAG) {
            Log.e(tag, message);
        }
    }

    /**
     * 判断网络状态
     *
     * @return false 网络未连接
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            showToast(context, R.string.net_expection);
        }
        return true;
    }

    /**
     * 从网络获取标记词性后的文章内容
     *
     * @param content
     * @return
     */
    public static String post(String content) {
        String contentTag = null;
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost("http://nactem7.mib.man.ac.uk/geniatagger/a.cgi");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
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
                while ((line = br.readLine()) != null) {
//                    Log.d("文章内容",line);
                    temp.append(line);
                }
//                System.out.println(EntityUtils
//                        .toString(responseEntity, "utf-8"));
                contentTag = temp.toString();
                StringBuilder tempTag = new StringBuilder();
                Pattern expression = Pattern.compile("<table .*?>(.*?)</table>", Pattern.MULTILINE | Pattern.DOTALL);
                Matcher matcher = expression.matcher(contentTag);
                while (matcher.find()) {
                    String tag = matcher.group();
                    tempTag.append(tag);
                }
//               二次过滤
                expression = Pattern.compile("<tr>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?</tr>", Pattern.MULTILINE | Pattern.DOTALL);
                matcher = expression.matcher(tempTag);
//                清空上次内容
                temp.setLength(0);
                while (matcher.find()) {
                    String tag = matcher.group(1);
//                    Log.d("捕获到",tag);
                    temp.append(tag + "_");
                    tag = matcher.group(3);
//                    Log.d("捕获到词性",tag);
                    temp.append(tag + " ");
                }

                contentTag = temp.toString();
            } else {
                Log.d("Util报告", "获取文章内容词性，服务器无响应！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpClient.getConnectionManager().shutdown();
        }

        return contentTag;
    }

    /**
     * 本地标记文章中不认识单词的词性编码
     * (出现导包问题和识别文件路径问题！！)
     * 将此方法在存储数据库之前调用即可
     *
     * @param body
     * @return
     */
    private String addWordTag(final String body) {
        Util.d(TAG, "开始标记词性");
        long start = System.currentTimeMillis();
        //本地标记文章内容对应的单词词性
//        final File file = new File(OBJPATH);
//        if (file.exists() && file.length() != 0) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    MaxentTagger tagger = new MaxentTagger(OBJPATH);
//                    mTaggedContent = tagger.tagString(body.toString());
//                    Util.d(TAG, "标记后的文章内容" + mTaggedContent);
//                }
//            }).start();
//
//        }else{
//            //如果文件不存在则拷贝
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    InputStream inputStream = context.getResources().openRawResource(R.raw.taggers);
//                    FileOutputStream fileOutputStream = null;
//                    byte[] item = new byte[2048];
//                    int len = 0;
//                    Util.d("开始拷贝标记词性需要的文件", ".......");
//                    long start = System.currentTimeMillis();
//                    try {
////                        File dir = new File(file.getParent());
////                        dir.mkdir();
//                        fileOutputStream = new FileOutputStream(OBJPATH);
//                        while ((len = inputStream.read(item)) != -1) {
//                            fileOutputStream.write(item, 0, len);
//                        }
//                        inputStream.close();
//
//                        fileOutputStream.flush();
//                        fileOutputStream.close();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }finally {
//                        Util.d(TAG+"报告", "拷贝完成");
//                        long end = System.currentTimeMillis();
//                        Util.d(TAG+"报告","copy 耗时"+(end -start)/1000+"秒");
//                    }
//                }
//            }).start();
//        }

        long end = System.currentTimeMillis();
        Util.d(TAG + "报告", "本地词性耗时" + (end - start) / 1000 + "秒");
        Util.d(TAG + "报告", "单词标记完成");
        return body;
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * 从SD卡获取pdf文件
     *
     * @param rootPath 搜索根目录
     */
    public static void getPdfList(File rootPath, ArrayList<File> fileList) {
        //去掉以。开头的文件或文件夹,分级判断
        if (!rootPath.isDirectory()) {
            String name = rootPath.getName();
            Util.d("获取到文件名字",name);
            if (!name.startsWith(".") && name.endsWith(".pdf")) {
//                String filePath = path.getAbsolutePath();
                if (!fileList.contains(rootPath)) {
                    fileList.add(rootPath);
                }
            }
        } else {
            File[] files = rootPath.listFiles();
            for (int i = 0; i < files.length; i++) {
                File itemFile = files[i];
                if (!itemFile.isDirectory()) {
                    String name = files[i].getName();
                    if (!fileList.contains(itemFile) && !name.startsWith(".") && name.endsWith(".pdf")) {
                        fileList.add(itemFile);
                    }
                } else {
                    getPdfList(itemFile, fileList);
                }
            }
        }
    }
}
