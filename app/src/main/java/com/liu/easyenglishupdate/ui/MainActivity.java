package com.liu.easyenglishupdate.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.adapter.ClassifyAdapter;
import com.liu.easyenglishupdate.entity.ClassifyEnglishMessage;
import com.liu.easyenglishupdate.util.Util;

import java.util.ArrayList;

/**
 * 主页及四个底部导航和左侧导航
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    static FragmentManager mFragManager;
    /**
     * 分类英语及资讯页面
     */
    private EnglishClassify mEnglishClassify;
    /**
     * 单词分类页面
     */
    private WordClassifyFragment mWordClassify;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * 底部导航切换
     */
    private static LinearLayout mLytBottom;
    private LinearLayout mLytBottomNav1;
    private LinearLayout mLytBottomNav2;
    private LinearLayout mLytBottomNav3;
    private LinearLayout mLytBottomNav4;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * record first press back time
     */
    private long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragManager = getSupportFragmentManager();
        initView();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * 初始化底部导航布局及设置相应的点击事件
     */
    private void initView() {
        //默认显示第一个页面
        if (mEnglishClassify == null) {
            mEnglishClassify = new EnglishClassify();
        }

        if (!mEnglishClassify.isAdded()) {
            setFragment(mEnglishClassify, false);
        }

        mLytBottom = (LinearLayout) findViewById(R.id.lyt_bottom);
        mLytBottomNav1 = (LinearLayout) findViewById(R.id.lyt_bottom_item1);
        mLytBottomNav1.setSelected(true);
        mLytBottomNav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLytBottomNav1.setSelected(true);
                mLytBottomNav2.setSelected(false);
                mLytBottomNav3.setSelected(false);
                mLytBottomNav4.setSelected(false);
                //分类英语
                EnglishClassify.flag = 1;
                if (mEnglishClassify == null) {
                    mEnglishClassify = new EnglishClassify();
                }
                EnglishClassify.flag = 1;
                if (!mEnglishClassify.isAdded()) {
                    setFragment(mEnglishClassify, false);
                } else {
                    //采用同一个fragment只变化数据
                    mEnglishClassify.initData();
                }
            }
        });
        mLytBottomNav2 = (LinearLayout) findViewById(R.id.lyt_bottom_item2);
        mLytBottomNav2.setSelected(false);
        mLytBottomNav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLytBottomNav1.setSelected(false);
                mLytBottomNav2.setSelected(true);
                mLytBottomNav3.setSelected(false);
                mLytBottomNav4.setSelected(false);

                if (mWordClassify == null) {
                    mWordClassify = new WordClassifyFragment();
                }
                if (!mWordClassify.isAdded()) {
                    setFragment(mWordClassify, false);
                }

            }
        });
        mLytBottomNav3 = (LinearLayout) findViewById(R.id.lyt_bottom_item3);
        mLytBottomNav3.setSelected(false);
        mLytBottomNav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLytBottomNav1.setSelected(false);
                mLytBottomNav2.setSelected(false);
                mLytBottomNav3.setSelected(true);
                mLytBottomNav4.setSelected(false);
                //已翻译列表
                ArticleListFragment articleList = new ArticleListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Util.SHIFT_FLAG, getString(R.string.already_translate_list));
                articleList.setArguments(bundle);
                setFragment(articleList, false);
            }
        });
        mLytBottomNav4 = (LinearLayout) findViewById(R.id.lyt_bottom_item4);
        mLytBottomNav4.setSelected(false);
        mLytBottomNav4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLytBottomNav1.setSelected(false);
                mLytBottomNav2.setSelected(false);
                mLytBottomNav3.setSelected(false);
                mLytBottomNav4.setSelected(true);
                if (mEnglishClassify == null) {
                    mEnglishClassify = new EnglishClassify();
                }
                //报错由于fragment已经激活
