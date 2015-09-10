package com.wenhuiliu.EasyEnglishReading;

import android.util.Log;
import android.widget.Toast;

import com.example.liu.autotanslate.Refresh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderArticle implements ISpiderArticle{

    private Matcher mat = null;
    private ArrayList<String> techURLList;
    private ArrayList<String> healthURLList;
    private ArrayList<String> educationURLList;
    private ArrayList<String> economicsURLList;
    private ArrayList<String> natureURLList;
    private ArrayList<String> othersURLList;


    public HashMap<String, ArrayList<String>> urlList;
    final private Pattern timePat = Pattern.compile("<SPAN class=datetime>(.*)</SPAN>", Pattern.CASE_INSENSITIVE);
    final private Pattern titlePat = Pattern.compile("<div id=\"title\">(.*?)</div>");
    final private Pattern contentPat = Pattern.compile("<P>([^<_]*?)</P>", Pattern.MULTILINE|Pattern.DOTALL);
    final private Pattern catalogyPat = Pattern.compile("<div id=\"nav\">.*title=.*?title=\"(.*?)\">");


    public SpiderArticle(){
        techURLList = new ArrayList<String>();
        techURLList.add("http://www.51voa.com/Technology_Report_1.html");
        techURLList.add("http://www.51voa.com/Technology_Report_2.html");
        urlList = new HashMap<String,ArrayList<String>>();
        urlList.put("科技", techURLList);

        healthURLList = new ArrayList<String>();
        healthURLList.add("http://www.51voa.com/Health_Report_1.html");
        healthURLList.add("http://www.51voa.com/Health_Report_2.html");
        urlList.put("健康", healthURLList);


        educationURLList = new ArrayList<String>();
        educationURLList.add("http://www.51voa.com/Education_Report_1.html");
        educationURLList.add("http://www.51voa.com/Education_Report_2.html");
        urlList.put("教育", educationURLList);

        economicsURLList = new ArrayList<String>();
        economicsURLList.add("http://www.51voa.com/Economics_Report_1.html");
        economicsURLList.add("http://www.51voa.com/Economics_Report_2.html");
        urlList.put("经济", economicsURLList);


        natureURLList = new ArrayList<String>();
        natureURLList.add("http://www.51voa.com/Explorations_1.html");
        natureURLList.add("http://www.51voa.com/Explorations_2.html");
        urlList.put("自然", natureURLList);

        othersURLList = new ArrayList<String>();
        othersURLList.add("http://www.51voa.com/This_is_America_1.html");
        othersURLList.add("http://www.51voa.com/This_is_America_2.html");
        urlList.put("今日", othersURLList);
    }


    public String getMessage(String url){
        String message = null;
        HttpClient hc = new HttpClient(url);
        message = hc.getResponse();
        return message;
    }

    @Override
    public ArrayList<String> getUrlList(String catalogy) {
        ArrayList<String>  urls = new ArrayList<String>();
        if(urlList.get(catalogy) != null){
            for (String url : urlList.get(catalogy)) {
                String message = new HttpClient(url).getResponse();
                Pattern pat = Pattern.compile("<li>.*?href=\"(.*?)\" target.*?</li>");
                Matcher mat = pat.matcher(message);
                while(mat.find()){
                    urls.add("http://www.51voa.com"+mat.group(1));
                }
            }
        }
        return urls;
    }

    @Override
    public String getTitle(String message){
        String title = null;
        if(message != null) {

            mat = titlePat.matcher(message);
            if (mat.find()) {
                title = mat.group(1);
            }
        }
        return title;
    }

    @Override
    public StringBuilder getContent(String message){
        StringBuilder content = new StringBuilder();
        mat = contentPat.matcher(message);
        while(mat.find()){
            content.append(mat.group(1));
            content.append("^");
        }
        return content;
    }

    @Override
    public String getTime(String message){
        String time = null;
        mat = timePat.matcher(message);
        if(mat.find()){
            time = mat.group(1);
        }
        return time;
    }

    @Override
    public String getCatalogy(String message){
        String catalogy = "null";
        mat = catalogyPat.matcher(message);
        if(mat.find()){
            catalogy = mat.group(1).substring(0,2);
        }
        return catalogy;
    }

    public Article getPassage(String url){
        String message = getMessage(url);
        String title = getTitle(message);
        StringBuilder content = getContent(message);
        String catalogy = getCatalogy(message);
        String time = getTime(message);
        Article article = new Article(title, content, catalogy);
        article.setTime(time);
        return article;
    }
}
