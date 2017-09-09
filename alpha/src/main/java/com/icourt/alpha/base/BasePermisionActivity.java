package com.icourt.alpha.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.icourt.alpha.R;
import com.icourt.alpha.base.permission.IAlphaPermission;
import com.icourt.alpha.base.permission.IAlphaSelectPhoto;
import com.icourt.alpha.utils.SnackbarUtils;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;

public class BasePermisionActivity extends AppCompatActivity
        implements
        IAlphaPermission,
        IAlphaSelectPhoto {

    private AlertDialog mPermissionAlertDialog;

    /**
     * 检查权限
     *
     * @param permission
     * @return
     */
    protected boolean checkPermission(@NonNull String permission) {
        if (TextUtils.isEmpty(permission)) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)// Permission was added in API Level 16
        {
            return ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * 检查权限
     *
     * @param permissions
     * @return
     */
    protected boolean checkPermissions(@NonNull String[] permissions) {
        if (permissions == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)// Permission was added in API Level 16
        {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 权限申请
     *
     * @param permission
     * @param rationaleId
     * @param requestCode
     */
    protected void reqPermission(final String permission, @StringRes int rationaleId, final int requestCode) {
        reqPermission(permission, getString(rationaleId), requestCode);
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    protected void reqPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BasePermisionActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    protected void reqPermissions(final String[] permissions, final int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mPermissionAlertDialog = builder.show();
    }

    @Override
    public boolean checkCameraPermission() {
        return checkPermission(Manifest.permission.CAMERA);
    }

    @Override
    public boolean checkAcessFilePermission() {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void requestCameraPermission() {
        reqPermission(Manifest.permission.CAMERA, R.string.permission_rationale_camera, PERMISSION_REQ_CODE_CAMERA);
    }

    @Override
    public void requestAcessFilePermission() {
        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_rationale_storage, PERMISSION_REQ_CODE_ACCESS_FILE);
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_CODE_CAMERA:
                if (grantResults != null
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    SnackbarUtils.showTopSnackBar(
                            this,
                            getString(R.string.permission_denied_camera));
                }
                break;
            case PERMISSION_REQ_CODE_ACCESS_FILE:
                if (grantResults != null
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    SnackbarUtils.showTopSnackBar(
                            this,
                            getString(R.string.permission_denied_storage));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void checkAndSelectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkAcessFilePermission()) {
            selectMutiPhotos(onHanlderResultCallback);
        } else {
            requestAcessFilePermission();
        }
    }

    @Override
    public void selectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(9)
                .build();
        GalleryFinal.openGalleryMuti(REQ_CODE_GALLERY_MUTI, config, onHanlderResultCallback);
    }

    @Override
    public void checkAndSelectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkAcessFilePermission()) {
            selectSingleFromPhotos(onHanlderResultCallback);
        } else {
            requestAcessFilePermission();
        }
    }

    @Override
    public void selectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableEdit(false)
                .setEnableCrop(false)
                .build();
        GalleryFinal.openGallerySingle(REQ_CODE_GALLERY_SINGLE, config, onHanlderResultCallback);
    }

    @Override
    public void selectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableEdit(false)
                .setEnableCrop(false)
                .build();
        GalleryFinal.openCamera(REQ_CODE_CAMERA, config, onHanlderResultCallback);
    }

    @Override
    public void checkAndSelectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkCameraPermission()) {
            selectFromCamera(onHanlderResultCallback);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPermissionAlertDialog != null
                && mPermissionAlertDialog.isShowing()) {
            mPermissionAlertDialog.dismiss();
        }
    }
}
