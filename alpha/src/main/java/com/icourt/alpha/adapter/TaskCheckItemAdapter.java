package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskCheckItemEntity;
import com.icourt.alpha.utils.SystemUtils;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskCheckItemAdapter extends MultiSelectRecyclerAdapter<TaskCheckItemEntity.ItemEntity> {

    OnLoseFocusListener onLoseFocusListener;
    boolean valid;

    public void setOnLoseFocusListener(OnLoseFocusListener onLoseFocusListener) {
        this.onLoseFocusListener = onLoseFocusListener;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_check_layout;
    }

    @Override
    public void onBindSelectableHolder(final ViewHolder holder, final TaskCheckItemEntity.ItemEntity itemEntity, boolean selected, final int position) {
        ImageView checkedTextView = holder.obtainView(R.id.check_item_checktext_tv);
        final EditText nameView = holder.obtainView(R.id.check_item_name_tv);
        ImageView deleteView = holder.obtainView(R.id.check_item_delete_image);
        if (itemEntity.state) {
            checkedTextView.setImageResource(R.mipmap.checkbox_square_checked_highlight);
            nameView.setTextColor(0xFF8c8f92);
        } else {
            checkedTextView.setImageResource(R.mipmap.checkbox_square);
            nameView.setTextColor(0xFF4A4A4A);
        }
        checkedTextView.setClickable(valid);
        deleteView.setVisibility(valid ? View.VISIBLE : View.GONE);
        nameView.setText(itemEntity.name);
        nameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.equals(nameView.getText().toString(), itemEntity.name)) {
                        //如果内容没有改变，就不走接口了。
                        return;
                    }
                    if (onLoseFocusListener != null) {
                        onLoseFocusListener.loseFocus(itemEntity, position, nameView.getText().toString());
                    }
                }
            }
        });
        //屏蔽回车键 回车键表示完成
        nameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    holder.itemView.setFocusable(true);
                    holder.itemView.setFocusableInTouchMode(true);
                    holder.itemView.requestFocus();//请求焦点
                    holder.itemView.findFocus();//获取焦点
                    SystemUtils.hideSoftKeyBoard(textView.getContext(), textView);
                    return true;
                }
                return false;
            }
        });
        holder.bindChildClick(checkedTextView);
        holder.bindChildClick(deleteView);
    }


    public interface OnLoseFocusListener {
        void loseFocus(TaskCheckItemEntity.ItemEntity itemEntity, int position, String name);
    }

}
