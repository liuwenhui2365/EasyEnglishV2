package com.liu.easyenglishupdate.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.adapter.ArticleAdapter;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.util.SpiderAndroidArticle;
import com.liu.easyenglishupdate.util.SpiderAndroidNewsArticle;
import com.liu.easyenglishupdate.util.SpiderChinaDailyArticle;
import com.liu.easyenglishupdate.util.Util;
import com.liu.easyenglishupdate.view.AutoListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表页面
 * 实现上拉加载更多，下拉刷新获取网络最新
 * ArrayMap支持API19以上，所以采用SuppressLint
 */
@SuppressLint("NewApi")
public class ArticleListFragment extends Fragment implements AutoListView.OnLoadListener {
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
    private ArticleMessageFragment mArticleMessage;
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
     * 用于区分中英文
     * E为英文C为中文
     */
    private String flag;

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
            mView = inflater.inflate(R.layout.fragment_article_list, null);
        }else {
            ((ViewGroup)mView.getParent()).removeView(mView);
        }
        return mView;
    }

    //如果View为空则不会执行下面的方法
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        ArticleActivity.IS_CONTINUE_LOAD = true;
        //判断是否需要显示底部导航
        if (mActivity instanceof MainActivity){
            ((MainActivity)mActivity).setFragmentVisiblity(View.VISIBLE);
        }
//      获取分类
        type = mActivity.getIntent().getStringExtra(Util.SHIFT_FLAG);
        //从fragment的实例中获取
        if (type == null){
            type = getArguments().getString(Util.SHIFT_FLAG);
        }
        if (type != null) {
            if (type.equalsIgnoreCase(getString(R.string.english_middle))) {
                type = "中学英语";
            } else if (type.equalsIgnoreCase(getString(R.string.english_china_daily))) {
                type = getString(R.string.english_china_daily);
                flag = "E";
            } else if (type.equalsIgnoreCase(getString(R.string.already_translate_list))) {
                type = getString(R.string.already_translate_list);
                flag = "E";
            } else if (type.equalsIgnoreCase(getString(R.string.android_news))) {
                type = getString(R.string.android_news);
                flag = "E";
            } else if (type.equalsIgnoreCase(getString(R.string.android_src))) {
                type = getString(R.string.android_src);
                flag = "C";
            } else if (type.equalsIgnoreCase(getString(R.string.android_experience))) {
                type = getString(R.string.android_experience);
                flag = "C";
            } else if (type.equalsIgnoreCase(getString(R.string.android_luo))){
                type = getString(R.string.android_luo);
                flag = "C";
            } else if (type.equalsIgnoreCase(getString(R.string.android_elite))){
                type = getString(R.string.android_elite);
                flag = "C";
            }
        }

        mListView = (AutoListView) view.findViewById(R.id.article_list);
        //不允许刷新
        mListView.setIsRefresh(false);
        mListView.setOnLoadListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = mArticleList.get(position);
                //如果article为空则下面代码不执行了
                Bundle toMessage = new Bundle();
                toMessage.putSerializable(Util.SHIFT_FLAG, article);
                if ("E".equalsIgnoreCase(flag)) {
                    if (mArticleMessage == null) {
                        mArticleMessage = new ArticleMessageFragment();
                    }
                    mArticleMessage.setArguments(toMessage);
                    if (type.equalsIgnoreCase(getString(R.string.already_translate_list))) {
                        //隐藏底部导航
                        ((MainActivity) mActivity).setFragmentVisiblity(View.GONE);
                        ((MainActivity) mActivity).setFragment(mArticleMessage, true);
                    } else {
                        ((ArticleActivity) mActivity).setFragment(mArticleMessage);
                    }
                } else {
                    if (mWeb == null) {
                        mWeb = new WebFragment();
                    }
                    mWeb.setArguments(toMessage);
                    ((ArticleActivity) mActivity).setFragment(mWeb);
                }
            }
        });
        //适配器
        mArticleAdapter = new ArticleAdapter(getActivity(), mArticleList);
        mListView.setAdapter(mArticleAdapter);
        //测试网页数据
        mTxtRemind = (TextView) view.findViewById(R.id.tv_remind);
        TextView textView = (TextView)view.findViewById(R.id.tv_title);
        textView.setText(type);
