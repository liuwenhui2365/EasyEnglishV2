package com.liu.easyenglishupdate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.Util;

/**
 * 列表工具，实现上下拉刷新
 *显示套餐列表等数据
 */

public class AutoListView extends ListView implements OnScrollListener {

	// 区分PULL和RELEASE的距离的大小
	private static final int SPACE = 20;

	// 定义header的四种状态和当前状态
	private static final int NONE = 0; // 正常状态；
	private static final int PULL = 1;// 提示下拉状态；
	private static final int RELEASE = 2;// 提示释放状态；
	private static final int REFRESHING = 3;// 正在刷新状态；
	private int state; //当前状态

	private LayoutInflater mInflater;
	private View mViewHeader;
	private View mViewFooter;
	private TextView mTxtTip;
	private TextView mTxtLastUpdate;
	private ImageView mImgArrow;       //下拉箭头
	private ProgressBar mProgressBarRefreshing;//头部进度条

	private TextView mTxtNoData;   //无数据
	private TextView mTxtLoadFull; //加载完毕
	private TextView mTxtMore;     //还可以继续加载
	private ProgressBar mProgressBarLoading;//底部进度条

	private RotateAnimation mAnimation;
	private RotateAnimation mReverseAnimation;

	private int mStartY;

	private int mFirstVisibleItem;
	private int mScrollState;
	private int mHeaderContentInitialHeight;
	private int mHeaderContentHeight;

	// 只有在listview第一个item显示的时候（listview滑到了顶部）才进行下拉刷新，
	//否则此时的下拉只是滑动listview
	private boolean mIsRecorded;
	/**
	 * 是否允许刷新
	 * 默认允许
	 */
	private boolean mIsRefresh = true;
	private boolean mIsLoading;// 判断是否正在加载
	/**
	 * 开启或者关闭加载更多功能
	 */
	private boolean mLoadEnable = true;
	private boolean mIsLoadFull;
	private int mPageSize = 10;

	private OnRefreshListener mOnRefreshListener;
	private OnLoadListener mOnLoadListener;

	public AutoListView(Context context) {
		super(context);
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	// 下拉刷新监听
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.mOnRefreshListener = onRefreshListener;
	}

	// 加载更多监听
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.mLoadEnable = true;
		this.mOnLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return mLoadEnable;
	}

	public int getPageSize() {
		return mPageSize;
	}

	public void setPageSize(int pageSize) {
		this.mPageSize = pageSize;
	}

	// 初始化组件
	private void initView(Context context) {

		// 设置箭头特效
		mAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setDuration(100);
		mAnimation.setFillAfter(true);

		mReverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(100);
		mReverseAnimation.setFillAfter(true);

		mInflater = LayoutInflater.from(context);
		mViewFooter = mInflater.inflate(R.layout.footview, null);
		mTxtLoadFull = (TextView) mViewFooter.findViewById(R.id.loadFull);
		mTxtNoData = (TextView) mViewFooter.findViewById(R.id.noData);
		mTxtMore = (TextView) mViewFooter.findViewById(R.id.load_more);
		mProgressBarLoading = (ProgressBar) mViewFooter.findViewById(R.id.pbLoaderWaiting);

		mViewHeader = mInflater.inflate(R.layout.headerview, null);
		mImgArrow = (ImageView) mViewHeader.findViewById(R.id.ivArrow);//箭头图片
		mTxtTip = (TextView) mViewHeader.findViewById(R.id.refresh_tips);//松开或下拉
		mTxtLastUpdate = (TextView) mViewHeader.findViewById(R.id.refresh_last_time);//时间
		mProgressBarRefreshing = (ProgressBar) mViewHeader.findViewById(R.id.refresh_Progress);//进度条

		// 为listview添加头部和尾部，并进行初始化
		mHeaderContentInitialHeight = mViewHeader.getPaddingTop();
//		measureView(mViewHeader);
		mHeaderContentHeight = mViewHeader.getMeasuredHeight();
		topPadding(-mHeaderContentHeight);
		mViewHeader.setVisibility(GONE);
		mViewFooter.setVisibility(GONE);
		this.addHeaderView(mViewHeader);
		this.addFooterView(mViewFooter);
		this.setOnScrollListener(this);
	}


	/**
	 * 	这里的开启或者关闭加载更多，并不支持动态调整
	 */
	public void setLoadEnable(boolean loadEnable) {
		this.mLoadEnable = loadEnable;
		this.removeFooterView(mViewFooter);
	}

	/**
	 * 设置是否允许刷新
	 */
	public void setIsRefresh(boolean isRefresh){
		mIsRefresh = isRefresh;
	}

	public void onRefresh() {
		if (mOnRefreshListener != null && mIsRefresh) {
			mOnRefreshListener.onRefresh();
		}
	}

	public void onLoad() {
		if (mOnLoadListener != null) {
			//在这里设置正在加载标志位，不能在调用onLoad之后再设置true，否则一直true
			mIsLoading = true;
			mOnLoadListener.onLoad();
		}
	}

