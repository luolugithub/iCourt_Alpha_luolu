package com.icourt.alpha.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.TaskEntity;

import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         头像组合view
 * @data 创建时间:17/3/8
 */

public class PhotoGroupView extends RelativeLayout {

    private Context context;
    private ImageView oneView, twoView, threeView;
    private TextView countView;
    private List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attends;

    public PhotoGroupView(Context context) {
        super(context);
        init(context);
    }

    public PhotoGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotoGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.photo_group_layout, this);
    }

    public void setAttends(List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attends) {
        this.attends = attends;
        this.initData();
    }

    public List<TaskEntity.TaskItemEntity.AttendeeUserEntity> getAttends() {
        return attends;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.initWidget();

    }

    private void initWidget() {
        oneView = (ImageView) findViewById(R.id.photo_group_one_view);
        twoView = (ImageView) findViewById(R.id.photo_group_two_view);
        threeView = (ImageView) findViewById(R.id.photo_group_three_view);
        countView = (TextView) findViewById(R.id.photo_group_three_count_view);
    }

    private void initData() {
        if (attends != null) {
            if (attends.size() == 1) {
                this.setVisibility(View.VISIBLE);
                oneView.setVisibility(View.VISIBLE);
                twoView.setVisibility(View.GONE);
                threeView.setVisibility(View.GONE);
                countView.setVisibility(View.GONE);
                oneView.setImageURI(Uri.parse(attends.get(0).pic));
            } else if (attends.size() == 2) {
                this.setVisibility(View.VISIBLE);
                oneView.setVisibility(View.VISIBLE);
                twoView.setVisibility(View.VISIBLE);
                threeView.setVisibility(View.GONE);
                countView.setVisibility(View.GONE);
                oneView.setImageURI(Uri.parse(attends.get(0).pic));
                twoView.setImageURI(Uri.parse(attends.get(1).pic));
            } else if (attends.size() == 3) {
                this.setVisibility(View.VISIBLE);
                oneView.setVisibility(View.VISIBLE);
                twoView.setVisibility(View.VISIBLE);
                threeView.setVisibility(View.VISIBLE);
                countView.setVisibility(View.GONE);
                oneView.setImageURI(Uri.parse(attends.get(0).pic));
                twoView.setImageURI(Uri.parse(attends.get(1).pic));
                threeView.setImageURI(Uri.parse(attends.get(2).pic));
            } else if (attends.size() > 3) {
                this.setVisibility(View.VISIBLE);
                oneView.setVisibility(View.VISIBLE);
                twoView.setVisibility(View.VISIBLE);
                threeView.setVisibility(View.VISIBLE);
                countView.setVisibility(View.VISIBLE);
                oneView.setImageURI(Uri.parse(attends.get(0).pic));
                twoView.setImageURI(Uri.parse(attends.get(1).pic));
                threeView.setImageResource(R.drawable.task_group_count_circle_bg);
                countView.setText(String.valueOf(attends.size()));
            } else {
                this.setVisibility(View.GONE);
            }
        } else {
            this.setVisibility(View.GONE);
        }
    }
}
