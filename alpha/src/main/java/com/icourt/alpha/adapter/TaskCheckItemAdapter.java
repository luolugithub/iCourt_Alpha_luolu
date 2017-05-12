package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskCheckItemEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskCheckItemAdapter extends MultiSelectRecyclerAdapter<TaskCheckItemEntity.ItemEntity> implements BaseRecyclerAdapter.OnItemChildClickListener {

    public TaskCheckItemAdapter() {
        setOnItemChildClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_check_layout;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, TaskCheckItemEntity.ItemEntity itemEntity, boolean selected, int position) {
        CheckedTextView checkedTextView = holder.obtainView(R.id.check_item_checktext_tv);
        TextView nameView = holder.obtainView(R.id.check_item_name_tv);
        ImageView deleteView = holder.obtainView(R.id.check_item_delete_image);
        if (itemEntity.state) {
            checkedTextView.setChecked(true);
            nameView.setTextColor(0xFF8c8f92);
        } else {
            checkedTextView.setChecked(false);
            nameView.setTextColor(0xFF4A4A4A);
        }
        nameView.setText(itemEntity.name);
        holder.bindChildClick(checkedTextView);
        holder.bindChildClick(deleteView);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        TaskCheckItemEntity.ItemEntity itemEntity = getItem(position);
        showLoadingDialog(view.getContext(), null);
        switch (view.getId()) {
            case R.id.check_item_checktext_tv:
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    itemEntity.state = false;
                } else {
                    itemEntity.state = true;
                }
                finisCheckItem(itemEntity);
                break;
            case R.id.check_item_delete_image:
                deleteCheckItem(itemEntity);
                break;
        }
    }

    /**
     * 修改检查项
     *
     * @param itemEntity
     */
    private void finisCheckItem(final TaskCheckItemEntity.ItemEntity itemEntity) {
        getApi().taskCheckItemUpdate(RequestUtils.createJsonBody(new Gson().toJson(itemEntity).toString())).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                updateItem(itemEntity);
            }
        });
    }

    /**
     * 删除检查项
     *
     * @param itemEntity
     */
    private void deleteCheckItem(final TaskCheckItemEntity.ItemEntity itemEntity) {
        getApi().taskCheckItemDelete(itemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                removeItem(itemEntity);
            }
        });
    }
}