//                Bundle bundle = new Bundle();
//                bundle.putInt("flag", 1);
//                mEnglishClassify.setArguments(bundle);
                EnglishClassify.flag = 2;
                //里面的代码不一定会执行
                if (!mEnglishClassify.isAdded()) {
                    setFragment(mEnglishClassify, false);
                } else {
                    mEnglishClassify.initData();
                }
            }
        });
    }


    /**
     * 左侧菜单栏选中事件
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position);
    }

    /**
     * 左侧导航根据位置进行相应的操作
     *
     * @param number
     */
    public void onSectionAttached(int number) {
        Class TargetActivity = null;
        switch (number) {
            case 0: //修改头像
                Intent toChangeInfo = new Intent(this, ChangeInfoActivity.class);
                toChangeInfo.putExtra(Util.SHIFT_FLAG, getString(R.string.change_icon));
                startActivity(toChangeInfo);
                break;
            case 1://修改昵称
                toChangeInfo = new Intent(this, ChangeInfoActivity.class);
                toChangeInfo.putExtra(Util.SHIFT_FLAG, getString(R.string.change_name));
                startActivity(toChangeInfo);
                break;
            case 2:
                mTitle = getString(R.string.title_section1);
                Util.showToast(this, "你点击了第一项");
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                TargetActivity = PdfParseActivity.class;
                Intent to = new Intent(this, TargetActivity);
                startActivity(to);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                Util.showToast(this, "你点击了第三项");

                break;
            case 5:
                mTitle = getString(R.string.title_section4);
                Util.showToast(this, "反馈建议");
                break;
            default:
                break;

        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Util.showToast(this, "你选择了设置");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置fragment的跳转
     *
     * @param back 是否允许返回
     */
    public static void setFragment(Fragment fragment, Boolean back) {
        FragmentTransaction transaction = mFragManager.beginTransaction();
        //防止快速切换导致崩溃
        if (!fragment.isAdded()) {
            transaction.replace(R.id.fragment_container, fragment);
        }
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * 设置底部导航显隐
     *
     * @param visiblity
     */
    public static void setFragmentVisiblity(int visiblity) {
        mLytBottom.setVisibility(visiblity);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EnglishClassify extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        /**
         * 分类页面标题
         */
        private TextView mTxtClassifyTitle;
        /**
         * 分类页面布局
         */
        private GridView mGridClassify;
        /**
         * 分类页面数据
         */
        private ArrayList<ClassifyEnglishMessage> mClassifyData;
        /**
         * 适配器
         */
        private ClassifyAdapter mClassifyAdapter;
        /**
         * start time
         *
         * @param savedInstanceState
         */
        private long startTime;

        /**
         * 跳转标志位
         * 1.分类英语 2.分类资讯
         */
        private static int flag = 0;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_english_message_classify, container, false);
            init(rootView);
            return rootView;
        }

        /**
         * 初始化页面控件和数据
         *
         * @param rootView
         */
        public void init(View rootView) {
            //获取标志位
//            Bundle bundle = getArguments();
//            if (bundle != null) {
//                flag = bundle.getInt("flag");
//            }
            mTxtClassifyTitle = (TextView) rootView.findViewById(R.id.classify_title);
            mGridClassify = (GridView) rootView.findViewById(R.id.classify_grid);
            //适配器
            mClassifyData = new ArrayList<>();
            mClassifyAdapter = new ClassifyAdapter(getActivity(), mClassifyData);
            mGridClassify.setAdapter(mClassifyAdapter);

            mGridClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                     @Override
                                                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                         String name = mClassifyData.get(position).getmName();
                                                         Intent toArticleList = new Intent(getActivity(), ArticleActivity.class);
                                                         toArticleList.putExtra(Util.SHIFT_FLAG, name);
                                                         startActivity(toArticleList);
                                                     }
                                                 }
            );
            initData();
        }

        public void initData() {
            if (flag == 2) {
                //分类资讯
                mClassifyData.clear();
                ClassifyEnglishMessage mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(Color.BLUE);
                mClassifyItem.setmName(getActivity().getString(R.string.english_china_daily));
                mClassifyData.add(mClassifyItem);
                mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(Color.GREEN);
                mClassifyItem.setmName(getActivity().getString(R.string.android_news));
                mClassifyData.add(mClassifyItem);
                mClassifyAdapter.notifyDataSetChanged();
            } else {
                //分类英语
                mClassifyData.clear();
                ClassifyEnglishMessage mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(R.drawable.ab);
                mClassifyItem.setmName(getActivity().getString(R.string.android_luo));
                mClassifyData.add(mClassifyItem);
                mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(Color.GREEN);
                mClassifyItem.setmName(getActivity().getString(R.string.android_elite));
                mClassifyData.add(mClassifyItem);
                mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(Color.GREEN);
                mClassifyItem.setmName(getActivity().getString(R.string.android_experience));
                mClassifyData.add(mClassifyItem);
                mClassifyItem = new ClassifyEnglishMessage();
                mClassifyItem.setmDrawPic(Color.GREEN);
                mClassifyItem.setmName(getActivity().getString(R.string.android_src));
                mClassifyData.add(mClassifyItem);
                mClassifyAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Fragment curFragment = mFragManager.findFragmentById(R.id.fragment_container);
        if (curFragment instanceof WordClassifyFragment
                || curFragment instanceof MessageClassifyFragment
                || curFragment instanceof ArticleListFragment
                || curFragment instanceof EnglishClassify) {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < 2000) {
                finish();
            } else {
                startTime = System.currentTimeMillis();
                Util.showToast(this, R.string.press_back_again);
            }
        } else {
            mFragManager.popBackStack();
        }
    }
}
