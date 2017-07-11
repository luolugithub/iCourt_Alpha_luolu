package com.icourt.alpha.adapter;

import com.bigkoo.pickerview.adapter.WheelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/11
 * version 2.0.0
 */

public class TimeWheelAdapter implements WheelAdapter<String> {
    List<String> timeList = new ArrayList<>();

    public TimeWheelAdapter(List<String> timeList) {
        this.timeList = timeList;
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
