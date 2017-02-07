package com.codeest.geeknews.ui.zhihu.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeest.geeknews.R;
import com.codeest.geeknews.app.App;
import com.codeest.geeknews.component.ImageLoader;
import com.codeest.geeknews.model.bean.SectionChildListBean;
import com.codeest.geeknews.widget.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by codeest on 16/8/28.
 */

public class SectionChildAdapter extends RecyclerView.Adapter<SectionChildAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;
    private List<SectionChildListBean.StoriesBean> mList;
    private OnItemClickListener onItemClickListener;

    public SectionChildAdapter(Context mContext,List<SectionChildListBean.StoriesBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_daily,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SectionChildListBean.StoriesBean listItem = mList.get(position);
        final StringBuilder url = new StringBuilder("");
        if (listItem.getImages() != null && listItem.getImages()
                                                    .size() > 0) {
            holder.ivImage.setVisibility(View.VISIBLE);
            url.append(listItem.getImages()
                               .get(0));
            ImageLoader.load(mContext, url.toString(), holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }
        if (listItem.getReadState()) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.news_read));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.news_unread));
        }
        holder.tvTitle.setText(listItem.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    SquareImageView iv = (SquareImageView) view.findViewById(R.id.iv_daily_item_image);
                    ViewCompat.setTransitionName(iv,
                                                 App.getInstance()
                                                    .getString(R.string.transition_share_item_name) + "-" + url);
                    onItemClickListener.OnItemClick(holder.getAdapterPosition(), iv);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_daily_item_image)
        SquareImageView ivImage;
        @BindView(R.id.tv_daily_item_title)
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position,View shareView);
    }

    public void setReadState(int position,boolean readState) {
        mList.get(position).setReadState(readState);
    }
}
