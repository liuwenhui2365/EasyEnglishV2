package com.liu.easyenglishupdate.ui;

import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.example.liu.easyreadenglishupdate.R;

/**
 * pdf解析为字符串
 */
public class PdfParseActivity extends ActionBarActivity {
    private FragmentManager mFragmentManager;
    private PdfListFragment mPdfList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mFragmentManager = getSupportFragmentManager();
        setTitle(R.string.title_activity_pdf_parse);
        if (mPdfList == null){
            mPdfList = new PdfListFragment();
        }
        setFragment(mPdfList);
    }


    public void setFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.article_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        //这里注意不要调用父类方法，否则会出现连续出栈导致数据没有显示
//        super.onBackPressed();
        //不要继续加载
        Fragment fragment = mFragmentManager.findFragmentById(R.id.article_fragment_container);
        if (fragment instanceof PdfListFragment) {
            finish();
        } else {
            mFragmentManager.popBackStack();
        }
    }
}
