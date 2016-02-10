package com.liu.easyenglishupdate.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.adapter.WordAdapter;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.util.Util;
import com.liu.easyenglishupdate.view.CustomListView;
import com.liu.easyenglishupdate.view.SideBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词及意思显示页面
 * 实现上拉加载更多
 */
public class WordClassifyFragment extends Fragment implements CustomListView.OnLoaderListener {

    private List<Word> wordsList = new ArrayList<>();
    private CustomListView mCustomListView = null;
    private EditText mEdtSearch = null;
    private TextView textView = null;
    /**
     * 屏幕中间显示字母控件
     */
    private TextView mTxtCharDisplay = null;
    private LinearLayout linearLayout = null;
    private Button mTogBtn = null;
    private  int wordNum =0;
    private int loadIndex = 0;
    private int perReadNum = 50;
    private SideBar mSidebar;
    private WordAdapter mWordAdapter;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //布局复用会造成切换fragment异常，提示移除子View
        if(mView == null){
            mView = inflater.inflate(R.layout.fragment_word_classify,null);
        }else{
            //把之前的View移除掉即可
            ((ViewGroup)mView.getParent()).removeView(mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setFragmentVisiblity(View.VISIBLE);
        mEdtSearch = (EditText)view.findViewById(R.id.word_search);
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //根据用户输入的信息进行数据过滤
//                String str = s.toString();
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxtCharDisplay = (TextView)view.findViewById(R.id.word_initial_display);
        mTxtCharDisplay.setVisibility(View.GONE);
        mSidebar = (SideBar)view.findViewById(R.id.word_side_bar);
        //设置中间显示字母控件
        mSidebar.setTextView(mTxtCharDisplay);
        //右边滑动事件
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //字母首次出现的位置
                int position = mWordAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mCustomListView.setSelection(position);
                }else{
                    Util.showToast(getActivity(),R.string.no_load_now);
                }

            }
        });


        readWord(loadIndex, perReadNum);

        showWordView(view);
        // 注意顺序，否则在初始化时只能读取一次，只有再进去才能读
        wordNum = DataSupport.count(Word.class);


    }

    /**
     * 过滤数据
     * @param inputText
     */
    private void filterData(String inputText) {
        List<Word> filterDataList = new ArrayList<>();
        if(TextUtils.isEmpty(inputText)){
            filterDataList = wordsList;
        }else{
            filterDataList.clear();
            for (Word word : wordsList){
                String wordStr = word.getWord();
                //根据大写字母的索引值和内容开头比较
                if (wordStr.toUpperCase().indexOf(inputText.toString().toUpperCase())!= -1
                    || wordStr.toUpperCase().startsWith(inputText.toUpperCase())){
                    filterDataList.add(word);
                }
            }
            mWordAdapter.setAdapterData((ArrayList)filterDataList);
        }
    }

    public void showWordView(View view){
        mCustomListView = (CustomListView)view.findViewById(R.id.word_list);
        mCustomListView.setIsRefresh(false);
        mWordAdapter = new WordAdapter(getActivity(), (ArrayList<Word>)wordsList);
//      注意是不为空的时候
        if(!mWordAdapter.isEmpty()){
            mWordAdapter.notifyDataSetChanged();
            mCustomListView.setAdapter(mWordAdapter);
        }

        mCustomListView.setLoaderListener(this);
        mCustomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              这里的View就是ListView中item的 View
                String word = wordsList.get((int) id).getWord();
                String meaning = wordsList.get((int) id).getMeaning();
                Bundle bundle = new Bundle();
                bundle.putString(getActivity().getString(R.string.word_tag), word);
                bundle.putString(getActivity().getString(R.string.meaning_tag), meaning);
                WordMeaning wordMeaning = new WordMeaning();
                wordMeaning.setArguments(bundle);
                ((MainActivity) getActivity()).setFragment(wordMeaning, true);
                ((MainActivity) getActivity()).setFragmentVisiblity(View.GONE);
            }

        });
    }

    public void readWord(int firstIndex, int perReadNum){
        Util.d("上拉加载", "开始获取单词");
        long start = System.currentTimeMillis();
        List wordsListTemp = DataSupport.limit(perReadNum).offset(firstIndex).find(Word.class);
        Util.d("上拉加载", "结束获取获取单词");
        long end = System.currentTimeMillis();
        Util.d("上拉加载", "耗时" + (end - start) / 1000 + "秒");
        wordsList.addAll(wordsListTemp);
//        mWordAdapter.setAdapterData((ArrayList)wordsList);
     }



    /**
     * 上拉加载
     */
    public void onLoad() {
//      考虑索引问题
        loadIndex = loadIndex + perReadNum;
        int fromIndex = loadIndex;
        if(fromIndex >= wordNum)
        {
            //数据加载完成
            fromIndex = wordNum;
        }else if (fromIndex + perReadNum >= wordNum) {
            //最后几条数据
            fromIndex = wordNum - perReadNum;
        }else {
            readWord(fromIndex, perReadNum);
        }

        Util.d("上拉刷新 begin" + fromIndex, "end" + (fromIndex + perReadNum));
        mCustomListView.loadComplete();

    }

    @Override
    public void onReflash() {
        //下拉刷新不用
        mCustomListView.reflashComplete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
