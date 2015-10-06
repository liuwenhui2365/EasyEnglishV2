package com.liu.easyenglishupdate.util;

import android.util.Log;

import com.liu.easyenglishupdate.entity.Article;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/2.
 * 获取ChinaDaily的网页内容，并按要求对应相应的内容
 * 获取适合手机的网页内容
 */
public class MSpiderChinaDaily implements ISpiderArticle {
        final private Pattern titlePat = Pattern.compile("<h2>(.*?)</h2>", Pattern.MULTILINE|Pattern.DOTALL);
        final private Pattern timePat = Pattern.compile("<div class=\"articleTitle\">.*?<span class=\"from\">(.*?)</span>",Pattern.MULTILINE|Pattern.DOTALL);
        //	final private Pattern catalogyPat = Pattern.compile("<div class=\"w980 greyTxt9 titleTxt22 pt20 pb10\"><a.*?>(.*?)</a>",Pattern.MULTILINE|Pattern.DOTALL);
        final private Pattern contentPat = Pattern.compile("<div.*sudaclick=\"articleContent\">.*?(<p[^a-zA-Z].*?)</div>", Pattern.MULTILINE|Pattern.DOTALL);
        final private Pattern contentFilter = Pattern.compile("<p.*?>(<.*?>)*(.*?)(<.*?>)*</p>");

    //final private Pattern contentPat = Pattern.compile("<div id=\"j_articleContent\".*?</div>", Pattern.MULTILINE);

        @Override
        public ArrayList<String> getUrlList(String catalogy) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getMessage(String url) throws IOException {
            // TODO Auto-generated method stub
            String message = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = httpResponse.getEntity();
                message = EntityUtils.toString(entity,"UTF-8");
            }
//            Log.d("网页内容",message);
            return message;
        }

        @Override
        public String getTitle(String message) {
            // TODO Auto-generated method stub
            String title = null;
            Matcher mat = titlePat.matcher(message);
            if(mat.find()){
                title = mat.group(1);
            }
            return title;
        }

        @Override
        public StringBuilder getContent(String message) {
            // TODO Auto-generated method stub
            StringBuilder content = new StringBuilder();
            //Log.d("过滤内容",message);
            Matcher mat = contentPat.matcher(message);
            while(mat.find()){
                String body = mat.group(1);
                Matcher filter = contentFilter.matcher(body);
//                Log.d("内容：", body);
                while(filter.find()) {
                    String bodyItem = filter.group(2);
                    bodyItem = bodyItem.replace("<em>", "");
                    bodyItem = bodyItem.replace("<EM>", "");
                    bodyItem = bodyItem.replace("</em>", "");
                    bodyItem = bodyItem.replace("</EM>", "");
                    if (bodyItem.length() != 0) {
//                        Log.d("每行：", bodyItem);
                        content.append(bodyItem);
                        content.append("^");
                    }
                }

            }

            return content;
        }

        @Override
        public String getTime(String message) {
            // TODO Auto-generated method stub
            String time = null;
            Matcher mat = timePat.matcher(message);
            if(mat.find()){
                time = mat.group(1);
            }
            return time;
        }

        @Override
        public String getCatalogy(String message) {
            // TODO Auto-generated method stub
//            String catalogy = null;
//		Matcher mat = catalogyPat.matcher(message);
//		if(mat.find()){
//			catalogy = mat.group(1).substring(0,2);
//		}
            return "默认";
        }

    //    初始化文章对象
        public Article getPassage(String url) throws IOException {
            String message = getMessage(url);
//            Log.d("网页内容",message);
            String title = getTitle(message);
//            Log.d("标题完成", title);
            String content = getContent(message).toString();
            Log.d("内容完成",content.toString());
            String catalogy = getCatalogy(message);
            String time = getTime(message);
//            Log.d("时间完成",time);
            Article article = new Article(title, content, catalogy);
            article.setTime(time);
//            Log.d("文章对象初始化完成！","恭喜");
            return article;
        }
 }

