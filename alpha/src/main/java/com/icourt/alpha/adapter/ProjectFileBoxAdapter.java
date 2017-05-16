package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;

/**
 * Description 项目详情：文档适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectFileBoxAdapter extends BaseArrayRecyclerAdapter<FileBoxBean>{

    private static final int FILE_TYPE = 0;//文件
    private static final int DIR_TYPE = 1;//文件夹


    @Override
    public int getItemViewType(int position) {
        FileBoxBean fileBoxBean = getItem(position);
        if (!TextUtils.isEmpty(fileBoxBean.type)) {
            if (TextUtils.equals("file", fileBoxBean.type)) {
                return FILE_TYPE;
            }
            if (TextUtils.equals("dir", fileBoxBean.type)) {
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
        return R.layout.adapter_item_project_file_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FileBoxBean fileBoxBean, int position) {
        if (getItemViewType(position) == FILE_TYPE) {
            setFileTypeData(holder, fileBoxBean);
        } else if (getItemViewType(position) == DIR_TYPE) {
            setDirTypeData(holder, fileBoxBean);
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

        imageView.setImageResource(FileUtils.getFileIcon20(fileBoxBean.name));
        fileName.setText(fileBoxBean.name);
        uploadName.setText(FileUtils.bFormat(fileBoxBean.size));
        if (fileBoxBean.mtime > 0) {
            uploadHour.setText(DateUtils.getTimeDateFormatMm(fileBoxBean.mtime));
        }
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
        dirName.setText(fileBoxBean.name);
    }
}
