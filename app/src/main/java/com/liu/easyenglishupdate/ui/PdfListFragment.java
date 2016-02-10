package com.liu.easyenglishupdate.ui;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.adapter.ArticleAdapter;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.Util;
import com.liu.easyenglishupdate.view.AutoListView;

import java.io.File;
import java.util.ArrayList;

/**
 * pdf文件显示页面
 */
public class PdfListFragment extends Fragment {
    /**
     * 上下文
     */
    private Activity mActivity;
    /**
     * 适配器
     */
    private ArticleAdapter mArticleAdapter;
    /**
     * 适配器数据源
     */
    private ArrayList<Article> mArticleList;
    /**
     * 跳转的下一个页面
     */
    private PdfMessageFragment mPdfMessage;
    /**
     * 跳转的下一个页面
     */
    private WebFragment mWeb;
    /**
     * 上下拉控件
     */
    private AutoListView mListView;
    /**
     * 提示控件
     */
    private TextView mTxtRemind;
    //当前页
    private int mPage;

    private int refreshIndex = 0;
    /**
     * 开始加载的索引值
     */
    private int loadIndex = 0;
    //  每个页面显示的行数
    private int perReadNum = 6;
    private int articleNum = 0;
    private String type = null;
    View footer, header;// 底顶部布局
    LayoutInflater inflater = null;
    /**
     * 当前页面View
     */
    private View mView;
    //  获取网络状态
    private IntentFilter intentFilter;

    /**
     * pdf文件列表
     * @param savedInstanceState
     */
    private ArrayList<File> fileList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化适配器数据
        mArticleList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        //TODO 布局重用造成每次需要移除子View
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_pdf_list, null);
        } else {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
        return mView;
    }

    //如果View为空则不会执行下面的方法
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        mListView = (AutoListView) view.findViewById(R.id.article_list);
        //不允许刷新
        mListView.setIsRefresh(false);
        //适配器
        mArticleAdapter = new ArticleAdapter(getActivity(), mArticleList);
        //获取文件的列表
        fileList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //如果数据不为空
                if (mArticleList.size() == 0) {
                    Util.getPdfList(Environment.getExternalStorageDirectory(), fileList);
                    for (int i = 0; i < fileList.size(); i++) {
                        Article article = new Article();
                        File file = fileList.get(i);
                        article.setTitle(file.getName());
                        article.setUrl(file.getAbsolutePath());
                        article.setTime(Util.getCurrentTime());
                        mArticleList.add(article);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mArticleAdapter.setAdapterData(mArticleList);
                        }
                    });
                }
            }
        }).start();
        mListView.setAdapter(mArticleAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = mArticleList.get(position);
                //如果article为空则下面代码不执行了
                Bundle toMessage = new Bundle();
                toMessage.putSerializable(Util.SHIFT_FLAG, article);
                if (mPdfMessage == null) {
                    mPdfMessage = new PdfMessageFragment();
                }
                mPdfMessage.setArguments(toMessage);
                ((PdfParseActivity) mActivity).setFragment(mPdfMessage);
            }
        });

        //测试网页数据
        mTxtRemind = (TextView) view.findViewById(R.id.tv_remind);
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setText(type);
    }

}


