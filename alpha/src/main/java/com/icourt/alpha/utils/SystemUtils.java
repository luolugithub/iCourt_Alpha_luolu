package com.icourt.alpha.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.icourt.alpha.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemUtils {

    private static final String KEY_KEYBOARD_HEIGHT = "KEKE_KEYBOARDHEIGHT";

    /**
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 启动系统自带摄像头拍照
     *
     * @param context     要调用的Activity对象
     * @param uri         拍照生成的文件uri
     * @param requestCode 完成后进行的回调参数值,请监听onActivityResult函数
     */
    public static void doTakePhotoAction(Activity context, Uri uri,
                                         int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        try {
            // intent.putExtra("return-data", false);

            context.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void doTakePhotoAction(Fragment context, Uri uri,
                                         int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        try {
            // intent.putExtra("return-data", false);

            context.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动系统自带相册
     *
     * @param context     要调用的Activity对象
     * @param requestCode 完成后进行的回调参数值,请监听onActivityResult函数,通过data.getData()可获取选中的图片的uri
     */
    public static void lauchAlbum(Activity context, int requestCode) {
        Intent getImage = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // getImage.setType("image/*");
        try {
            context.startActivityForResult(getImage, requestCode);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void lauchAlbum(Fragment context, int requestCode) {
        Intent getImage = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // getImage.setType("image/*");
        try {
            context.startActivityForResult(getImage, requestCode);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * @param activity
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    public static void cropImageUri(Activity activity, Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 通过uri获取真实的相册路径
     *
     * @param uri      相册选择后返回的路径
     * @param resolver ContentResolver对象,activity中可以通过getContentResolver()获取
     */
    public static String getImageRealPathFromURI(Uri uri,
                                                 ContentResolver resolver) {
        String path = "";
        String url = uri.toString();
        if (url.startsWith("file:///")) {
            path = url.substring(8);
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = resolver.query(uri, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);
            cursor.close();
        }
        return path;
    }


    /**
     * 检测某Activity是否在当前Task的栈顶
     */
    public static boolean isTopActivy(Context context, String cmdName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;

        if (null != runningTaskInfos) {
            RunningTaskInfo info = (RunningTaskInfo) runningTaskInfos.get(0);
            cmpNameTemp = info.topActivity.toString();
        }

        if (null == cmpNameTemp) return false;
        return cmpNameTemp.equals(cmdName);
    }

    /**
     * 判断应用是否在运行
     */
    public static boolean isRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())
                    || info.baseActivity.getPackageName().equals(context.getPackageName())) {
                isAppRunning = true;
                break;
            }
        }

        return isAppRunning;
    }


    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideSoftKeyBoard(Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideSoftKeyBoard(Activity act, boolean clearFouces) {
        try {
            if (clearFouces) {
                View currentFocus = act.getCurrentFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }
            }
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘2
     *
     * @param act
     */
    public static void hideSoftKeyBoard(Context act, View v) {
        try {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    /**
     * 隐藏软键盘2
     *
     * @param act
     */
    public static void hideSoftKeyBoard(Context act, View v, boolean clearFouces) {
        try {
            if (v != null && clearFouces) {
                v.clearFocus();
            }
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    /**
     * 显示软键盘
     *
     * @param act
     */
    public static void showSoftKeyBoard(Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(act.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
        }
    }

    /**
     * 显示软键盘2
     *
     * @param act
     */
    public static void showSoftKeyBoard(Context act, View v) {
        try {
            if (v != null) v.requestFocus();
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
        }
    }


    /**
     * 获得缓存目录
     *
     * @return
     */
    public static String getFileDiskCache(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || Environment.isExternalStorageRemovable()) {
            // sdcard路径
            File file = new File(context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES).getPath()
                    + File.separator + "takephoto");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            // 缓存
            File file = new File(context.getFilesDir().getPath()
                    + File.separator + "takephoto");
            if (!file.exists()) {
                file.mkdirs();
            }

            return file.getAbsolutePath();
        }
    }

    public static void showMultiChoiceAlbum(Activity activity) {
        if (activity == null) return;
//        Intent intent = new Intent(activity, LocalAlbum.class);
//        activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
//        hideSoftKeyBoard(activity);
    }


    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPixels(Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm == null ? 0 : dm.heightPixels;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidthPixels(Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm == null ? 0 : dm.widthPixels;
    }

    /**
     * 键盘高度
     *
     * @param context
     * @return
     */
    public static int getKeyBoardHeight(Context context) {
        int height = 0;
        try {
            height = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_KEYBOARD_HEIGHT, 0);
        } catch (Exception e) {
        }
        return height;
    }

    /**
     * 保存键盘高度
     *
     * @param context
     * @param height
     */
    public static void setKeyBoardHeight(Context context, int height) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_KEYBOARD_HEIGHT, height).commit();
        } catch (Exception e) {
        }
    }

    /**
     * 状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Context activity) {
        if (activity == null) return 0;
        int height = 0;
        try {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        } catch (Resources.NotFoundException e) {
        }
        return height;
    }

    /**
     * 是否包含虚拟导航栏
     *
     * @param context
     * @return
     */
    public static boolean hasNavigationBar(Context context) {
        try {
            int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                return context.getResources().getBoolean(id);
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * 导航栏高度 有高度 也可能隐藏 建议先 hasNavigationBar(Context context)判断
     *
     * @param activity
     * @return
     */
    public static int getNavigationBarHeight(Context activity) {
        if (activity == null) return 0;
        int height = 0;
        try {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        } catch (Resources.NotFoundException e) {
        }
        return height;
    }

    /**
     * 复制到粘贴板
     *
     * @param context
     * @param lable
     * @param charSequence
     */
    public static void copyToClipboard(@NonNull Context context, @Nullable String lable, @NonNull CharSequence charSequence) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb == null) return;
        cmb.setPrimaryClip(ClipData.newPlainText(lable, charSequence)); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
    }

    /**
     * 获取粘贴板内容
     *
     * @param context
     * @return
     */
    public static CharSequence getFromClipboard(@NonNull Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null) {
            if (cmb.hasPrimaryClip()) {
                if (cmb.getPrimaryClip().getItemCount() > 0) {
                    return cmb.getPrimaryClip().getItemAt(0).getText();
                }
            }
        }
        return null;
    }

    /**
     * 检查权限
     *
     * @param permissions
     * @return
     */
    public static boolean checkPermissions(@NonNull Context context, @NonNull String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)// Permission was added in API Level 16
        {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取权限
     *
     * @param context
     * @param permissions
     * @param requestCode
     */
    public static void reqPermission(@NonNull Activity context, final String[] permissions, final int requestCode) {
        ActivityCompat.requestPermissions(context, permissions, requestCode);
    }

    /**
     * 检查是否在主线程
     *
     * @return
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    /**
     * 是否在主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        if (context != null) {
            String packageName = context.getPackageName();
            String processName = getProcessName(context);
            return packageName.equals(processName);
        }
        return false;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 检查是否允许通知 尤其是Toast
     *
     * @param context
     * @return
     */
    public static boolean isEnableNotification(@NonNull Context context) {
        try {
            final String CHECK_OP_NO_THROW = "checkOpNoThrow";
            final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    appOpsClass = Class.forName(AppOpsManager.class.getName());
                    Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                    Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                    int value = (int) opPostNotificationValue.get(Integer.class);
                    return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
        }
        return true;
    }


    /**
     * 启动手机设置界面
     *
     * @param context
     */
    public static void launchPhoneSettings(@NonNull Context context) {
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    /*
    * 判断手机是否正确   +86的
    * */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 启动 存储设置界面【内部存储】
     *
     * @param context
     */
    public static void launchInterStorageSettings(@NonNull Context context) {
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            context.startActivity(intent);
        }
    }

    /**
     * ContextCompat.getColor() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     * 默认返回值为黑色
     *
     * @param context
     * @param id
     */
    @ColorInt
    public static int getColor(Context context, @ColorRes int id) {
        return getColor(context, id, Color.BLACK);
    }

    /**
     * ContextCompat.getColor() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     *
     * @param context
     * @param id      如果不存在返回默认值                                                        does not exist.
     */
    @ColorInt
    public static int getColor(Context context, @ColorRes int id, @ColorInt int defaultColor) {
        if (context == null) return defaultColor;
        try {
            return ContextCompat.getColor(context, id);
        } catch (android.content.res.Resources.NotFoundException e) {
        }
        return defaultColor;
    }

    /**
     * ContextCompat.getColor() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     *
     * @param context
     * @param id      如果不存在返回默认值                                                        does not exist.
     */
    @CheckResult
    @Nullable
    public static ColorStateList getColorStateList(Context context, @ColorRes int id) {
        if (context == null) return null;
        try {
            return ContextCompat.getColorStateList(context, id);
        } catch (android.content.res.Resources.NotFoundException e) {
        }
        return null;
    }

    /**
     * ContextCompat.getDrawable() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     * 默认返回值为黑色
     *
     * @param context
     * @param id
     */
    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        return getDrawable(context, id, R.mipmap.ic_launcher);
    }

    /**
     * ContextCompat.getDrawable() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     *
     * @param context
     * @param id      如果不存在返回默认值                                                        does not exist.
     */
    @Nullable
    public static Drawable getDrawable(Context context, @DrawableRes int id, @DrawableRes int defaultDrawable) {
        if (context == null) return null;
        Drawable drawable = null;
        try {
            drawable = ContextCompat.getDrawable(context, id);
        } catch (android.content.res.Resources.NotFoundException e) {
            try {
                drawable = ContextCompat.getDrawable(context, defaultDrawable);
            } catch (android.content.res.Resources.NotFoundException e1) {
            }
        }
        return drawable;
    }


    /**
     * 是否销毁或者正在销毁
     *
     * @return
     */
    public static final boolean isDestroyOrFinishing(@NonNull Activity activity) {
        if (activity == null) return true;
        boolean destroyed = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                activity.isDestroyed();
        return destroyed || activity.isFinishing();
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phone
     */
    public static final void callPhone(Context context, String phone) {
        if (context == null) return;
        if (TextUtils.isEmpty(phone)) return;
        if (TextUtils.isEmpty(phone.trim())) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phone.trim());
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 发送到邮件
     *
     * @param context
     * @param email
     * @throws android.content.ActivityNotFoundException
     */
    public static void sendEmail(Context context, String email) throws android.content.ActivityNotFoundException {
        if (context == null) return;
        if (TextUtils.isEmpty(email)) return;
        if (TextUtils.isEmpty(email.trim())) return;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email.trim()});
        i.putExtra(Intent.EXTRA_SUBJECT, "标题");
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        context.startActivity(Intent.createChooser(i, "Send mail"));
    }

    /**
     * 打开文件管理器
     * @param context
     * @param reqCode
     */
    public static final void chooseFile(Activity context, int reqCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            context.startActivityForResult(Intent.createChooser(intent, "选择文件"), reqCode);
        } catch (android.content.ActivityNotFoundException ex) {
            SnackbarUtils.showTopSnackBar(context, "亲，木有文件管理器啊-_-!!");
        }
    }


}
