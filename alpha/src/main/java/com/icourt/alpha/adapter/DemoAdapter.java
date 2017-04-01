package com.icourt.alpha.adapter;

import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.recycleradapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.recycleradapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.DemoEntity;

/**
 * Description
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/29
 * version
 */

public class DemoAdapter extends BaseArrayRecyclerAdapter<DemoEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    public DemoAdapter() {

        //方式1：可以在adapter本身注册监听事件 多个activity fragment 引用 比较省事
        this.setOnItemClickListener(this);
        this.setOnItemChildClickListener(this);

        //方式2：可以在activity/fagment中设置监听 自由控制权限 点击去处

        /*DemoAdapter xxxxAdpter;
        xxxxAdpter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

            }
        });*/
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_demo;
    }

    @Override
    public void onBindHoder(ViewHolder holder, DemoEntity demoEntity, int position) {
        //第一步 校验空
        if (demoEntity == null) return;

        //方式1:获取控件 并赋值
        /*TextView tv_name = holder.obtainView(R.id.tv_name);
        TextView tv_age = holder.obtainView(R.id.tv_age);
        Button bt_click = holder.obtainView(R.id.bt_click);

        tv_name.setText(String.format("name:%s", demoEntity.name));
        tv_age.setText(String.format("age:%s", demoEntity.age));
        holder.bindChildClick(bt_click);*/

        //方式2:链式赋值
        holder.setText(R.id.tv_name, String.format("name:%s", demoEntity.name))
                .setText(R.id.tv_age, String.format("age:%s", demoEntity.age))
                .bindChildClick(R.id.bt_click);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        if (getParentHeaderFooterAdapter() == null) {
            showTopSnackBar(view, "item点击:" + position);
        } else {
            showTopSnackBar(view, "item点击:" + (position - getParentHeaderFooterAdapter().getItemCount()));
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.bt_click:
                if (getParentHeaderFooterAdapter() == null) {
                    showBottomSnackBar(view, "item child 点击:" + position);
                } else {
                    showBottomSnackBar(view, "item child 点击:" + (position - getParentHeaderFooterAdapter().getItemCount()));
                }
                break;
        }
    }
}
