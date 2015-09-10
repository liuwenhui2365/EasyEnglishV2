package com.wenhuiliu.EasyEnglishReading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.liu.autotanslate.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {
    private static final String TAG = Translate.class.getSimpleName();

//  统计不认识的单词个数
    private int unknownWordsNum;
    private Context context;
    private HashMap<String,String> unknownWords = new HashMap<>();
    SQLiteDatabase db = null;

    //	初级 < 20%
    final private int  PRIMARY = 20;
    //	中级 < 50%
    //final private int INTERMEDIATE = 50;
    // 高级 > 50%
    final private int ADVANCED = 50;

    public Translate(Context context){
        this.context = context;
    }

    public ArrayList<Article> translate(ArrayList<Article> passages){
//	        存放翻译好的文章链表
        ArrayList<Article> objPassages = new ArrayList<Article>();
        for(int i=0;i<passages.size();i++){
//		    把文章的内容转成字符串
            ArrayList<String> objBody = passages.get(i).getWords();
//			翻译文章内容中的每一个不认识的单词
            for (int j = 0; j < objBody.size(); j++) {
                String word = objBody.get(j);
                Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
                Matcher matcher = expression.matcher(word);
                if(unknownWords.containsKey(word) && matcher.find()){
                        unknownWordsNum++;
                        objBody.set(i, word+"("+unknownWords.get(word)+")");
                }
//			ToDo 	暂时不添加网络获取并加入单词表中
//                         unknownWordsNum++;
//                        String unmean = HttpClient.ClientRun(word);
//                        objBody.set(i, unmean);

            }

            passages.get(i).setBody(objBody.toString());
            int difficultRatio = unknownWordsNum / objBody.size() * 100;
            if( difficultRatio <= PRIMARY ){
                passages.get(i).setLevel("初级");
            }else if(difficultRatio <= ADVANCED){
                passages.get(i).setLevel("中级");
            }else{
                passages.get(i).setLevel("高级");
            }
            objPassages.set(i,passages.get(i));
        }

        return objPassages;
    }

    public void getWordClassify(DbArticle dbArticle) {
//		从数据库中获取不认识单词及意思
        HashMap<String, String> wordMeaning = new HashMap<String, String>();

        Cursor c = null;
        try {
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            String word = null;
            String meaning = null;
            if (c.moveToNext()) {
                int count = c.getInt(0);
//                Log.d("表的个数",count+"");
                if (count > 0) {
//                    注意修改之后的类型名
                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"unknow"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("bu知道的单词和意思",word+meaning);
                        unknownWords.put(word, meaning);
                    }

                } else {
//                    一般情况是不会运行此部分
                    wordMeaning = Words.getWord();
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
                    Log.e("数据库", "单词表创建成功");
//                  从map中读取单词和意思
                    Iterator iter = wordMeaning.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
//                 ***** 往数据库里放数据（默认是认识的）*******
                        db.execSQL("INSERT INTO words values(?,?,?)", new String[]{entry.getKey().toString(), entry.getValue().toString(), "know"});
                        //                Log.d("单词：", entry.getKey().toString());
                        //                Log.d("意思：", entry.getValue().toString());
                    }

                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"unknow"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("bu知道的单词和意思", word + meaning);
                        unknownWords.put(word, meaning);
                    }
                }
            }
        }catch (Exception w) {
            w.printStackTrace();
        }finally {
            if (c != null){
                c.close();
            }

            if(db != null){
                db.close();
            }

        }

    }

