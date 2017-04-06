package com.icourt.alpha.adapter;

import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.recycleradapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.recycleradapter.BaseRecyclerAdapter;
import com.icourt.alpha.db.BaseDao;
import com.icourt.alpha.db.dbdao.FlowerDao;
import com.icourt.alpha.db.dbmodel.FlowerModel;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class FlowerAdapter extends BaseArrayRecyclerAdapter<FlowerModel> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_flower;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FlowerModel flowerModel, int position) {
        if (flowerModel == null) return;
        holder.setText(R.id.tv_flower_id, String.format("pk:%s", flowerModel.pk))
                .setText(R.id.tv_flower_name, String.format("name:%s", flowerModel.name))
                .bindChildClick(R.id.bt_flower_del)
                .bindChildClick(R.id.bt_flower_update);
    }


}
