package com.liu.easyenglishupdate.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.ClassifyEnglishMessage;

import java.util.ArrayList;

/**
 * 英语分类和资讯分类页面适配器
 * Created by Administrator on 2015/10/7.
 */
public class ClassifyAdapter extends BaseAdapter {
    private ArrayList<ClassifyEnglishMessage> mClassifyData;
    private ViewHolder mViewHolder;
    private Activity mActivity;

    public ClassifyAdapter(Activity activity,ArrayList<ClassifyEnglishMessage> mClassifyData) {
        this.mClassifyData = mClassifyData;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mClassifyData.size();
    }

    @Override
    public ClassifyEnglishMessage getItem(int position) {
        return mClassifyData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView!= null){
            mViewHolder = (ViewHolder)convertView.getTag();
        }else{
            mViewHolder = new ViewHolder();
            convertView = mActivity.getLayoutInflater().inflate(R.layout.fragment_classify_item,null);
            mViewHolder.mTxtClassifyName = (TextView)convertView.findViewById(R.id.classify_item_name);
//            mViewHolder.mImgClassify = (ImageView)convertView.findViewById(R.id.classify_item_pic);
            convertView.setTag(mViewHolder);
        }

        mViewHolder.mTxtClassifyName.setText(getItem(position).getmName());
        mViewHolder.mTxtClassifyName.setBackgroundColor(getItem(position).getmDrawPic());
//        mViewHolder.mImgClassify.setImageResource(getItem(position).getmDrawPic());

        return convertView;
    }

    class ViewHolder{
//        private ImageView mImgClassify;
        private TextView mTxtClassifyName;
    }
}
