package com.aquila.lib.album.base;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2018/3/30 14:07
 * @author 作者: yulong
 * @description ViewPager的基类Adapter
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    protected List<T> dataList;

    /***
     * 添加数据列表
     * @param list
     */
    public void addDataList(List<T> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    /***
     * 获取实际数据的数目
     * @return
     */
    public int getRealCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    /**
     * 添加一条数据
     * @param t
     */
    public void addOneData(T t){
        if (dataList == null){
            dataList = new ArrayList<>();
        }
        dataList.add(t);
        notifyDataSetChanged();
    }

    /**
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

    /***
     * 获取数据源
     * @return
     */
    public List<T> getDataList() {
        return dataList;
    }

    /**
     * 获取制定位置的数据
     * @param position
     * @return
     */
    public T getItem(int position){
        if (dataList != null){
            return dataList.get(position % getRealCount());
        }
        return null;
    }

    /**
     * 设置数据源
     * @param list
     */
    public void setDataList(List<T> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getRealCount();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
