package com.icourt.alpha.adapter;

import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.db.dbmodel.ContactDbModel;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/13
 * version 1.0.0
 */
public class DemoAdapter2 extends BaseArrayRecyclerAdapter<ContactDbModel> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_test2;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ContactDbModel contactDbModel, int position) {
        if (contactDbModel == null) return;
        TextView tv = holder.obtainView(R.id.textView);
        tv.setText(contactDbModel.name);
    }
}