//      从数据库获取
        if (getString(R.string.already_translate_list).equals(type)) {
            articleNum = DataSupport.count(Article.class);
            mListView.setLoadEnable(false);
            if (articleNum > 0) {
                //读取数据库文章
                readArticle();
            } else {
                Util.showToast(mActivity, R.string.database_empty);
            }
        }else if (getString(R.string.english_china_daily).equals(type)) {
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //处理解析网页异常
                            try {
                                long start = System.currentTimeMillis();
                                getChinaDailyArticleNet();
                                long end = System.currentTimeMillis();
                                int second = (int) (end - start) / 1000;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTxtRemind.setText("一共获取到" + String.valueOf(mArticleList.size()) + "篇文章");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }else if(getString(R.string.android_news).equals(type)) {
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //处理解析网页异常
                            try {
                                long start = System.currentTimeMillis();
                                getAndroidArticleNet();
                                long end = System.currentTimeMillis();
                                int second = (int) (end - start) / 1000;
                                Util.d("获取文章列表耗时",second+"秒");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTxtRemind.setText("一共获取到" + String.valueOf(mArticleList.size()) + "篇文章");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        } else if (getString(R.string.android_luo).equalsIgnoreCase(type)){
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = "http://blog.csdn.net/luoshengyang/article/details/8923485";
                            getArticle(mTxtRemind,url,0);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }else if (getString(R.string.android_elite).equalsIgnoreCase(type)){
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = "http://blog.csdn.net/sinyu890807/article/category/1369150";
                            getArticle(mTxtRemind,url,1);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }else if (getString(R.string.android_experience).equalsIgnoreCase(type)){
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = "http://blog.csdn.net/sinyu890807/article/category/1399638";
                            getArticle(mTxtRemind,url,1);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }else if (getString(R.string.android_src).equalsIgnoreCase(type)){
            //检查网络
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = "http://a.codekk.com/";
                            getArticle(mTxtRemind,url,0);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }
//        mArticleAdapter.notifyDataSetChanged();

    }

    /**
     * 根据网址获取中文文章列表
     * @param textView
     * @param url
     * @param index
     */
    private void getArticle(final TextView textView,String url,int index) {
        //处理解析网页异常
        try {
            long start = System.currentTimeMillis();
            //UI显示获取的个数
            mArticleList = SpiderAndroidArticle.getAndroidArticle(mActivity,mArticleAdapter,index,url);
            long end = System.currentTimeMillis();
            int second = (int) (end - start) / 1000;
            Util.d("获取文章列表耗时",second+"秒");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    textView.setText("一共获取到" + String.valueOf(mArticleList.size()) + "篇文章");
                    textView.setText("一共获取到" + String.valueOf(mArticleAdapter.getCount()) + "篇文章");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readArticle() {
        List<Article> articles = DataSupport.findAll(Article.class);
        mArticleList = (ArrayList) articles;
        mArticleAdapter.setAdapterData(mArticleList);
    }

    public void getChinaDailyArticleNet() {
        SpiderChinaDailyArticle spiderChinaDailyArticle = new SpiderChinaDailyArticle();
        //插入数据(逆序写入保证读取到最新的）
//        这样在多次插入数据再读取的时候会出现顺序不匹配问题
        try {
            mArticleList = spiderChinaDailyArticle.getChinaDailyArticle(getActivity(), mArticleAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Util.getClassName(), "获取ChinaDaily文章异常"+e.toString());
        }
    }

    public void getAndroidArticleNet() {
        SpiderAndroidNewsArticle androidNewsArticle = new SpiderAndroidNewsArticle();
        //插入数据(逆序写入保证读取到最新的）
//        这样在多次插入数据再读取的时候会出现顺序不匹配问题
        try {
            mArticleList = androidNewsArticle.getAndroidArticle(getActivity(), mArticleAdapter,1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Util.getClassName(), "获取AndroidNews文章异常"+e.toString());
        }
    }


    /**
     * 每次读取的索引值
     *
     * @param firstIndex
     * @param perReadNum
     */
    public void readWord(int firstIndex, int perReadNum) {
        Util.d("上拉加载", "开始获取单词");
        long start = System.currentTimeMillis();
        List wordsListTemp = DataSupport.limit(perReadNum).offset(firstIndex).find(Word.class);
        Util.d("上拉加载", "结束获取获取单词");
        long end = System.currentTimeMillis();
        Util.d("上拉加载", "耗时" + (end - start) / 1000 + "秒");
        mArticleList.addAll(wordsListTemp);
//        mWordAdapter.setAdapterData((ArrayList)wordsList);
    }


    /**
     * 上拉加载
     */
    @Override
    public void onLoad() {
        if (getString(R.string.already_translate_list).equalsIgnoreCase(type)) {
//      考虑索引问题
            loadIndex = loadIndex + perReadNum;
            int fromIndex = loadIndex;
            if (fromIndex >= articleNum) {
                //数据加载完成
                fromIndex = articleNum;
            } else if (fromIndex + perReadNum >= articleNum) {
                //最后几条数据
                fromIndex = articleNum - perReadNum;
            } else {
                readWord(fromIndex, perReadNum);
            }

            Util.d("上拉刷新 begin" + fromIndex, "end" + (fromIndex + perReadNum));
//        mCustomListView.loadComplete();
        }else if(getString(R.string.android_experience).equalsIgnoreCase(type)){
            //Android经验分页显示
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mPage++;
                            String url = "http://blog.csdn.net/sinyu890807/article/category/1399638";
                            getArticle(mTxtRemind,url,mPage);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }else if(getString(R.string.android_elite).equalsIgnoreCase(type)){
            //Android经验分页显示
            if (mArticleList.size() == 0) {
                if (Util.isNetConnected(mActivity)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mPage++;
                            String url = "http://blog.csdn.net/sinyu890807/article/category/1369150";
                            getArticle(mTxtRemind,url,mPage);
                        }
                    }).start();
                } else {
                    Util.showToast(mActivity, R.string.net_not_connect);
                }
            }
        }
    }
}


