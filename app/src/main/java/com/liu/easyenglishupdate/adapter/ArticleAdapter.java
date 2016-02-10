package com.liu.easyenglishupdate.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;

import java.util.ArrayList;

/**
 * 文章列表适配器
 * Created by Administrator on 2015/3/19.
 */
public class ArticleAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<Article> mClassifyData = null;
    private long start;
    private long end;

    public ArticleAdapter(Activity context, ArrayList<Article> articleList) {
        this.context = context;
        mClassifyData = articleList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setAdapterData(ArrayList<Article> articles){
        mClassifyData = articles;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mClassifyData.size();
    }

    @Override
    public Article getItem(int position)
    {
        return mClassifyData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.fragment_article_listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.mTxtUpdateTime = (TextView) convertView
                    .findViewById(R.id.date);
            viewHolder.mTxtTitle = (TextView) convertView
                    .findViewById(R.id.title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Article message = getItem(position);
        String title = message.getTitle();
        viewHolder.mTxtTitle.setText(title);
        viewHolder.mTxtUpdateTime.setText(message.getTime());

        return convertView;
    }

    public class ViewHolder {
        /**
         * 标题
         */
        TextView mTxtTitle;
        /**
         * 更新时间
         */
        TextView mTxtUpdateTime;
        Button button;
    }

}
