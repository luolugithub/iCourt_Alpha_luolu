package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.CustomerEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class CustomerAdapter extends BaseArrayRecyclerAdapter<CustomerEntity> {

    private boolean isShowCustomerNum;

    public void setShowCustomerNum(boolean isShowCustomerNum) {
        if (isShowCustomerNum != this.isShowCustomerNum) {
            this.isShowCustomerNum = isShowCustomerNum;
            notifyDataSetChanged();
        }
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_customer;
    }

    @Override
    public void onBindHoder(ViewHolder holder, CustomerEntity customerEntity, int position) {
        if (customerEntity == null) return;
        ImageView iv_customer_icon = holder.obtainView(R.id.iv_customer_icon);
        TextView tv_customer_name = holder.obtainView(R.id.tv_customer_name);
        TextView customer_num_tv = holder.obtainView(R.id.customer_num_tv);


        if (!TextUtils.isEmpty(customerEntity.contactType)) {
            //公司
            if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                iv_customer_icon.setImageResource(R.mipmap.company);
            } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                if (TextUtils.equals(customerEntity.sex, "女")) {
                    iv_customer_icon.setImageResource(R.mipmap.female);
                } else //显示为男
                {
                    iv_customer_icon.setImageResource(R.mipmap.male);
                }
            }
        }
        tv_customer_name.setText(customerEntity.name);
        boolean showCustomerNumTv = (isShowCustomerNum && position == getItemCount() - 1);
        customer_num_tv.setVisibility(showCustomerNumTv
                ? View.VISIBLE : View.GONE);
        if (showCustomerNumTv) {
            customer_num_tv.setText(String.format("%s 位成员", getItemCount() - 1));
        }
    }
}
