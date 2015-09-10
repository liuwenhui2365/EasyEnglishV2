package com.example.liu.autotanslate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class Classify extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ActionBar actionbar;
    private ViewPager Viewpage;
    private Button button;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private HashMap<String,String> item = null;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<HashMap<String,String>> menuLists = new ArrayList<>();
    private ListAdapter adapter;
    /**ViewPager包含的Fragment集合**/
//    private ArrayList<Fragment> fragments;
    /**ActionBar上的Tab集合**/
    private ArrayList<ActionBar.Tab> tabs;
    /**当前页**/
    protected int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

//        Utils.showOverflowMenu(this);//如果手机有menu键也显示flowMenu
        initViewPager();//初始化ViewPager要在初始化initTab之前，否则会出错
        initMenu();
//      添加tab切换的时候使用
//      initTab();
   //   ton.setOnClickListener(listener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classify, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //将ActionBar上的图标与Drawer结合起来g关键步骤否则打不开菜单栏
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Utils.showToast(this, "您点击了刷新菜单", Toast.LENGTH_SHORT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        // TODO Auto-generated method stub
        LayoutInflater inflater=getLayoutInflater();
        Viewpage = (ViewPager) findViewById(R.id.vpage_catalogy);

//      为每个Tab标签添加相应的布局文件
//       TODO 添加tab切换
        ArrayList<View> views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.classifyitem1, null));
//        views.add(inflater.inflate(R.layout.classifyitem2, null));
//        views.add(inflater.inflate(R.layout.classifyitem3, null));
        Viewpage.setAdapter(new ViewPagerAdapter(views));
//        viewPager.setOnPageChangeListener(new ViewPagerChangeListener());
        //初始化ViewPager显示的页面集合
//        fragments = new ArrayList<Fragment>();
//        BaseFragment fragment1=BaseFragment.newInstance(BaseFragment.LOAD_FRAGMENT_1);
//        BaseFragment fragment2=BaseFragment.newInstance(BaseFragment.LOAD_FRAGMENT_2);
//        fragments.add(fragment1);
//        fragments.add(fragment2);
//        //设置ViewPager adapter
//        BaseFragmentPagerAdapter adapter=new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//        vpContent.setAdapter(adapter);
//        Viewpage.setCurrentItem(0);//默认显示第一个页面
        //监听ViewPager事件
        Viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
////            position表示第几页从0开始
//
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                    getLayoutInflater().inflate(R.layout.classifyitem1, null);
                    button = (Button) findViewById(R.id.button1);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button2);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button3);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button4);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button5);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button6);
                    button.setOnClickListener(listener);
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                actionbar.selectTab(tabs.get(position));//当滑动页面结束让ActionBar选择指定的Tab

            }
//
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//
    }

    /**
     * 初始化菜单栏
     */
     private void initMenu(){
         mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         mDrawerList = (ListView) findViewById(R.id.menu_list);

         item = new HashMap<>();
         item.put("item","单词分类");
         menuLists.add(item);
         item = new HashMap<>();
         item.put("item","分享");
         menuLists.add(item);

//         item = new HashMap<>();
//         item.put("item","接受分享");
//         menuLists.add(item);

         item = new HashMap<>();
         item.put("item","接受到的分享");
         menuLists.add(item);
         adapter = new SimpleAdapter(Classify.this,menuLists,//需要绑定的数据
                 R.layout.menulistitem,//每一行的布局
                 //动态数组中的数据源的键对应到定义布局的View中
                 new String[] {"item"},
                 new int[] {R.id.menu}
         );
         mDrawerList.setAdapter(adapter);

//         左侧菜单栏的点击事件
         mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              switch (position) {
                  case 0:
                      Intent intent = new Intent(Classify.this, WordClassify.class);
                      startActivity(intent);
                      break;
                  case 1:
                      intent = new Intent(Classify.this, Share.class);
                      startActivity(intent);
                      break;
//                  case 2:
//                      intent = new Intent(Classify.this, AcceptShare.class);
//                      startActivity(intent);
//                      break;
                  case 2:
                      intent = new Intent(Classify.this, AlreadyAcceptShare.class);
                      startActivity(intent);
                      break;
              }
              }
         });

//         设置标题栏图标
         mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                 R.drawable.tubiao, R.string.open,
                 R.string.close) {
             @Override
             public void onDrawerOpened(View drawerView) {
                 super.onDrawerOpened(drawerView);
                 getSupportActionBar().setTitle("菜单");
                 invalidateOptionsMenu(); // Call onPrepareOptionsMenu()
             }

             @Override
             public void onDrawerClosed(View drawerView) {
                 super.onDrawerClosed(drawerView);
                 getSupportActionBar().setTitle("主页");
                 invalidateOptionsMenu();
             }
         };

         mDrawerLayout.setDrawerListener(mDrawerToggle);

         //开启ActionBar上APP ICON的功能
         try {
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             getSupportActionBar().setHomeButtonEnabled(true);
         }catch (NullPointerException w){
             w.printStackTrace();
         }
     }

    View.OnClickListener listener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            button = (Button)v;
            switch(button.getId())
            {
                case R.id.button1:
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级科技");
                    startActivity(intent);
                    break;
                case R.id.button2:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级健康");
                    startActivity(intent);
                    break;
                case R.id.button3:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级经济");
                    startActivity(intent);
                    break;
                case R.id.button4:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级教育");
                    startActivity(intent);
                    break;
                case R.id.button5:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级自然");
                    startActivity(intent);
                    break;
                case R.id.button6:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级其他");
                    startActivity(intent);
                    break;


//                case R.id.button21:
//                    intent = new Intent(Intent.ACTION_SEND);
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级科技");
//                    startActivity(intent);
//                    break;
//                case R.id.button22:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级健康");
//                    startActivity(intent);
//                    break;
//                case R.id.button23:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级经济");
//                    startActivity(intent);
//                    break;
//                case R.id.button24:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级历史");
//                    startActivity(intent);
//                    break;
//                case R.id.button25:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级理财");
//                    startActivity(intent);
//                    break;
//                case R.id.button26:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("中级娱乐");
//                    startActivity(intent);
//                    break;
//
//                case R.id.button31:
//                    intent = new Intent(Intent.ACTION_SEND);
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级科技");
//                    startActivity(intent);
//                    break;
//                case R.id.button32:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级健康");
//                    startActivity(intent);
//                    break;
//                case R.id.button33:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级经济");
//                    startActivity(intent);
//                    break;
//                case R.id.button34:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级历史");
//                    startActivity(intent);
//                    break;
//                case R.id.button35:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级理财");
//                    startActivity(intent);
//                    break;
//                case R.id.button36:
//                    intent = new Intent();
//                    intent.setClass(Classify.this,Refresh.class);
//                    intent.setAction("高级娱乐");
//                    startActivity(intent);
//                    break;
            }
        }
    };

//    左侧菜单栏选择
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //需要将ActionDrawerToggle与DrawerLayout的状态同步
        //将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home-Button的Icon
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
