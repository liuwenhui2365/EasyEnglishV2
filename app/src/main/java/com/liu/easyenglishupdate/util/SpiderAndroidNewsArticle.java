package com.liu.easyenglishupdate.util;

import android.app.Activity;
import android.text.TextUtils;

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
 * 获取Android英文网站文章
 */
public class SpiderAndroidNewsArticle implements ISpiderArticle {

    public SpiderAndroidNewsArticle() {

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
     * @param index    页码值
     * @return 文章列表
     * @throws Exception
     */
    public ArrayList<Article> getAndroidArticle(Activity activity, final ArticleAdapter adapter, int index) throws Exception {
        //包含文章对象的列表
        final ArrayList<Article> articles = new ArrayList<>();
        String pageUrl = getPageUrl(index);
        Util.d("获取到第" + index + "页网址", pageUrl);
        if (pageUrl != null) {
            ArrayList<VariableMap> articleList = getArticleList(pageUrl);
            Util.d("获取到第" + index + "页", articleList.size()+"条");
            if (articleList != null && articleList.size() > 0) {
                for (int i = 0; i < articleList.size(); i++) {
                    //设置标志位控制是否继续加载
                    if (ArticleActivity.IS_CONTINUE_LOAD) {
                        VariableMap url = articleList.get(i);
//                VariableMap url = articleList.get(0);
                        Util.d("获取到的标题", url.getmVariable1());
                        Util.d("获取到的网址", url.getmVariable2());
                        Article article = getArticle(url.getmVariable2());
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
    private Article getArticle(String url) {
        //试着采用网页直接的方式将其显示出来
        Article article = new Article();
        Connection connection = Jsoup.connect(url);
        try {
            Document doc = connection.get();
            Elements title = doc.getElementsByClass("entry-title");
            Util.d("文档标题", title.text());
            article.setTitle(title.text());
            //获取p标签里面的内容进行词性标记
            long start = System.currentTimeMillis();
            Elements pElements = doc.body().select("p");
            for (int i = 0; i < pElements.size(); i++) {
                Element pElement = pElements.get(i);
                String itemText = pElement.text();
                String itemTextTag = Util.post(itemText);
                pElement.text(itemTextTag);
            }
            long end = System.currentTimeMillis();
            Util.d("获取Android英文文章报告", "标记词性结束耗时" + (end - start) / 10000 + "秒");
            //采用第三方包进行过滤
            String body = doc.body().toString();
            String bodyClean = Jsoup.clean(body, Whitelist.basicWithImages());
            article.setBody(bodyClean);

            //内容将网页源码存储
//            article.setBody(doc.select("main").toString());
            article.setTime("");
            //区别与其他显示方式
            article.setCatalogy("android");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return article;
    }

    /**
     * 根据子网页网址解析，根据主地址处理不方便
     *
     * @return
     * @throws Exception
     */
    private ArrayList<VariableMap> getArticleTypeUrl() throws Exception {
        //出现类没有声明
//        ArrayMap<String, String> urlsMap = new ArrayMap<String, String>();
        ArrayList<VariableMap> urlsMap = new ArrayList<>();
        Connection connection = Jsoup
                .connect("http://www.chinadaily.com.cn/travel/");
        Document doc = connection.get();
        Elements content = doc.getElementsByClass("dropdown");
        // 获取主页几大模块
        Elements urls = content.select("a");
        for (int i = 0; i < urls.size() - 4; i++) {
            Element urlName = urls.get(i);
            // 网址最后一个引号
            int lastIndex = urlName.toString().indexOf("\"", 9);
            // System.out.println("类型" + urlName.text());
            // 查找第二个"第一个为8所以值大于等于开始截取的字符串
            String url = urlName.toString().substring(9, lastIndex);
            // System.out.println("网址" + url);
            VariableMap item = new VariableMap();
            item.setmVariable1(urlName.text());
            item.setmVariable2(url);
            urlsMap.add(item);
        }

        return urlsMap;
    }

    /**
     * 根据索引值获取每页对应的网址
     * 从1开始
     */
    private String getPageUrl(int i) {
        String BaseUrl = "https://www.androidpit.com";
        String firstPage = "https://www.androidpit.com/news";
        Connection connection = Jsoup
                .connect(firstPage);
        Document doc = null;
        try {
            if (i != 1) {
                // 获取新闻每一页对应的网址
                Elements pages = doc.getElementsByClass("pages");
                // 获取第二页网址
                Element pageUrls = pages.select("a").get(0);
                String pageUrl = BaseUrl + pageUrls.attr("href");
                return pageUrl.replace("2", String.valueOf(i));
            } else {
                return firstPage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.d("获取文章每页网址", "fail");
        }
        return null;
    }

    /**
     * 根据网址获取文章列表
     * <p/>
     * 不要抛出异常出现异常返回空继续解析
     */
    private ArrayList<VariableMap> getArticleList(String pageurl) {
        String BaseUrl = "https://www.androidpit.com";
        ArrayList<VariableMap> urlsList = new ArrayList<>();
        Connection connection = Jsoup
                .connect(pageurl);
        Document doc = null;
        try {
            doc = connection.get();
            Elements content = doc.getElementsByClass("mainContent");
            Elements urls = content.select("a");
            for (int j = 0; j < urls.size(); j++) {
                Element urlItem = urls.get(j);
                Elements urlName = urlItem.getElementsByClass(
                        "articleTeaserContent").select("h2");
                String url = BaseUrl + urlItem.attr("href");
                if (urlName.text().length() > 0) {
//                    Util.d("标题", urlName.text());
//                    Util.d("网址", url);
                    VariableMap map = new VariableMap(urlName.text(), url);
                    urlsList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.d("获取文章列表", "失败");
        }

        return urlsList;
    }
}
