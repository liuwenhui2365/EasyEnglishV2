package com.liu.easyenglishupdate.entity;

import org.litepal.crud.DataSupport;

/**
 * 词性对应表
 * 根据编码查找对应的词性
 * Created by Administrator on 2015/10/1.
 */
public class WordTag extends DataSupport {
    /**
     * 词性对应的编码
     */
    private String oldTag;
    /**
     * 词性
     */
    private String newTag;

    public String getOldTag() {
        return oldTag;
    }

    public void setOldTag(String oldTag) {
        this.oldTag = oldTag;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }
}
