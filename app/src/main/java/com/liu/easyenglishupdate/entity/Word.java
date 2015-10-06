package com.liu.easyenglishupdate.entity;

import org.litepal.crud.DataSupport;

/**
 * Word table
 * Created by Administrator on 2015/10/1.
 */
public class Word extends DataSupport {

    private String word;
    /**
     * the meaning of word
     */
    private String meaning;
    /**
     * know or unknow
     */
    private String type;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
