package com.liu.easyenglishupdate.entity;

import com.liu.easyenglishupdate.util.MyURLSpan;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章序列化实现对象传输
 */
public class Article extends DataSupport implements Serializable{

    /**
     * 文章类，描述关于文章的信息
     * 标题、内容、时间、分类、级别、难度系数
     * 通过构造方法来进行初始化，时间默认为当前时间、难度系数默认为100、级别默认高级
     * 通过getWords的方法把文章内容一单个单词的形式存储到链表
     * 关联数据库创建的表的时候用到此类，如果某个变量不想关联，修改修饰符为其他即可。
     * 实现添加数据必须继承DataSupport,这里需要注意不支持StringBuilder
     */
    private String title;
    private String body;
    private String time;
    private String catalogy;
    private String level;
    private String url;
    private int difficultRatio;

    public Article(){
        this.title = "title";
        this.body = "body";
        this.time=new Date().toString();
        this.difficultRatio = 100;
    }

    public void setCatalogy(String catalogy) {
        this.catalogy = catalogy;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Article(String title,String body,String catalogy){
//        Log.d("开始初始化文章对象","...");
        if(title==null || body==null || catalogy == null) {
            throw  new IllegalArgumentException("title/body/catalogy not null");
        }
        this.title = title;
        this.body = body;

        this.time=new Date().toString().substring(0,16);
//        Log.d("时间",time);
        this.difficultRatio = 100;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getCatalogy(){
        return catalogy;
    }

    public void setDifficultRatio(int ratio){
        this.difficultRatio = ratio;
    }

    public int getDifficultRatio(){
        return this.difficultRatio;
    }

    private String description;

    public String getDescription() {
            return this.description;
        }

//   把文章内容为String类型的转为Arraylist类型的
    public ArrayList<String> getWords(){
        ArrayList<String> words = new ArrayList<String>();
        //Pattern expression = Pattern.compile("[a-zA-Z,."';:]+");  //定义正则表达式匹配单词
        //Pattern expression = Pattern.compile("([\\w]+)|([,.:;\"?!]+)");
        //Pattern expression = Pattern.compile("([\\w]+)|([\\pP])");
        Pattern expression = Pattern.compile("([\\w]+)|([.,\"\\?!:'$^])");

        Matcher matcher = expression.matcher(body);

        while(matcher.find()){
            words.add(matcher.group());
//            System.out.println(matcher.group());
        }

        return words;

    }


    /**
     *     把文章内容为String类型的转为Arraylist类型的
     */
    public ArrayList<String> getWordsBySpace(){
        ArrayList<String> wordsList = new ArrayList<String>();
        String [] words = body.split(" ");
        for (int i = 0; i<words.length; i++){
            wordsList.add(words[i]);
        }
        return wordsList;

    }
}
