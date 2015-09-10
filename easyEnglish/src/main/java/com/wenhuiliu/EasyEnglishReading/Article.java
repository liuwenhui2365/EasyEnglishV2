package com.wenhuiliu.EasyEnglishReading;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Article {

    /**
     * 文章类，描述关于文章的信息
     * 标题、内容、时间、分类、级别、难度系数
     * 通过构造方法来进行初始化，时间默认为当前时间、难度系数默认为100、级别默认高级
     * 通过getWords的方法把文章内容一单个单词的形式存储到链表
     */
    private String title;
    private StringBuilder body;
    private String time;
    private Catalogy catalogy;
    private Level level;
    private int difficultRatio;

    public Article(){
        this.title = "title";
        this.body = new StringBuilder("body");
        this.catalogy = Catalogy.valueOf("科技");
        this.time=new Date().toString();
        this.difficultRatio = 100;
        this.level = Level.valueOf("高级");
    }

    public Article(String title,StringBuilder body,String catalogy){
//        Log.d("开始初始化文章对象","...");
        if(title==null || body==null || catalogy == null) {
            throw  new IllegalArgumentException("title/body/catalogy not null");
        }
        this.title = title;
        this.body = body;
        this.catalogy = Catalogy.valueOf(catalogy);
        this.time=new Date().toString().substring(0,16);
//        Log.d("时间",time);
        this.difficultRatio = 100;
        this.level = Level.valueOf("高级");
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public StringBuilder getBody() {
        return body;
    }


    public void setBody(String body) {

        this.body = new StringBuilder(body);
    }



    public String getTime() {
        return time;
    }



    public void setTime(String time) {
        this.time = time;
    }


    public String getCatalogy(){
        return catalogy.getDescription();
    }

    public String getLevel(){
        return level.getLevel();
    }

    public void setLevel(String alevel){
        this.level = Level.valueOf(alevel);
    }

    public void setDifficultRatio(int ratio){
        this.difficultRatio = ratio;
    }

    public int getDifficultRatio(){
        return this.difficultRatio;
    }
    //	如果不匹配系统自动报错。
    public enum  Catalogy implements Serializable{
        科技         ( "科技"),
        娱乐         ( "自然"),
        健康         ( "健康"),
        理财      	( "教育"),
        经济   	    ( "经济"),
        今日  	    ( "今日"),
        默认         ( "默认");

        //        private final int status;
        private final String description;

        //       在构造函数的时候给其赋值
        Catalogy(String desc) {
//        	 this.status = aStatus;
            this.description = desc;
        }


//        public int getStatus() {
//            return this.status;
//        }

        public String getDescription() {
            return this.description;
        }

    }

    public enum Level{
        初级    ("初级"),
        中级    ("中级"),
        高级    ("高级");

        private final String level;
        private Level(String alevel){
            this.level = alevel;
        }

        public String getLevel(){
            return this.level;
        }
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
}
