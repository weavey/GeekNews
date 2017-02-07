package com.codeest.geeknews.ui.zhihu.activity;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codeest.geeknews.R;
import com.codeest.geeknews.base.BaseActivity;
import com.codeest.geeknews.model.bean.SectionChildListBean;
import com.codeest.geeknews.presenter.SectionChildPresenter;
import com.codeest.geeknews.presenter.contract.SectionChildContract;
import com.codeest.geeknews.ui.zhihu.adapter.SectionChildAdapter;
import com.codeest.geeknews.util.SnackbarUtil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by codeest on 16/8/28.
 */

public class SectionActivity extends BaseActivity<SectionChildPresenter> implements SectionChildContract.View {

    @BindView(R.id.rv_section_content)
    RecyclerView rvSectionContent;
    @BindView(R.id.view_loading)
    RotateLoading viewLoading;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

    List<SectionChildListBean.StoriesBean> mList;
    SectionChildAdapter mAdapter;

    int id;
    String title;

    private int position = -1;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_section;
    }

    @Override
    protected void initEventAndData() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        title = intent.getStringExtra("title");
        setToolBar(mToolBar,title);
        mList = new ArrayList<>();
        mAdapter = new SectionChildAdapter(mContext, mList);
        rvSectionContent.setLayoutManager(new LinearLayoutManager(mContext));
        rvSectionContent.setAdapter(mAdapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getThemeChildData(id);
            }
        });
        mAdapter.setOnItemClickListener(new SectionChildAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View shareView) {
                mPresenter.insertReadToDB(mList.get(position).getId());
                mAdapter.setReadState(position, true);
                SectionActivity.this.position = position;
                Intent intent = new Intent();
                intent.setClass(mContext, ZhihuDetailActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                String transitionSharedItemName = ViewCompat.getTransitionName(shareView);
                intent.putExtra("shared-item-name", transitionSharedItemName);
                if (shareView != null) {
                    ActivityCompat.startActivity(SectionActivity.this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, shareView, transitionSharedItemName).toBundle());
                } else {
                    ActivityCompat.startActivity(SectionActivity.this, intent, ActivityOptionsCompat.makeBasic().toBundle());
                }
            }
        });
        mPresenter.getThemeChildData(id);
        viewLoading.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null && position > 0) {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        position = -1;
    }

    @Override
    public void showContent(SectionChildListBean sectionChildListBean) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        mList.clear();
        mList.addAll(sectionChildListBean.getStories());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String msg) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            viewLoading.stop();
        }
        SnackbarUtil.showShort(getWindow().getDecorView(),msg);
    }
}
