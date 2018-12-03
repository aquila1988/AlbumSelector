package com.aquila.lib.album.base;


import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/***
 *@date 创建时间 2018/3/22 13:28
 *@author 作者: yulong
 *@description 基类的RecyclerAdapter
 */
public abstract class BaseRecycleAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> dataList;

    protected int selectPosition = -1;

    public BaseRecycleAdapter() {
    }

    public void deleteItem(T t) {
        dataList.remove(t);
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public T getSelectItemObject() {
        if (dataList == null || selectPosition < 0) {
            return null;
        }
        return dataList.get(selectPosition % getRealCount());
    }

    //添加一条数据
    public void addOneData(T t) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.add(t);
        notifyDataSetChanged();
    }

    /***
     * 获取数据在索引的位置
     * @param t
     * @return
     */
    public int getDataPosition(T t){
        if (dataList == null){
            return -1;
        }
        return dataList.indexOf(t);
    }

    //设置数据源
    public void setDataList(List<T> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    //添加一组数据
    public void addDataList(List<T> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (holder == null) {
            return;
        }
        final T t = dataList.get(position);
        holder.initUIData(t, position);
        holder.onSelectPosition(position == selectPosition);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getRealCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public int getItemCount() {
        return getRealCount();
    }
}
