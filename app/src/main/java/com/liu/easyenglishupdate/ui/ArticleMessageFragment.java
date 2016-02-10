package com.liu.easyenglishupdate.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.MyURLSpan;
import com.liu.easyenglishupdate.util.Translate;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ArticleMessageFragment extends Fragment {
    private final String TAG = ArticleMessageFragment.class.getSimpleName();
    /**
     * 文章对象
     */
    private Article mArticle;
    private TextView textView = null;
    /**
     * 存储文章内容
     */
    private SpannableString mss = null;
    /**
     * 显示文章标题和时间的控件
     */
    private TextView mTxtDate = null;
    /**
     * 显示文章内容的控件
     */
    private TextView mTxtContent = null;
    /**
     * 文章的标题
     */
    private String title = null;
    /**
     * 复用View
     */
    private View mView;
    private int start;
    private int end;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_message, null);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //获取屏幕宽度用来做网页图片宽度

        Bundle bundle = getArguments();
        title = bundle.getString(getActivity().getString(R.string.message));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        final String time = simpleDateFormat.format(curDate);
        mTxtDate = (TextView) view.findViewById(R.id.date_message);
        mTxtDate.setText(time);
        textView = (TextView) view.findViewById(R.id.title_message);
        textView.setText(title);
        mTxtContent = (TextView) view.findViewById(R.id.content_message);
        // 特别注意是LinkMovementMehond方法获取实例，否则点击无响应
        mTxtContent.setMovementMethod(LinkMovementMethod.getInstance());
        //从列表跳转过来
        final Article article = (Article) bundle.getSerializable(Util.SHIFT_FLAG);
        if (article != null) {
            title = article.getTitle();
            textView.setText(title);

            final String content = article.getBody();
            if ("android".equals(article.getCatalogy())) {
                //处理网页图片，不要在这里开启线程，结果发不回去
                final Html.ImageGetter imageGetter = new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(final String imgUrl) {
                        Drawable d = null;
                        Util.d("获取到图片网址",imgUrl);
                        try {
                            d = Drawable.createFromStream(new URL(imgUrl).openStream(), "image.jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //宽度设置为充满全屏图片不显示
                        d.setBounds(1, 1, d.getIntrinsicWidth(), d.getIntrinsicHeight());

                        return d;
                    }
                };

                final Html.TagHandler tagHandler = new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//                        Util.d("获取到标签", tag);
//                        Util.d("获取到网页长度",output.length()+"");
                    }

                };
                //开启线程解析网页数据下载图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //先翻译
                        article.setTime(time);
                        long start = System.currentTimeMillis();
                        Translate translate = new Translate(getActivity());
//                        translate.translate(article);
                        Document document = Jsoup.parse(content);
                        Elements pElements = document.select("p");
                        for (int i = 0; i < pElements.size(); i++) {
                            Element pElement = pElements.get(i);
                            String itemText = pElement.text();
                            Util.d("获取翻译前的句子",itemText);
                            //进行翻译(因为用户不认识的单词一直在变化)
                            String itemTextTag = translate.translateSentence(itemText);
                            Util.d("获取翻译后的句子",itemTextTag);
                            pElement.text(itemTextTag);
                        }
                        long end = System.currentTimeMillis();
                        Util.d(TAG + "报告", "翻译结束耗时" + (end - start) / 10000 + "秒");
                        start = System.currentTimeMillis();
                        Spanned html = Html.fromHtml(document.toString(), imageGetter, tagHandler);
                        end = System.currentTimeMillis();
                        Util.d(TAG + "报告", "解析网页结束耗时" + (end - start) / 10000 + "秒");
                        mss = new SpannableString(html);
                        //TODO 想想为什么后面的两个类的字符串长度异常大
                        Util.d(TAG + "报告", "获取待处理Str大小为" + mss.length());
                        MyURLSpan.setStringLink(new MyURLSpan.StringLink() {
//                            @Override
                            public void setLink(int start, int end, String clickStr) {
                                mss.setSpan(new MyURLSpan(getActivity(), clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//                              不再使用这种方法，显示速度太慢，该方法被频繁调用导致UI显示缓慢，20000字符串长度需要1分半左右
//                               getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //必须确保每一次调用控件操作在UI线程上
//                                        Util.d("显示文章内容报告","当前显示长度"+mss.length());
//                                        mTxtContent.setText(mss);
//                                    }
//
//                                });
                            }
                        });
                        MyURLSpan.handlerStr(mss.toString());
                        //把字符串标记好再显示到UI，提高显示速度，一般二十秒左右足矣
                        Message message = handler.obtainMessage();
                        message.what = 2;
                        message.obj = mss;
                        handler.sendMessage(message);
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //标记词性
                        String contentTag = Util.post(content);
                        article.setBody(contentTag);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = article;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        }
    }

    /**
     * 从数据库读取并翻译
     */

    public void readArticle(String title) {
        try {
            Util.d(TAG + "数据库获取到的标题", title);
            int num = DataSupport.count(Article.class);
            List<Article> articleList = DataSupport.where("title = ?", title).find(Article.class);
            if (articleList.size() > 0) {
                mArticle = articleList.get(0);
                String time = mArticle.getTime();
                String catalogy = mArticle.getCatalogy();
                String sBody = mArticle.getBody();
                mTxtDate.setText(time);
                Util.d(TAG + "提示", "开始翻译啦");
                final long start = System.currentTimeMillis();
//                article = translate.translate(article);
//                long end = System.currentTimeMillis();
                Util.d(TAG + "报告", "翻译结束耗时" + (end - start) / 10000 + "秒");
//                final String str = article.getBody().toString();
                //            选中每个单词,开启线程，传入的字符串不能为空
//                if (!TextUtils.isEmpty(str)) {
                if (mArticle != null && getActivity() != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Translate translate = new Translate(getActivity());
                            mArticle = translate.translate(mArticle);
                            long end = System.currentTimeMillis();
                            Util.d(TAG + "报告", "翻译结束耗时" + (end - start) / 10000 + "秒");
                            final String str = mArticle.getBody().toString();
                            mss = new SpannableString(str);
                            //TODO 想想为什么后面的两个类的字符串长度异常大
                            Util.d(TAG + "报告", "传入处理Str传入之前大小为" + str.length());
                            MyURLSpan.setStringLink(new MyURLSpan.StringLink() {
                                @Override
                                public void setLink(int start, int end, String clickStr) {
                                    mss.setSpan(new MyURLSpan(getActivity(), clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    // 注意如果点击事件没有响应看是不是赋值出错了(切换页面太快可能会出现空指针)
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //必须确保每一次调用控件操作在UI线程上
                                            mTxtContent.setText(mss);
                                        }

                                    });
                                }
                            });
                            MyURLSpan.handlerStr(str);
                        }
                    }).start();
                } else {
                    Util.showToast(getActivity(), R.string.content_empty);
                }
            } else {
                Util.showToast(getActivity(), R.string.article_empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.e(TAG + "翻译时", "发生异常");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Article articleTag = (Article) msg.obj;
                articleTag.save();
                //      单词分类优化到翻译类里面从pdf解析跳转过来
                //如果没有该文章则会去开启线程标记并保存
                readArticle(articleTag.getTitle());
            }else if (msg.what == 2){
                //解析网页数据结果
                Spanned html = (SpannableString)msg.obj;
                Util.d("handler获取到结果数据length",html.length()+"");
//                Util.d("获取到截取索引值",start+":"+end);
                //不能tostring否则会没有图片显示
//                CharSequence result = html.subSequence(1105,end);
                mTxtContent.setText(html);

            }
        }
    };
}
