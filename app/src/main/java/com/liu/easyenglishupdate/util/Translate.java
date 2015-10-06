package com.liu.easyenglishupdate.util;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.entity.WordTag;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {
    private static final String TAG = Translate.class.getSimpleName();
    /**
     * 用来将文件拷贝到SD卡然后调用
     */
    private static final String OBJPATH = Environment.getExternalStorageDirectory()+"/EasyEnglish/englishTag";
    /**
     * 标记后的文章内容
     */
    private String mTaggedContent;
    /**
     * 标记后的文章内容以单词和词性编码对应保存
     */
    private ArrayList<WordMap> articleTagged = new ArrayList<WordMap>();

    //  统计不认识的单词个数
    private int unknownWordsNum;
    private Activity mActivity;
    private HashMap<String,String> unknownWords = new HashMap<>();
    SQLiteDatabase db = null;

    //	初级 < 20%
    final private int  PRIMARY = 20;
    //	中级 < 50%
    final private int INTERMEDIATE = 50;
    // 高级 > 50%
    final private int ADVANCED = 50;

    public Translate(Activity context){
        this.mActivity = context;
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
//            if( difficultRatio <= PRIMARY ){
//                passages.get(i).setLevel("初级");
//            }else if(difficultRatio <= ADVANCED){
//                passages.get(i).setLevel("中级");
//            }else{
//                passages.get(i).setLevel("高级");
//            }
            objPassages.set(i,passages.get(i));
        }

        return objPassages;
    }

    /**
     * 获取不认识的单词
     */
    public void getWordClassify() {
//		从数据库中获取不认识单词及意思
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        try {
            List<Word> unknowList = DataSupport.where("type = ?","unknow").find(Word.class);
            for (int i=0; i<unknowList.size(); i++){
                Word unknowWord = unknowList.get(i);
                String word = unknowWord.getWord();
                String meaning = unknowWord.getMeaning();
//                Util.d(TAG+"不知道的单词和意思", word + meaning);
                unknownWords.put(word, meaning);
            }
            Util.d(TAG+"不认识单词大小",unknownWords.size()+"");

        }catch (Exception w) {
            w.printStackTrace();
        }

    }

    /**
     * 翻译文章
     * @param article
     * @return
     */
    public Article translate(Article article){
//	        存放翻译好的文章链表
//		    把文章的内容转成字符串
        ArrayList<WordMap> articleTagged = null;
        getWordClassify();
        //统计文章内容大小
        ArrayList<String> bodyWords = article.getWords();
        articleTagged =  transContent(article.getBody());
        if (articleTagged.size()>0) {
            article.setBody(translateUnknowWords(articleTagged));
        }
        int difficultRatio = unknownWordsNum / bodyWords.size() * 100;
//        if( difficultRatio <= PRIMARY ){
//            article.setLevel("初级");
//        }else if(difficultRatio <= ADVANCED){
//            article.setLevel("中级");
//        }else{
//            article.setLevel("高级");
//        }

        return article;
    }

    /**
     * 标记文章中不认识单词的词性编码
     * (出现导包问题和识别文件路径问题！！)
     * 通过网络获取，在翻译的时候开启线程，这里不需要开了，
     * 否则会出现刚开始标记的词性文章内容为空
     * @param body
     * @return
     */
    private  ArrayList<WordMap> transContent(final String body) {
//        Util.d(TAG,"文章内容"+body);
        Util.d(TAG,"开始标记词性");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
            long start = System.currentTimeMillis();
            mTaggedContent = Util.post(body.toString());
            long end = System.currentTimeMillis();
            Util.d(TAG+"报告","网络获取词性耗时"+(end - start)/1000+"秒");
            if (mTaggedContent != null) {
                String[] wordsTagged = mTaggedContent.split(" ");
                for (String word : wordsTagged) {
                    String[] wordlist = word.split("_");
                    WordMap wordMap = new WordMap(wordlist[0], wordlist[1]);
                    articleTagged.add(wordMap);
                }
            }else{
                Util.showToast(mActivity, "从网络获取信息失败！请检查网络");
            }
//            }
//        }).start();
//        本地获取
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
        Util.d(TAG+"报告","单词标记完成");
        return articleTagged;
    }

    /**
     * translate taggedArticle everyone unknow word
     * @param articleTagged
     * @return
     */
    private String translateUnknowWords(ArrayList<WordMap> articleTagged) {
        StringBuilder body = new StringBuilder();
        for (int j = 0; j < articleTagged.size(); j++) {
            String word = articleTagged.get(j).getWord();
            Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
            Matcher matcher = expression.matcher(word);
            //TODO 还需要考虑单词的首字母大小写(暂时都转为小写)
//            String smallWord = word.toLowerCase();
            if(unknownWords.containsKey(word) && matcher.find()){
                unknownWordsNum++;
                //如果词性不存在则返回为空
                String tag = getUnknowWordTag(articleTagged.get(j).getValue());
//              根据词性查找意思(注意暂时为小写)
                String mean = unknownWords.get(word);
                //注意转义字符直接写|会以单个字符分开·
                String [] meaning = unknownWords.get(word).split("\\|");
                //防止为空
                if(meaning.length > 0 && tag != null) {
                    for (int i = 0; i < meaning.length; i++) {
                        //去除\t
                        tag = tag.substring(1);
                        if (meaning[i].contains(tag)) {
                            //拼接的时候以原文单词为主
                            body.append(word + "(" + meaning[i] + ")" + " ");
                        }
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

    /**
     * 获取不认识单词的词性
     * @param value
     * @return
     */
    private String getUnknowWordTag(String value) {
        String tag = null;
        try {
            List<WordTag> wordTagList = DataSupport.where("oldTag = ?",value).find(WordTag.class);
            if (wordTagList.size() >0) {
                tag = wordTagList.get(0).getNewTag();
                Util.d("获取到不认识单词的词性为", tag);
                return tag;
            }
        }catch (Exception w) {
            w.printStackTrace();
        }
        return tag;
    }
    /**
     * 翻译好的文章进行分类（暂无）
     */
    public ArrayList<Article>  getPassages (ArrayList<Article> objPassages,String level,String cataUtily){
        ArrayList<Article> classifiedPassages = new ArrayList<Article>();
        for (int i = 0; i < objPassages.size(); i++) {
            //			测试一下比较方法
//            if(objPassages.get(i).getCatalogy().equalsIgnoreCase(cataUtily) && objPassages.get(i).getLevel().equalsIgnoreCase(level)){
//                //			   用的是add方法不是set方法
//                classifiedPassages.add(objPassages.get(i));
//            }
        }

        return classifiedPassages;
    }

    /**
     *     单词类存储单词和词性或意思
     */
    class WordMap {
        String word;
        String value;

        public WordMap(String word, String value) {
            this.word = word;
            this.value = value;
        }

        public String getWord() {
            return this.word;

        }

        public String getValue() {
            return this.value;
        }
    }
}
