package com.codeest.geeknews.ui.zhihu.fragment;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codeest.geeknews.R;
import com.codeest.geeknews.base.BaseFragment;
import com.codeest.geeknews.model.bean.HotListBean;
import com.codeest.geeknews.presenter.HotPresenter;
import com.codeest.geeknews.presenter.contract.HotContract;
import com.codeest.geeknews.ui.zhihu.activity.ZhihuDetailActivity;
import com.codeest.geeknews.ui.zhihu.adapter.HotAdapter;
import com.codeest.geeknews.util.SnackbarUtil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by codeest on 2016/8/11.
 */
public class HotFragment extends BaseFragment<HotPresenter> implements HotContract.View {

    @BindView(R.id.rv_hot_content)
    RecyclerView rvHotContent;
    @BindView(R.id.view_loading)
    RotateLoading viewLoading;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    List<HotListBean.RecentBean> mList;
    HotAdapter mAdapter;

    private int position = -1;

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void initEventAndData() {
        mList = new ArrayList<>();
        viewLoading.start();
        mAdapter = new HotAdapter(mContext,mList);
        rvHotContent.setVisibility(View.INVISIBLE);
        rvHotContent.setLayoutManager(new LinearLayoutManager(mContext));
        rvHotContent.setAdapter(mAdapter);
        mPresenter.getHotData();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getHotData();
            }
        });
        mAdapter.setOnItemClickListener(new HotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View shareView) {
                mPresenter.insertReadToDB(mList.get(position).getNews_id());
                mAdapter.setReadState(position,true);
                HotFragment.this.position = position;
                Intent intent = new Intent();
                intent.setClass(mContext, ZhihuDetailActivity.class);
                intent.putExtra("id",mList.get(position).getNews_id());
                String transitionSharedItemName = ViewCompat.getTransitionName(shareView);
                intent.putExtra("shared-item-name", transitionSharedItemName);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, shareView, transitionSharedItemName);
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && position > 0) {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        position = -1;
    }

    @Override
    public void showError(String msg) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        SnackbarUtil.showShort(rvHotContent,msg);
    }

    @Override
    public void showContent(HotListBean hotListBean) {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        rvHotContent.setVisibility(View.VISIBLE);
        mList.clear();
        mList.addAll(hotListBean.getRecent());
        mAdapter.notifyDataSetChanged();
    }
}
