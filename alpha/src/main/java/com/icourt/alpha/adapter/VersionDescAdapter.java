package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.AppVersionEntity;

/**
 * Description 更新对话框 内容适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：2017/9/14
 * version 2.1.0
 */
public class VersionDescAdapter extends BaseArrayRecyclerAdapter<AppVersionEntity.VersionDescsBean> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_version_desc;
    }

    @Override
    public void onBindHoder(ViewHolder holder, AppVersionEntity.VersionDescsBean versionDescBean, int position) {
        if (versionDescBean == null) return;
        TextView typeView = holder.obtainView(R.id.version_type_tv);
        TextView descView = holder.obtainView(R.id.version_desc_tv);
        typeView.setText(String.format("[%s]", versionDescBean.type));
        descView.setText(versionDescBean.versionDesc);
        if (TextUtils.equals("新增", versionDescBean.type)) {
            typeView.setTextColor(0xFF63bc7d);
        } else if (TextUtils.equals("优化", versionDescBean.type)) {
            typeView.setTextColor(0xFFc2d391);
        } else if (TextUtils.equals("其他", versionDescBean.type)) {
            typeView.setTextColor(0xFFa6a6a6);
        }
    }
}
