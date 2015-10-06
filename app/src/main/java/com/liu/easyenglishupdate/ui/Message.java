package com.liu.easyenglishupdate.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.MyURLSpan;
import com.liu.easyenglishupdate.util.Translate;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.Util;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


public class Message extends Fragment{
    private final String TAG  = Message.class.getSimpleName();
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
        Bundle bundle = getArguments();
        title = bundle.getString(getActivity().getString(R.string.message));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);
        mTxtDate = (TextView)view.findViewById(R.id.date_message);
        mTxtDate.setText(time);
        textView = (TextView) view.findViewById(R.id.title_message);
        textView.setText(title);
        mTxtContent = (TextView)view.findViewById(R.id.content_message);
//      单词分类优化到翻译类里面
        readArticle(title);
    }

    /**
     *     从数据库读取并翻译
     */
    public void readArticle(String title) {
        try {
            Util.d(TAG + "数据库获取到的标题", title);
            int num = DataSupport.count(Article.class);
            List<Article> articleList = DataSupport.where("title = ?", title).find(Article.class);
            if(articleList.size() > 0){
                mArticle = articleList.get(0);
                String time = mArticle.getTime();
                String catalogy = mArticle.getCatalogy();
//                StringBuilder sBody = article.getBody();
                String sBody = mArticle.getBody().toString();
//                article = new Article(title, sBody, catalogy);
                mTxtDate.setText(time);
            //测试数据
    //            Article article = new Article(title, new StringBuilder("this is article body"), "test");
                Util.d(TAG + "提示", "开始翻译啦");
                final long start = System.currentTimeMillis();
//                article = translate.translate(article);
//                long end = System.currentTimeMillis();
//                Util.d(TAG + "报告", "翻译结束耗时" + (end - start) / 10000 + "秒");
//                final String str = article.getBody().toString();
    //            final String str = "this【表情】 is article body";
    //            选中每个单词,开启线程，传入的字符串不能为空
//                if (!TextUtils.isEmpty(str)) {
                if (mArticle != null) {
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
                                    //              注意如果点击事件没有响应看是不是赋值出错了
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //必须确保每一次调用控件操作在UI线程上
                                            mTxtContent.setText(mss);
                                            // 特别注意是LinkMovementMehond方法获取实例，否则点击无响应
                                            mTxtContent.setMovementMethod(LinkMovementMethod.getInstance());
                                        }

                                    });
                                }
                            });
                            MyURLSpan.handlerStr(str);
                        }
                    }).start();
                }else{
                    Util.showToast(getActivity(),R.string.content_empty);
                }
            } else {
                Util.showToast(getActivity(), R.string.article_empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.e(TAG + "翻译时", "发生异常");
        }
    }
}
