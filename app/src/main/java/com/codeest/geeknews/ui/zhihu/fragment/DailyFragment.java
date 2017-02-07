package com.codeest.geeknews.ui.zhihu.fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codeest.geeknews.R;
import com.codeest.geeknews.base.BaseFragment;
import com.codeest.geeknews.component.RxBus;
import com.codeest.geeknews.model.bean.DailyBeforeListBean;
import com.codeest.geeknews.model.bean.DailyListBean;
import com.codeest.geeknews.presenter.DailyPresenter;
import com.codeest.geeknews.presenter.contract.DailyContract;
import com.codeest.geeknews.ui.zhihu.activity.CalendarActivity;
import com.codeest.geeknews.ui.zhihu.activity.ZhihuDetailActivity;
import com.codeest.geeknews.ui.zhihu.adapter.DailyAdapter;
import com.codeest.geeknews.util.CircularAnimUtil;
import com.codeest.geeknews.util.DateUtil;
import com.codeest.geeknews.util.SnackbarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by codeest on 2016/8/11.
 */
public class DailyFragment extends BaseFragment<DailyPresenter> implements DailyContract.View {

    @BindView(R.id.view_loading)
    RotateLoading viewLoading;
    @BindView(R.id.fab_calender)
    FloatingActionButton fabCalender;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.rv_daily_list)
    RecyclerView rvDailyList;

    String currentDate;
    DailyAdapter mAdapter;
    List<DailyListBean.StoriesBean> mList = new ArrayList<>();

    private int position = -1;

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily;
    }

    @Override
    protected void initEventAndData() {
        currentDate = DateUtil.getTomorrowDate();
        mAdapter = new DailyAdapter(mContext,mList);
        mAdapter.setOnItemClickListener(new DailyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,View shareView) {
                mPresenter.insertReadToDB(mList.get(position).getId());
                mAdapter.setReadState(position,true);
                DailyFragment.this.position = position;
                Intent intent = new Intent();
                intent.setClass(mContext, ZhihuDetailActivity.class);
                intent.putExtra("id",mList.get(position).getId());
                String transitionSharedItemName = ViewCompat.getTransitionName(shareView);
                intent.putExtra("shared-item-name", transitionSharedItemName);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, shareView, transitionSharedItemName);
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(currentDate.equals(DateUtil.getTomorrowDate())) {
                    mPresenter.getDailyData();
                } else {
                    int year = Integer.valueOf(currentDate.substring(0,4));
                    int month = Integer.valueOf(currentDate.substring(4,6));
                    int day = Integer.valueOf(currentDate.substring(6,8));
                    CalendarDay date = CalendarDay.from(year, month - 1, day);
                    RxBus.getDefault().post(date);
                }
            }
        });
        rvDailyList.setLayoutManager(new LinearLayoutManager(mContext));
        rvDailyList.setAdapter(mAdapter);
        viewLoading.start();
        mPresenter.getDailyData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter == null || position < 0) {
            return;
        }
        if (mAdapter.getIsBefore()) {
            mAdapter.notifyItemChanged(position + 1);
        } else {
            mAdapter.notifyItemChanged(position + 2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        position = -1;
    }

    /**
     * 当天数据
     * @param info
     */
    @Override
    public void showContent(DailyListBean info) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        mList = info.getStories();
        currentDate = String.valueOf(Integer.valueOf(info.getDate()) + 1);
        mAdapter.addDailyDate(info);
        mPresenter.stopInterval();
        mPresenter.startInterval();
    }

    /**
     * 过往数据
     * @param date
     * @param info
     */
    @Override
    public void showMoreContent(String date,DailyBeforeListBean info) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        mPresenter.stopInterval();
        mList = info.getStories();
        currentDate = String.valueOf(Integer.valueOf(info.getDate()));
        viewLoading.stop();
        mAdapter.addDailyBeforeDate(info);
    }

    @Override
    public void showProgress() {
        viewLoading.start();
    }

    @Override
    public void doInterval(int currentCount) {
        mAdapter.changeTopPager(currentCount);
    }

    @OnClick(R.id.fab_calender)
    void startCalender() {
        Intent it = new Intent();
        it.setClass(mContext,CalendarActivity.class);
        CircularAnimUtil.startActivity(mActivity,it,fabCalender,R.color.fab_bg);
    }

    @Override
    public void showError(String msg) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        SnackbarUtil.showShort(rvDailyList,msg);
    }
}
