package com.liu.easyenglishupdate.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.BaseAdapter;

import com.liu.easyenglishupdate.adapter.ArticleAdapter;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.VariableMap;
import com.liu.easyenglishupdate.ui.ArticleActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * 获取ChinaDaily文章
 */
public class SpiderChinaDailyArticle implements ISpiderArticle {

    //以后是否能用到
//    final private Pattern contentPat = Pattern.compile("<P>([^<_]*?)</P>", Pattern.MULTILINE|Pattern.DOTALL);


    public SpiderChinaDailyArticle() {

    }


    public String getMessage(String url) {
        String message = "";
        return message;
    }

    @Override
    public ArrayList<String> getUrlList(String catalogy) {
        ArrayList<String> urls = new ArrayList<String>();
        return urls;
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
     * 获取ChinaDaily网站数据
     * @param activity
     * @param adapter
     * @return
     * @throws Exception
     */
    public ArrayList<Article> getChinaDailyArticle(Activity activity, final ArticleAdapter adapter) throws Exception {
        //包含文章对象的列表
        final ArrayList<Article> articles = new ArrayList<>();
        //存储每种类型对应的标题和网址(这里注意要List嵌套，否则就是最后一个类型的文章列表)
        ArrayList<ArrayList<VariableMap>> articleList = new ArrayList<>();
        //获取文章的类型和对应网址
        ArrayList<VariableMap> typeList = getArticleTypeUrl();
        for (int i = 0; i < typeList.size(); i++) {
            VariableMap item = typeList.get(i);
            Util.d("获取到的类型", item.getmVariable1());
            Util.d("获取到类型对应的网址", item.getmVariable2());
            ArrayList<VariableMap> itemMap = getArticleList(item.getmVariable2());
            if(itemMap != null) {
                articleList.add(itemMap);
            }
        }

        if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
                ArrayList<VariableMap> itemObj = articleList.get(i);
                for (int j = 0; j<itemObj.size(); j++) {
                    //控制是否继续加载
                    if(ArticleActivity.IS_CONTINUE_LOAD) {
                        VariableMap url = itemObj.get(j);
                        Util.d("获取到的标题", url.getmVariable1());
                        Util.d("获取到的网址", url.getmVariable2());
                        Article article = getArticle(url.getmVariable2());
                        if (article != null) {
                            articles.add(article);
                        }
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

        return articles;
    }

    /**
     * 获取文章相关信息
     *
     * @param url 获取网页信息两种方式，建立连接之后，response和document
     */
    private Article getArticle(String url) {
        Article article = new Article();
        Document doc = null;
        try {
            Connection connection = Jsoup.connect(url);
            doc = connection.get();
            String titleTag = doc.title();
            String title = titleTag.substring(0, titleTag.length() - 20);
            Util.d(Util.getClassName() + "标题", title);
            article.setTitle(title);
//        System.out.println("*******************文档内容********************");
            Elements content = doc.getElementsByClass("pt30");
//        System.out.println("文档内容大小" + content.size());
            Elements time = doc.getElementsByClass("pt5");
            if (time.toString().length() < 21) {
                time = doc.getElementsByClass("mb15");
            }
//			 System.out.println(time);
            if (time.text().contains("(")) {
                int index = time.text().indexOf(")");
                System.out.println("文档更新时间"
                        + time.text().substring(index + 1, index + 21));
                article.setTime(time.text().substring(index + 1, index + 21));
            } else {
                System.out.println("文档更新时间" + time.text().substring(0, 20));
                article.setTime(time.text().substring(0, 20));
            }

            StringBuffer articleContent = new StringBuffer();
            Elements ChildBody = content.select("p");
            int size = ChildBody.size();
            for (int i = 0; i < size; i++) {
                String perLine = ChildBody.get(i).toString();
                // System.out.println("每一行"+perLine);
                if (perLine.contains("br")) {
                    perLine = perLine.replace("<br><br>", "\n");
                    System.out.println(perLine.substring(3,
                            perLine.length() - 4));
                    articleContent.append(perLine.substring(3,
                            perLine.length() - 4));
                } else {
                    System.out.println(ChildBody.get(i).text());
                    articleContent.append(ChildBody.get(i).text());
                }
            }
            if (TextUtils.isEmpty(articleContent.toString())){
                return null;
            }
            article.setBody(articleContent.toString());
            //其他属性存为默认值
            article.setCatalogy("默认");
            article.setDescription("");
            article.setLevel("first_level");
            article.setDifficultRatio(50);
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
        for (int i = 1; i < urls.size() - 4; i++) {
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
     * 根据网址获取文章列表
     * <p/>
     * 不要抛出异常出现异常返回空继续解析
     */
    private ArrayList<VariableMap> getArticleList(String url) {
        String BaseUrl = url;
        ArrayList<VariableMap> list = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(BaseUrl);
            Document doc = connection.get();
            // 以上两种方式获取网页内容
            String listTitle = doc.title();
            Util.d("列表标题", listTitle);
            Element bodyChild = doc.body();
            // Elements content = doc.getElementsByClass("list5");
            Elements content = doc.getElementsByClass("mb20");
            // 获取主页几大模块
            Elements urls = content.select("a");
            for (int i = 0; i < urls.size(); i++) {
                Element urlName = urls.get(i);
                String title = urlName.text();
                Util.d("文档标题", title);
                // 过滤不符合规范的文章
                if (title.length() > 8) {
                    // //查找第二个"第一个为8所以值大于等于开始截取的字符串
                    //还需要考虑有些网址直接有http暂时不考虑
                    //还有结尾没有htm过滤掉
                    if (!urlName.toString().contains("http") && urlName.toString().contains("htm")) {
                        if (urlName.toString().contains("..")) {
                            String url1 = BaseUrl.substring(0, BaseUrl.length() - 7)
                                    + urlName.toString().substring(11,
                                    urlName.toString().indexOf("\"", 9));
                            Util.d("文档包含。对应网址", url1);
                            VariableMap item = new VariableMap();
                            item.setmVariable1(title);
                            item.setmVariable2(url1);
                            list.add(item);
                        } else {
                            String url1 = BaseUrl
                                    + urlName.toString().substring(9,
                                    urlName.toString().indexOf("\"", 9));
                            Util.d("文档对应网址", url1);
                            VariableMap item = new VariableMap();
                            item.setmVariable1(title);
                            item.setmVariable2(url1);
                            list.add(item);
                        }
                    }
                }
            }

        } catch (Exception w) {
            w.printStackTrace();
            return null;
        }

        return list;
    }
}
