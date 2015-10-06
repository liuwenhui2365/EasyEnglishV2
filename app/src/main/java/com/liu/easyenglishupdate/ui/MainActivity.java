package com.liu.easyenglishupdate.ui;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.Util;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    static FragmentManager mFragManager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * 底部导航切换
     */
    private LinearLayout mLytBottomNav1,mLytBottomNav2,mLytBottomNav3,mLytBottomNav4;

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
     * 初始化底部导航空间
     */
    private void initView() {
        mLytBottomNav1 = (LinearLayout)findViewById(R.id.lyt_bottom_item1);
        mLytBottomNav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showToast(MainActivity.this,"你点击了底部导航1");
            }
        });
        mLytBottomNav2 = (LinearLayout)findViewById(R.id.lyt_bottom_item2);
        mLytBottomNav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new WordClassifyFragment(),true);
            }
        });
        mLytBottomNav3 = (LinearLayout)findViewById(R.id.lyt_bottom_item3);
        mLytBottomNav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mLytBottomNav4 = (LinearLayout)findViewById(R.id.lyt_bottom_item4);
        mLytBottomNav4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    /**
     * 左侧导航根据位置进行相应的操作
     *
     * @param number 从1开始
     */
    public void onSectionAttached(int number) {
        Class TargetActivity = null;
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                Util.showToast(this, "你点击了第一项");
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                TargetActivity = PdfParseActivity.class;
                Intent to = new Intent(this, TargetActivity);
                startActivity(to);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                Util.showToast(this, "你点击了第三项");
                Message pdfTest = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.message),"pdfTest");
                pdfTest.setArguments(bundle);
                setFragment(pdfTest,true);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                Util.showToast(this, "你点击了第四项");
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
     * @param back 是否允许返回
     */
    public static void setFragment(Fragment fragment,Boolean back){
        FragmentTransaction transaction = mFragManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        if(back){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private TextView mTxtPdfContent;
        /**
         * start time
         *
         * @param savedInstanceState
         */
        private long startTime;


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            initView(rootView);
            return rootView;
        }

        private void initView(View rootView) {
            mTxtPdfContent = (TextView) rootView.findViewById(R.id.pdf_content);


        }

        @Override
        public void onAttach(Activity activity) {

            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }



    @Override
    public void onBackPressed() {
        Fragment curFragment = mFragManager.findFragmentById(R.id.fragment_container);
        if (curFragment instanceof WordClassifyFragment
                || curFragment instanceof PlaceholderFragment) {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < 2000) {
                finish();
            } else {
                startTime = System.currentTimeMillis();
                Util.showToast(this, R.string.press_back_again);
            }
        }else {
            mFragManager.popBackStack();
        }
    }
}
