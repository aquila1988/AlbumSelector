package com.aquila.lib.album.base;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2018/4/11 11:54
 * @author 作者: yulong
 * @description List的Adapter基类
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> dataList;

    /**
     * 设置数据源
     * @param dataList
     */
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    /***
     * 添加数据的列表
     * @param list
     */
    public void addDataList(List<T> list) {
        if (this.dataList == null) {
            this.dataList = list;
        } else {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    /**
     * 添加一条数据
     */
    public void addOneData(T t){
        if (dataList == null){
            dataList = new ArrayList<>();
        }
        dataList.add(t);
        notifyDataSetChanged();
    }

    /***
     * 删除一条数据
     * @param t
     * @return
     */
    public boolean deleteItem(T t) {
        boolean flag = false;
        if (dataList != null){
            flag = dataList.remove(t);
            notifyDataSetChanged();
        }
        return flag;
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

    /**
     * 获取实际数据的个数
     * @return
     */
    public int getRealCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public int getCount() {
        return getRealCount();
    }

    @Override
    public T getItem(int position) {
        if (dataList == null) {
            return null;
        }
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
