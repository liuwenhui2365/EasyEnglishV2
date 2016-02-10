package com.liu.easyenglishupdate.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liu.easyreadenglishupdate.R;

/**
 * 分类资讯页面
 * Created by Administrator on 2015/10/7.
 */
public class MessageClassifyFragment extends Fragment {
    /**
     * 当前UI
     */
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_english_message_classify,container,false);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
