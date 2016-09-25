package com.example.nozoeli.newsnote.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by nozoeli on 16-4-24.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager manager;
    private boolean loading = true;
    public boolean isSearch = false; // 设置flag来确保在查询条件下上拉不会加载更多

    public EndlessScrollListener(LinearLayoutManager manager) {
        this.manager = manager;
    }

    public abstract void onLoadMore();

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastVisibleItem = manager.findLastVisibleItemPosition(); // 获取最后一个可见的元素位置
        int totalItem = manager.getItemCount();                      // 获取Layout中总的元素个数

        if (loading && dy > 0) {
            loading = false;
//            previousTotal = totalItem;
        }
        if (!loading && totalItem - lastVisibleItem < 2 && !isSearch) {           // 当滑动到最后一个元素时加载新数据
            onLoadMore();
            loading = true;
        }

    }
}
