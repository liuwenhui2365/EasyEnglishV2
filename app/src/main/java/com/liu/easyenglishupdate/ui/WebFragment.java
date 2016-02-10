package com.liu.easyenglishupdate.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.Util;

/**
 * 分类资讯页面
 * Created by Administrator on 2015/10/7.
 */
public class WebFragment extends Fragment {
    /**
     * 当前UI
     */
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_web,container,false);
        }
        return mView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        Article article = null;
        if (bundle != null){
            article = (Article)bundle.getSerializable(Util.SHIFT_FLAG);
        }

        WebView webView = (WebView)view.findViewById(R.id.web);
        webView.setFitsSystemWindows(true);

        if (article!= null){
            webView.loadUrl(article.getUrl());
        }
    }
}
