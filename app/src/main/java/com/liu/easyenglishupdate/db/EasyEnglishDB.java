package com.liu.easyenglishupdate.db;
import android.database.sqlite.SQLiteDatabase;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.entity.WordTag;
import com.liu.easyenglishupdate.util.Util;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * EasyEnglishDB
 * Created by Administrator on 2015/9/30.
 */
public class EasyEnglishDB{
    /**
     * save all words data
     *
     * @return  true is success  otherise
     */
    public static boolean saveAllWords(InputStream ins) throws IOException {
        BufferedReader reader;
        InputStream inputStream = ins;
        long start=0,end;
        if(inputStream != null) {
            SQLiteDatabase db = Connector.getDatabase();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            start = System.currentTimeMillis();
            Util.d("EasyEnglishDB中单词开始行数", DataSupport.count(Word.class)+"");
            db.beginTransaction();
            while ((line = reader.readLine()) != null) {
                String[] wordMeaning = line.split(":");
                // 有些单词没有意思,有些只有意思
                if (wordMeaning.length == 2) {
                    Word word = new Word();
                    word.setWord(wordMeaning[0]);
                    word.setMeaning(wordMeaning[1]);
                    word.setType("know");
                    word.save();
                }
            }
            end = System.currentTimeMillis();
            //不调用此方法数据就没有写入表中
            db.setTransactionSuccessful();
            //结束事务
            db.endTransaction();
            Util.d("EasyEnglishDB中单词结束行数", DataSupport.count(Word.class) + "");
            Util.d("EasyEnglishDB中单词耗时", (end - start) / 1000 + "秒");
            int recordNum = DataSupport.count(Word.class);
            inputStream.close();

            if(db != null){
                db.close();
            }

            if (reader != null){
                reader.close();
            }

            if(recordNum > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * save all wordsTag data
     *
     * @return  true is success  otherise
     */
    public static boolean saveAllWordsTag(InputStream ins) throws IOException {
        BufferedReader reader;
        InputStream inputStream = ins;
        long start=0,end;
        if(inputStream != null) {
            SQLiteDatabase db = Connector.getDatabase();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            start = System.currentTimeMillis();
            Util.d("EasyEnglishDB中单词词性开始行数", DataSupport.count(WordTag.class)+"");
            db.beginTransaction();
            while ((line = reader.readLine()) != null) {
                String[] wordTagArray = line.split(" ");
                // 防止出现不符合规范的
                if (wordTagArray.length == 2) {
                    WordTag wordTag = new WordTag();
                    wordTag.setOldTag(wordTagArray[0]);
                    wordTag.setNewTag(wordTagArray[1]);
                    wordTag.save();
                }
            }
            end = System.currentTimeMillis();
            //不调用此方法数据就没有写入表中
            db.setTransactionSuccessful();
            //结束事务
            db.endTransaction();
            Util.d("EasyEnglishDB中单词词性结束行数", DataSupport.count(WordTag.class) + "");
            Util.d("EasyEnglishDB中单词词性耗时", (end - start) / 1000 + "秒");
            int recordNum = DataSupport.count(WordTag.class);
            inputStream.close();

            if(db != null){
                db.close();
            }

            if (reader != null){
                reader.close();
            }

            if(recordNum > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 修改单词的类型(以后考虑通过String文件获取)
     * @param word
     * @return
     */
    public static String modifyWordType(String word) {
        List<Word> wordlist = DataSupport.where("word = ?",word).find(Word.class);
        Word wordTemp = wordlist.get(0);
        String type = wordTemp.getType();
        if ("know".equals(type)){
            wordTemp.setType("unknow");
            wordTemp.updateAll("word = ?", word);
        }else{
            wordTemp.setType("know");
            wordTemp.updateAll("word = ?",word);
        }
        return wordTemp.getType();
    }
}
