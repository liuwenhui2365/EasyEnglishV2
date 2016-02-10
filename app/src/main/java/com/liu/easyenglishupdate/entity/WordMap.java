package com.liu.easyenglishupdate.entity;

/**
 *     单词类存储单词和词性或意思
 */
public class WordMap {
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