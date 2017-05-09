package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.utils.FileUtils;

/**
 * Description 项目详情：文档适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectFileBoxAdapter extends BaseArrayRecyclerAdapter<FileBoxBean> implements BaseRecyclerAdapter.OnItemClickListener {

    private static final int FILE_TYPE = 0;//文件
    private static final int DIR_TYPE = 1;//文件夹

    @Override
    public int getItemViewType(int position) {
        FileBoxBean fileBoxBean = getData(position);
        if (!TextUtils.isEmpty(fileBoxBean.getType())) {
            if (TextUtils.equals("file", fileBoxBean.getType())) {
                return FILE_TYPE;
            } else if (TextUtils.equals("dir", fileBoxBean.getType())) {
                return DIR_TYPE;
            }
        }
        return FILE_TYPE;
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case FILE_TYPE:
                return R.layout.adapter_item_project_file_layout;
            case DIR_TYPE:
                return R.layout.adapter_item_project_dir_layout;
        }
        return 0;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FileBoxBean fileBoxBean, int position) {
        switch (holder.getItemViewType()) {
            case FILE_TYPE:
                setFileTypeData(holder,fileBoxBean);
                break;
            case DIR_TYPE:
                setDirTypeData(holder,fileBoxBean);
                break;
        }
    }

    /**
     * 设置file数据
     *
     * @param holder
     * @param fileBoxBean
     */
    private void setFileTypeData(ViewHolder holder, FileBoxBean fileBoxBean) {
        ImageView imageView = holder.obtainView(R.id.file_image);
        TextView fileName = holder.obtainView(R.id.file_name_tv);
        TextView uploadDay = holder.obtainView(R.id.file_upload_time_day_tv);
        TextView uploadName = holder.obtainView(R.id.file_upload_name_tv);
        TextView uploadHour = holder.obtainView(R.id.file_upload_time_hour_tv);

        imageView.setImageResource(FileUtils.getFileIcon20(fileBoxBean.getName()));
        fileName.setText(fileBoxBean.getName());
        uploadName.setText(FileUtils.kbFromat(fileBoxBean.getSize()));
    }

    /**
     * 设置dir数据
     *
     * @param holder
     * @param fileBoxBean
     */
    private void setDirTypeData(ViewHolder holder, FileBoxBean fileBoxBean) {
        ImageView imageView = holder.obtainView(R.id.dir_image);
        TextView dirName = holder.obtainView(R.id.dir_name);
        dirName.setText(fileBoxBean.getName());
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }
}