//  由于上下文问题，接受传过来的数据库对象
//    优化翻译方式不需要读取认识单词
//    ************************以前版本**************************************
//    public Article translate(Article article,DbArticle dbArticle){
////	        存放翻译好的文章链表
////		    把文章的内容转成字符串
//        ArrayList<String> bodyWords = article.getWords();
//        StringBuilder body = new StringBuilder();
////			翻译文章内容中的每一个不认识的单词
//        for (int j = 0; j < bodyWords.size(); j++) {
//            String word = bodyWords.get(j);
//            Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
//            Matcher matcher = expression.matcher(word);
////            Log.d("不认识单词",unknownWords.size()+"");
//            if(unknownWords.containsKey(word) && matcher.find()){
////                    Log.d("提示","从不认识的单词表中获取");
//                unknownWordsNum++;
//                bodyWords.set(j, word+"("+unknownWords.get(word)+")");
//                body.append(bodyWords.get(j)+" ");
//    //                    TODO 暂时不添加
//    //                    Log.d("提示","从网络获取");
////              unknownWordsNum++;
////              String wordMeaning = HttpClient.ClientRun(word);
////              bodyWords.set(j, word+"("+wordMeaning+")");
////              body.append(bodyWords.get(j)+" ");
//
//            }else if (word.equalsIgnoreCase("^")){
////                System.out.println("找到符号了"+word);
//                body.append("\n\n");
//            }else {
//                body.append(bodyWords.get(j) + " ");
//            }
//        }
//
//        article.setBody(body.toString());
//        int difficultRatio = unknownWordsNum / bodyWords.size() * 100;
//        if( difficultRatio <= PRIMARY ){
//            article.setLevel("初级");
//        }else if(difficultRatio <= ADVANCED){
//            article.setLevel("中级");
//        }else{
//            article.setLevel("高级");
//        }
//
//        return article;
//    }
//    ********************************************************

    public Article translate(Article article,DbArticle dbArticle){
//	        存放翻译好的文章链表
//		    把文章的内容转成字符串
        ArrayList<WordMap> articleTagged = null;
        getWordClassify(dbArticle);
        ArrayList<String> bodyWords = article.getWords();
        articleTagged =  transContent(article.getBody());

//        article.setBody(translateUnknowWords(articleTagged,dbArticle));
        int difficultRatio = unknownWordsNum / bodyWords.size() * 100;
        if( difficultRatio <= PRIMARY ){
            article.setLevel("初级");
        }else if(difficultRatio <= ADVANCED){
            article.setLevel("中级");
        }else{
            article.setLevel("高级");
        }

        return article;
    }


    private  ArrayList<WordMap> transContent(StringBuilder body) {
        ArrayList<WordMap> articleTagged = new ArrayList<WordMap>();
//        Log.d(TAG,"文章内容"+body);
        Log.d(TAG,"开始标记词性");
        Log.d(TAG, "路径:" + context.getExternalCacheDir());
//        MaxentTagger tagger = new MaxentTagger(context.getExternalCacheDir()+ "raw/english-bidirectional-distsim.tagger");
        Log.d("标记后的单词","hhhhhhhhhddddd");


//        Log.d(TAG,"标记后的文章内容"+tagged);
//        String [] wordsTagged = tagged.split(" ");
//        for (String word : wordsTagged) {
//            String[] wordlist = word.split("_");
//            WordMap wordMap = new WordMap(wordlist[0], wordlist[1]);
//            articleTagged.add(wordMap);
//        }
        Log.d("单词标记完成","哈哈哈");
        return articleTagged;
    }


    private String translateUnknowWords(ArrayList<WordMap> articleTagged,DbArticle dbWordTag) {

//			翻译文章内容中的每一个不认识的单词
        StringBuilder body = new StringBuilder();
        for (int j = 0; j < articleTagged.size(); j++) {
            String word = articleTagged.get(j).getWord();
            Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
            Matcher matcher = expression.matcher(word);
            Log.d(TAG+"不认识单词大小",unknownWords.size()+"");
            if(unknownWords.containsKey(word) && matcher.find()){
//                    Log.d("提示","从不认识的单词表中获取");
                unknownWordsNum++;
                String tag = getUnknowWordTag(articleTagged.get(j).getValue(),dbWordTag);
//              根据词性查找意思
                String [] meaning = unknownWords.get(word).split("|");
                for (int i = 0; i <meaning.length ; i++) {
                    if (meaning[i].contains(tag)){
                        body.append(word+"("+meaning[i]+")"+" ");
                    }
                }
            }else if (word.equalsIgnoreCase("^")){
                body.append("\n\n");
            }else {
                body.append(word+" ");
            }
        }

        return body.toString();
    }

    private String getUnknowWordTag(String value,DbArticle dbWordTag) {
        String tag = null;
        String line = null;
        Cursor c = null;
        try {
            db = dbWordTag.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='wordsTag'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
//                Log.d("表的个数",count+"");
                if (count > 0) {
//                    注意修改之后的类型名
                    c = db.rawQuery("select type from words where tag = ?", new String[]{value});
                    while (c.moveToNext()) {
                        tag = c.getString(c.getColumnIndex("tag"));
                        Log.d("获取到不认识单词的词性为", tag);
                    }
                } else {
//                    一般情况是不会运行此部分
                    db.execSQL("CREATE TABLE wordsTag (tag VARCHAR PRIMARY KEY,type VARCHAR)");
                    Log.e("数据库", "单词词性编码表创建成功");

                    InputStream inputStream = context.getResources().openRawResource(R.raw.wordtag);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = reader.readLine()) != null) {
                        String[] type = line.split(" ");
                        db.execSQL("INSERT INTO words values(?,?)", new String[]{type[0], type[1]});

                    }

                    c = db.rawQuery("select type from words where tag = ?", new String[]{value});
                    while (c.moveToNext()) {
                        tag = c.getString(c.getColumnIndex("type"));
//                        Log.d("获取到的词性", tag);
                    }
                }
            }
        }catch (FileNotFoundException e){
            Log.e("文件没有找到","oooo");
        }catch (Exception w) {
            w.printStackTrace();
        }finally {
            if (c != null){
                c.close();
            }

            if(db != null){
                db.close();
            }

        }

        return tag;
    }


    //    ToDo 完善该方法
    public void insertWord(DbArticle dbArticle,String word,String meaning){
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
//            DbArticle dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate(context);
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
                    db.execSQL("INSERT INTO words values(?,?,?)", new String[]{word, meaning, "known"});
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("数据库异常","ERROR");
        }finally {
            if(c != null){
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }

    }
    //	翻译好的文章进行分类
    public ArrayList<Article>  getPassages (ArrayList<Article> objPassages,String level,String catalogy){
        ArrayList<Article> classifiedPassages = new ArrayList<Article>();
        for (int i = 0; i < objPassages.size(); i++) {
            //			测试一下比较方法
            if(objPassages.get(i).getCatalogy().equalsIgnoreCase(catalogy) && objPassages.get(i).getLevel().equalsIgnoreCase(level)){
                //			   用的是add方法不是set方法
                classifiedPassages.add(objPassages.get(i));
            }
        }

        return classifiedPassages;
    }
}

//   单词类存储单词和词性或意思
class WordMap{
    String word;
    String value;

    public WordMap(String word,String value){
        this.word = word;
        this.value = value;
    }

    public String getWord(){
        return this.word;

    }

    public String getValue(){
        return this.value;
    }

}
