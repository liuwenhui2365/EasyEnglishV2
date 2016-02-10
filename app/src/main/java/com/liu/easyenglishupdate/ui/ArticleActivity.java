package com.liu.easyenglishupdate.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.SpiderAndroidNewsArticle;

/**
 * 文章列表和文章详情页面
 */
public class ArticleActivity extends ActionBarActivity {
    /**
     * 是否继续加载
     */
    public static boolean IS_CONTINUE_LOAD = true;

    private FragmentManager mFragmentManager;
    private ArticleListFragment mArticleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentManager = getSupportFragmentManager();
        if (mArticleList == null) {
            mArticleList = new ArticleListFragment();
        }
        setFragment(mArticleList);
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.article_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        //这里注意不要调用父类方法，否则会出现连续出栈导致数据没有显示
//        super.onBackPressed();
        //不要继续加载
        IS_CONTINUE_LOAD = false;
        Fragment fragment = mFragmentManager.findFragmentById(R.id.article_fragment_container);
        if (fragment instanceof ArticleListFragment) {
            finish();
        } else {
            mFragmentManager.popBackStack();
        }
    }
}
