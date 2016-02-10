package com.liu.easyenglishupdate.util;

import android.app.Activity;

import com.liu.easyenglishupdate.adapter.ArticleAdapter;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.VariableMap;
import com.liu.easyenglishupdate.ui.ArticleActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 获取Android技术中文文章
 * 分别来自CSDN博客
 */
public class SpiderAndroidArticle implements ISpiderArticle {

    public SpiderAndroidArticle() {

    }

    @Override
    public ArrayList<String> getUrlList(String catalogy) {
        ArrayList<String> urls = new ArrayList<String>();
        return urls;
    }

    @Override
    public String getMessage(String url) throws IOException {
        return null;
    }

    @Override
    public String getTitle(String message) {
        String title = null;
        return title;
    }

    @Override
    public StringBuilder getContent(String message) {
        StringBuilder content = new StringBuilder();
        return content;
    }

    @Override
    public String getTime(String message) {
        String time = null;
        return time;
    }

    @Override
    public String getCatalogy(String message) {
        String catalogy = "null";
        return catalogy;
    }

    /**
     * 获取Android网站数据
     *
     * @param activity
     * @param adapter
     * @param index    页码值  0为不需要分页
     * @return 文章列表
     * @throws Exception
     */
    public static ArrayList<Article> getAndroidArticle(Activity activity, final ArticleAdapter adapter, int index, String articleUrl) throws Exception {
        //包含文章对象的列表
        final ArrayList<Article> articles = new ArrayList<>();
        //根据网址进行相应的处理
        if (index != 0) {
            //根据网址类型判断
            String url = "";
            String pageUrl = null;
            if (articleUrl.contains("1369150")) {
                //精华教程
                url = "http://blog.csdn.net/sinyu890807/article/category/1369150";
                pageUrl = getPageUrl(url, index);
            } else if (articleUrl.contains("1399638")) {
                //疑难解答
                url = "http://blog.csdn.net/sinyu890807/article/category/1399638";
                pageUrl = getPageUrl(url, index);
            }
            Util.d("获取到第" + index + "页网址", pageUrl);
            if (pageUrl != null) {
                String baseUrl = "http://blog.csdn.net/";
                ArrayList<VariableMap> articleList = getCSDNArticleList(baseUrl, pageUrl);
                Util.d("获取到第" + index + "页", articleList.size() + "条");
                if (articleList != null && articleList.size() > 0) {
                    for (int i = 0; i < articleList.size(); i++) {
                        //设置标志位控制是否继续加载
                        if (ArticleActivity.IS_CONTINUE_LOAD) {
                            VariableMap urlMap = articleList.get(i);
//                VariableMap url = articleList.get(0);
                            Util.d("获取到的标题", urlMap.getmVariable1());
                            Util.d("获取到的网址", urlMap.getmVariable2());
                            Article article = getArticle(urlMap.getmVariable2(),"title");
                            if (article != null && !articles.contains(article)) {
                                articles.add(article);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (adapter != null) {
                                            adapter.setAdapterData(articles);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        } else {
            ArrayList<VariableMap> articleList= null;
            //不需要分页的
            if (articleUrl.contains("codekk")) {
                //源码分析
                articleList = getSrcArticleList();
            } else if (articleUrl.contains("luoshengyang")) {
                //获取CSDN 老罗的
                String baseUrl = "http://blog.csdn.net/";
                String url = "http://blog.csdn.net/luoshengyang/article/details/8923485";
                articleList = getCSDNArticleList(baseUrl,url);
            }

            if (articleList != null && articleList.size() > 0) {
                for (int i = 0; i < articleList.size(); i++) {
                    //设置标志位控制是否继续加载
                    if (ArticleActivity.IS_CONTINUE_LOAD) {
                        VariableMap url = articleList.get(i);
//                VariableMap url = articleList.get(0);
                        Util.d("获取到的标题", url.getmVariable1());
                        Util.d("获取到的网址", url.getmVariable2());
                        Article article = getArticle(url.getmVariable2(),url.getmVariable1());
                        if (article != null && !articles.contains(article)) {
                            articles.add(article);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        adapter.setAdapterData(articles);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        return articles;
    }

    /**
     * 获取文章相关信息
     *
     * @param url 获取网页信息两种方式，建立连接之后，response和document
     */
    private static Article getArticle(String url,String title) {
        //试着采用网页直接的方式将其显示出来
        Article article = new Article();
//        url = url.replace(" ",Jsoup.parse("&nbsp;").text());
        Connection connection = Jsoup.connect(url);
        try {
            Document doc = connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0").timeout(5000).get();
            Util.d("文档标题", doc.title());
            article.setTitle(doc.title());
            article.setUrl(url);
            //采用第三方包进行过滤
            String body = doc.body().toString();
            String bodyClean = Jsoup.clean(body, Whitelist.basicWithImages());
            article.setBody(bodyClean);
            article.setTime("");

            //区别与其他显示方式
            article.setCatalogy("android");
        } catch (IOException e) {
            e.printStackTrace();
            Util.d("获取文章失败",e.getMessage());
            // TODO 暂时这样处理
            article.setUrl(url);
            article.setTitle(title);
//            return null;
            return article;
        } catch (Exception e) {
            e.printStackTrace();
            Util.d("获取文章失败", e.getMessage());
            return null;
        }
        return article;
    }

    /**
     * 根据索引值获取每页对应的网址
     * 从1开始
     */
    private static String getPageUrl(String baseUrl, int i) {
        String url = "";
        switch (i) {
            case 1:
                url = baseUrl;
                break;
            case 2:
                url = baseUrl + "/2";
                break;
            case 3:
                url = baseUrl + "/3";
                break;
            default:
                url = null;
                break;
        }
        return url;
    }

    /**
     * 根据网址获取源码分析文章列表
     * <p/>
     * 不要抛出异常出现异常返回空继续解析
     */
    private static ArrayList<VariableMap> getSrcArticleList() {
        String BaseUrl = "http://a.codekk.com/";
        ArrayList<VariableMap> urlsList = new ArrayList<>();
        Connection connection = Jsoup
                .connect("http://a.codekk.com/");
        Document doc = null;
        try {
            doc = connection.get();
            Elements urls = doc.body().getElementsByClass("row").select("a");
            for (int j = 0; j < urls.size(); j++) {
                Element urlItem = urls.get(j);
                String url = urlItem.attr("href");
                String urlName = urlItem.text();
                if (urlName.length() > 0) {
//                    Util.d("标题", urlName.text());
//                    Util.d("网址", url);
                    if (!url.contains("http")) {
                        url = BaseUrl + url;
                    }
                    VariableMap map = new VariableMap(urlName, url);
                    urlsList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.d("获取技术文章列表", "失败");
        }

        return urlsList;
    }

    /**
     * 获取CSDN博客文章列表
     * <p/>
     * 不要抛出异常出现异常返回空继续解析
     */
    private static ArrayList<VariableMap> getCSDNArticleList(String baseUrl, String articleUrl) {
        ArrayList<VariableMap> urlsList = new ArrayList<>();
        Connection connection = Jsoup
                .connect(articleUrl);
        Document doc = null;
        try {
            //CSDN获取时需要添加头信息
            doc = connection.userAgent(
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            Elements urls = doc.body().getElementsByClass("main").select("a");
            for (int j = 0; j < urls.size(); j++) {
                Element urlItem = urls.get(j);
                String url = urlItem.attr("href");
                String urlName = urlItem.text();
                if (urlName.length() > 0 && urlName.contains("Android")
                        || urlName.contains("Chromium") || urlName.contains("Dalvik")) {
//                    Util.d("标题", urlurlName.text());
//                    Util.d("网址", url);
                    if (!url.contains("http")) {
                        url = baseUrl + url;
                    }
                    VariableMap map = new VariableMap(urlName, url);
                    urlsList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.d("获取技术文章列表", "失败");
        }

        return urlsList;
    }
}