//	public void onRefreshComplete(String updateTime) {
//		mTxtLastUpdate.setText(getContext().getString(R.string.last_update_time)+":"+updateTime);
//		state = NONE;
//		refreshHeaderViewByState();
//	}

	// 用于下拉刷新结束后的回调
	public void onRefreshComplete() {
//		String currentTime = Util.getCurrentTime();
//		onRefreshComplete(currentTime);
		mTxtLastUpdate.setText(getContext().getString(R.string.refresh_complete));
		state = NONE;
		refreshHeaderViewByState();
	}

	// 用于加载更多结束后的回调
	public void onLoadComplete() {
		mIsLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.mFirstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.mScrollState = scrollState;
		isNeedLoad(view, scrollState);
	}

	// 根据listview滑动的状态判断是否需要加载更多
	private void isNeedLoad(AbsListView view, int scrollState) {
		if (!mLoadEnable) {
			return;
		}
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !mIsLoading
					&& view.getLastVisiblePosition() == view
					.getPositionForView(mViewFooter) && !mIsLoadFull) {
				onLoad();
			}
		} catch (Exception e) {
			e.printStackTrace();
			//出现异常修改正在加载标志位
			mIsLoading =false;
		}
	}

	/**
	 * 监听触摸事件，解读手势
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mFirstVisibleItem == 0) {
				//这里判断是否运行刷新
				if (mIsRefresh) {
					mIsRecorded = true;
				}else{
					mIsRecorded = false;
				}
				mStartY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (state == PULL) {
				state = NONE;
				refreshHeaderViewByState();
			} else if (state == RELEASE) {
				state = REFRESHING;
				refreshHeaderViewByState();
				onRefresh();
			}
			mIsRecorded = false;
			break;
		case MotionEvent.ACTION_MOVE:
			whenMove(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 *   解读手势，刷新header状态
	 */
	private void whenMove(MotionEvent ev) {
		if (!mIsRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - mStartY;
		int topPadding = space - mHeaderContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (mScrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > mHeaderContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < mHeaderContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	// 调整header的大小。其实调整的只是距离顶部的高度。
	private void topPadding(int topPadding) {
		mViewHeader.setPadding(mViewHeader.getPaddingLeft(), topPadding,mViewHeader.getPaddingRight(), mViewHeader.getPaddingBottom());
		mViewHeader.invalidate();
	}

	/**
	 * 这个方法是根据结果的大小来决定footer显示的。
	 * <p>
	 * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据,显示加载中。
	 * 如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
	 * </p>
	 *
	 * @param resultSize
	 */
	public void setResultSize(int resultSize) {
		if (resultSize == 0) {
			mIsLoadFull = true;
			mTxtLoadFull.setVisibility(View.GONE);
			mProgressBarLoading.setVisibility(View.GONE);
			mTxtMore.setVisibility(View.GONE);
			mTxtNoData.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < mPageSize) {
			mIsLoadFull = true;
			mTxtLoadFull.setVisibility(View.VISIBLE);
			mProgressBarLoading.setVisibility(View.GONE);
			mTxtMore.setVisibility(View.GONE);
			mTxtNoData.setVisibility(View.GONE);
		} else if (resultSize == mPageSize) {
			mIsLoadFull = false;
			mTxtLoadFull.setVisibility(View.GONE);
			mProgressBarLoading.setVisibility(View.VISIBLE);
			mTxtMore.setVisibility(View.VISIBLE);
			mTxtNoData.setVisibility(View.GONE);
		}

	}

	// 根据当前状态，调整header
	private void refreshHeaderViewByState() {

//		RotateAnimation anim = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//		anim.setDuration(500);
//		anim.setFillAfter(true);
//
//		RotateAnimation anim1 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//		anim1.setDuration(500);
//		anim1.setFillAfter(true);
		switch (state) {
		case NONE:
			topPadding(-mHeaderContentHeight);
			mTxtTip.setText(R.string.pull_to_refresh);
			mProgressBarRefreshing.setVisibility(View.GONE);
			mImgArrow.clearAnimation();
			mImgArrow.setImageResource(R.drawable.refresh);
			break;
		case PULL:
			mViewHeader.setVisibility(VISIBLE);
			mImgArrow.setVisibility(View.VISIBLE);
			mTxtTip.setVisibility(View.VISIBLE);
			mTxtLastUpdate.setVisibility(View.VISIBLE);
			mProgressBarRefreshing.setVisibility(View.GONE);
			mTxtTip.setText(R.string.pull_to_refresh);
			mImgArrow.clearAnimation();
			mImgArrow.setAnimation(mReverseAnimation);
			break;
		case RELEASE:
			mImgArrow.setVisibility(View.VISIBLE);
			mTxtTip.setVisibility(View.VISIBLE);
			mTxtLastUpdate.setVisibility(View.VISIBLE);
			mProgressBarRefreshing.setVisibility(View.GONE);
			mTxtTip.setText(R.string.release_to_refresh);
			mImgArrow.clearAnimation();
			mImgArrow.setAnimation(mAnimation);
			break;
		case REFRESHING:
			topPadding(mHeaderContentInitialHeight);
			mProgressBarRefreshing.setVisibility(View.VISIBLE);
			mImgArrow.clearAnimation();
			mImgArrow.setVisibility(View.GONE);
			//tip.setVisibility(View.GONE);
			mTxtTip.setText(R.string.release_to_refresh);
			mTxtLastUpdate.setVisibility(View.VISIBLE);
			//lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

	// 用来计算header大小的。比较隐晦。因为header的初始高度就是0,貌似可以不用。
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/*
	 * 定义下拉刷新接口
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/*
	 * 定义加载更多接口
	 */
	public interface OnLoadListener {
		public void onLoad();
	}

}
