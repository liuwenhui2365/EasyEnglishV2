package com.example.liu.autotanslate;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class MyListView extends ListView implements OnScrollListener {
    View footer, header;// 底部布局
    int totalItemCount;// ListView加载数据总量
    int lastVisibaleItem;// 底部显示的数据
    boolean isLoading;// 是否在加载
    OnLoaderListener loaderListener;// 加载监听
    // 下拉刷新数据
    int headerHeight;// 顶部布局文件的高度；
    int scrollState;// listview 当前滚动状态；
    boolean isRemark;// 标记，当前是在listview最顶端摁下的；
    int startY;// 摁下时的Y值；
    int state;// 当前的状态；
    final int NONE = 0;// 正常状态；
    final int PULL = 1;// 提示下拉状态；
    final int RELESE = 2;// 提示释放状态；
    final int REFLASHING = 3;// 刷新状态；
    public MyListView(Context context) {
        super(context);
        initView(context);
    }
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footview, null);
        header = inflater.inflate(R.layout.headerview, null);
//        NULL Exception
//        measureView(header);
        headerHeight = header.getMeasuredHeight();
        footer.findViewById(R.id.list).setVisibility(View.GONE);
        topPadding(-headerHeight);
        this.addHeaderView(header);// 加到顶部
        this.addFooterView(footer);// 加到底部
        header.setVisibility(GONE);
        footer.setVisibility(GONE);
        this.setOnScrollListener(this);// 监听滚动到底部
//        Log.d("初始化完成","header"+header+"foot"+footer);
    }

    public void setLoaderListener(OnLoaderListener loaderListener) {
        this.loaderListener = loaderListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (totalItemCount == lastVisibaleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.setVisibility(VISIBLE);
                footer.findViewById(R.id.list).setVisibility(View.VISIBLE);
                loaderListener.onLoad();// 加载更多
            }
        }
        this.scrollState = scrollState;
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {

        this.lastVisibaleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
//        Log.d("onScroll lastVisibaleItem", String.valueOf(lastVisibaleItem));

    }

    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }
    /**
     * 设置header 布局 上边距；
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(),
                header.getPaddingBottom());
        header.invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getFirstVisiblePosition() == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    // 加载最新数据；
                    reflashViewByState();
                    loaderListener.onReflash();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    /**
     * 判断移动过程操作；
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - headerHeight;

        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    reflashViewByState();
                }
                break;
            case PULL:
                header.setVisibility(VISIBLE);

                topPadding(topPadding);
                if (space > headerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashViewByState();
                }
                break;
            case RELESE:
                header.setVisibility(VISIBLE);

                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    state = PULL;
                    reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
        }
    }
    /**
     * 根据当前状态，改变界面显示；注意设置Visibility的属性
     */
    private void reflashViewByState() {
        TextView tip = (TextView) header.findViewById(R.id.refresh_tips);
        ImageView arrow = (ImageView) header.findViewById(R.id.ivArrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.refresh_Progress);

        RotateAnimation anim = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);

        RotateAnimation anim1 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);

        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                tip.setText("下拉可以刷新！");
                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFLASHING:
                topPadding(getPaddingTop());
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }
    /**
     * 下拉获取完数据；
     */
    public void reflashComplete() {
        state = NONE;
        isRemark = false;
        reflashViewByState();
        TextView lastupdatetime = (TextView) header.findViewById(R.id.refresh_last_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
        header.setVisibility(GONE);
    }
    /**
     * 上拉加载完数据
     */
    public void loadComplete() {
        isLoading = false;
        footer.setVisibility(GONE);
        footer.findViewById(R.id.list).setVisibility(View.GONE);
    }

    /**
     * 加载回调接口
     *
     * @author shizhao
     *
     */
    public interface OnLoaderListener {
        public void onLoad();// 上拉加载回调
        public void onReflash();// 下拉刷新回调
    }
}