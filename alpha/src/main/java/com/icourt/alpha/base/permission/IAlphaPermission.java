package com.icourt.alpha.base.permission;

/**
 * Description  权限基类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public interface IAlphaPermission {
    int PERMISSION_REQ_CODE_CAMERA = 60001;//权限 相机
    int PERMISSION_REQ_CODE_ACCESS_FILE = 60002;//权限 文件

    /**
     * 检查相机的权限
     *
     * @return
     */
    boolean checkCameraPermission();

    /**
     * 检查文件读写权限
     *
     * @return
     */
    boolean checkAcessFilePermission();

    /**
     * 请求相机权限
     */
    void requestCameraPermission();

    /**
     * 请求文件读写权限
     */
    void requestAcessFilePermission();
}
