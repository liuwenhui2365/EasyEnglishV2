package com.liu.easyenglishupdate.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;

import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;


public class WordMeaning extends Fragment {
    private View mView;
    /**
     * 单词
     */
    private TextView mTxtWord;
    /**
     * 单词意思
     */
    private TextView mTxtMeaning;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_word_meaning, null);
        }
        return mView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle =getArguments();
        mTxtWord = (TextView)view.findViewById(R.id.dis_word);
        mTxtWord.setText(bundle.getString(getActivity().getString(R.string.word_tag)));
        mTxtMeaning = (TextView)view.findViewById(R.id.meaning);
        mTxtMeaning.setText(bundle.getString(getActivity().getString(R.string.meaning_tag)));
    }

}
