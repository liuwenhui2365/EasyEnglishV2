package com.wenhuiliu.EasyEnglishReading;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 获取文章链表并解析
 * Created by Administrator on 2015/3/4.
 */
public interface ISpiderArticle {

    /**
     * 根据分类获取含文章列表的网址
     * @return 包含多篇文章的具体网址
     */
    public ArrayList<String> getUrlList(String catalogy);

    /**
     * 根据获取到的网址读取页面内容
     * @param url
     * @return  页面内容
     */
    public String getMessage(String url) throws IOException;

    /**
     * 根据提供网页内容获取文章的标题
     * @param message 网页内容
     * @return  文章标题
     */
    public String getTitle(String message);

    /**
     * 根据提供网页内容获取文章的内容
     * @param message 网页内容
     * @return 文章内容
     */
    public StringBuilder getContent(String message);

    /**
     * 根据提供网页内容获取文章的时间
     * @param message 网页内容
     * @return 写文章时间
     */
    public String getTime(String message);

    /**
     * 根据提供网页内容获取文章的分类
     * @param message 网页内容
     * @return  文章类别
     */
    public String getCatalogy(String message);
}
