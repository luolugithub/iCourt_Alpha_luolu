package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;

import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_ANYUAN_LAWYER_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_CLIENT_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_DEPARTMENT_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_NAME_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_OTHER_PERSON_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_TIME_TYPE;
import static com.icourt.alpha.entity.bean.ProjectBasicItemEntity.PROJECT_TYPE_TYPE;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/17
 * version 2.0.0
 */

public class ProjectBasicInfoAdapter extends BaseArrayRecyclerAdapter<ProjectBasicItemEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_project_basic_info_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectBasicItemEntity projectBasicItemEntity, int position) {
        ImageView iconView = holder.obtainView(R.id.key_icon);
        TextView keyView = holder.obtainView(R.id.key_name_tv);
        TextView valueView = holder.obtainView(R.id.value_name_tv);
        iconView.setImageResource(getImageByType(projectBasicItemEntity.type));
        keyView.setText(projectBasicItemEntity.key);
        valueView.setText(projectBasicItemEntity.value);
    }

    private int getImageByType(int type) {
        switch (type) {
            case PROJECT_NAME_TYPE://项目名称
                return R.mipmap.project_name_icon;
            case PROJECT_TYPE_TYPE://项目类型
                return R.mipmap.project_type_icon;
            case PROJECT_DEPARTMENT_TYPE://负责部门
                return R.mipmap.project_department_icon;
            case PROJECT_CLIENT_TYPE://客户
                return R.mipmap.project_customer_icon;
            case PROJECT_OTHER_PERSON_TYPE://其他当事人
                return R.mipmap.project_user_icon;
            case PROJECT_TIME_TYPE://项目时间
                return R.mipmap.project_time_icon;
            case PROJECT_ANYUAN_LAWYER_TYPE://案源律师
                return R.mipmap.project_user_icon;
        }
        return R.mipmap.project_name_icon;
    }
}
