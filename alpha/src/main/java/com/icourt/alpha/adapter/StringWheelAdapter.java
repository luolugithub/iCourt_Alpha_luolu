package com.icourt.alpha.adapter;

import com.bigkoo.pickerview.adapter.WheelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaodanyang on 2017/10/28.
 */

public class StringWheelAdapter implements WheelAdapter<String> {

    List<String> timeList = new ArrayList<>();

    public StringWheelAdapter(List<String> data) {
        if (data == null) {
            this.timeList = new ArrayList<>();
        } else {
            this.timeList = data;
        }
    }

    public List<String> getTimeList() {
        return timeList;
    }

    @Override
    public int getItemsCount() {
        return timeList.size();
    }

    @Override
    public String getItem(int i) {
        return timeList.get(i);
    }

    @Override
    public int indexOf(String o) {
        return timeList.indexOf(o);
    }

}
