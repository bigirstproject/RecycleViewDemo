package com.sunsun.recycleview.baseadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Description: ${todo}
 * Created by 李德贤，2019/8/26
 */
public abstract class BaseLoadAdapter<T> extends RecyclerView.Adapter {

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_BOTTOM = 2;

    public int loadState;
    public static final int STATE_LOADING = 1;
    public static final int STATE_LASTED = 2;
    public static final int STATE_ERROR = 3;

    boolean hasMore = true;
    boolean isLoading = false;

    private int pageCount = 5;//每一页和后台说定的条数
    public List<T> list;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return new BottomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bottom_layout, viewGroup, false));
        } else {
            return setItemViewHolder(viewGroup, viewType);
        }
    }

    protected abstract RecyclerView.ViewHolder setItemViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (TYPE_BOTTOM == getItemViewType(position)) {
            final ProgressBar progressBar = ((BottomViewHolder) viewHolder).progressBar;
            final TextView bottomTextView = ((BottomViewHolder) viewHolder).bottomTextView;
            final ImageView bottomIcon = ((BottomViewHolder) viewHolder).bottomIcon;
            switch (loadState) {
                case STATE_LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    bottomIcon.setVisibility(View.GONE);
                    bottomTextView.setText("加载中");
                    viewHolder.itemView.setOnClickListener(null);
                    hasMore = true;
                    break;
                case STATE_LASTED:
                    progressBar.setVisibility(View.GONE);
                    bottomIcon.setVisibility(View.VISIBLE);
                    bottomIcon.setImageResource(R.drawable.info_icon);
                    bottomTextView.setText("没有更多了");
                    viewHolder.itemView.setOnClickListener(null);
                    hasMore = false;
                    break;
                case STATE_ERROR:
                    progressBar.setVisibility(View.GONE);
                    bottomIcon.setVisibility(View.VISIBLE);
                    bottomIcon.setImageResource(R.drawable.error_icon);
                    bottomTextView.setText("加载失败请点击重试");
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            bottomIcon.setVisibility(View.GONE);
                            bottomTextView.setText("加载中");
                            LoadingMore();
                        }
                    });
                    hasMore = true;
                    break;
            }
        } else {
            onBindItemViewHolder(viewHolder, position);
        }
    }

    protected abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        if (list.size() < pageCount) {
            return TYPE_ITEM;
        } else {
            if (position == list.size()) {
                return TYPE_BOTTOM;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < pageCount) {
            hasMore = false;
            return list.size();
        } else {
            return list.size() + 1;
        }
    }

    protected abstract void LoadingMore();


    public void setErrorStatus() {
        loadState = STATE_ERROR;
        notifyItemChanged(list.size());
        setLoading(false);
    }

    public void setLastedStatus() {
        loadState = STATE_LASTED;
        notifyItemChanged(list.size());
    }


    public void addList(List addList) {
        int count = this.list.size();
        this.list.addAll(addList);
        notifyItemRangeChanged(count, addList.size());
        setLoading(false);
    }

    public void refreshList(List newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public class BottomViewHolder extends RecyclerView.ViewHolder {

        TextView bottomTextView;
        ImageView bottomIcon;
        ProgressBar progressBar;

        public BottomViewHolder(View itemView) {
            super(itemView);
            bottomTextView = (TextView) itemView.findViewById(R.id.bottom_title);
            bottomIcon = (ImageView) itemView.findViewById(R.id.bottom_icon);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}
