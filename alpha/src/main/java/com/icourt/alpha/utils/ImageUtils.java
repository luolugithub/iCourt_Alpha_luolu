package com.icourt.alpha.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.LocalImageEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import cn.finalteam.galleryfinal.utils.ILogger;

/**
 * @author 创建人:lu.zhao
 * @data 创建时间:16/11/8
 */

public class ImageUtils {

    private static int[] groupPhotos = {R.mipmap.color_1,
            R.mipmap.color_2, R.mipmap.color_3, R.mipmap.color_4, R.mipmap.color_5, R.mipmap.color_6, R.mipmap.color_7};
//    public static String dirFilePath = FileUtils.getSDPath() + ActionConstants.FILE_DOWNLOAD_PATH;

    public static String encodeImageByPath(String path) {
        try {
            //decode to bitmap
            compressImageByPixel(path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            baos.close();
            //base64 encode
            byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);
            String encodeString = new String(encode, "UTF-8");
            return encodeString;
        } catch (Exception e) {

        }
        return null;
    }

    public static String encodeBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);
            String str = new String(encode, "UTF-8");
            return str;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 多线程压缩图片的质量
     *
     * @param bitmap
     * @param imgPath
     */
    public static void compressImageByQuality(final Bitmap bitmap, final String imgPath) {

        // TODO Auto-generated method stub
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        while (baos.toByteArray().length / 1024 > 300) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即让下一次的写入覆盖之前的内容
            options -= 10;//图片质量每次减少10
            if (options < 0) options = 0;//如果图片质量小于10，则将图片的质量压缩到最小值
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
            if (options == 0) break;//如果图片的质量已降到最低则，不再进行压缩
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(imgPath));//将压缩后的图片保存的本地上指定路径中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按比例缩小图片的像素以达到压缩的目的
     *
     * @param imgPath
     */
    public static void compressImageByPixel(String imgPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        float maxSize = 1000f;//默认1000px
        int be = 1;
        if (width > height && width > maxSize) {//缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxSize);
        } else if (width < height && height > maxSize) {
            be = (int) (newOpts.outHeight / maxSize);
        }
        newOpts.inSampleSize = be;//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(imgPath));
        compressImageByQuality(bitmap, imgPath);//压缩好比例大小后再进行质量压缩
    }

    public static void degreeImage(String imgPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(imgPath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(new File(imgPath));//将压缩后的图片保存的本地上指定路径中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree
                = 0;
        try {
            //从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            //获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap
                returnBm = null;

        //根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            //将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 获取所有图片
     *
     * @param context
     * @return
     */
    public static List<LocalImageEntity> getAllPhotoFolder(Context context) {
        List<LocalImageEntity> allFolderList = new ArrayList<>();
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA,
                MediaStore.Images.Media.SIZE

        };
        Cursor cursor = null;
        try {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int thumbnailsDataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int imageSizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);

                    int imageId = cursor.getInt(imageIdColumn);
                    String path = cursor.getString(dataColumn);
                    String thumbnailsPath = cursor.getString(thumbnailsDataColumn);
                    long size = cursor.getLong(imageSizeColumn);

                    LocalImageEntity photoInfo = new LocalImageEntity();
                    photoInfo.photoId = imageId;
                    photoInfo.photoPath = path;
                    photoInfo.thumbPath = thumbnailsPath;
                    photoInfo.size = size;

//                    String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
//                            MediaStore.Images.Thumbnails.DATA};
//                    Cursor cur = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
//                            MediaStore.Images.Thumbnails.IMAGE_ID + "=" + imageId, null, null);
//                    if (cur != null) {
//                        cur.moveToFirst();
//                        photoInfo.setThumbPath(cur.getString(cur
//                                .getColumnIndex(MediaStore.Images.Thumbnails.DATA)));
//                    }


                    allFolderList.add(photoInfo);
                    if (allFolderList.size() >= 20) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.d("-------load img Exception --  " + ex.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return allFolderList;
    }

    /**
     * 获取所有图片
     *
     * @param context
     * @return
     */
    public static List<LocalImageEntity> getAllPhotoFolderThumb(Context context) {
        List<LocalImageEntity> allFolderList = new ArrayList<>();
        final String[] projectionPhotos = {
                MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA

        };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projectionPhotos,
                    null, null, "image_id DESC");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int _id;
                    int image_id;
                    String image_path;
                    int _idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID);
                    int image_idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
                    int dataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

                    do {
                        // Get the field values
                        _id = cursor.getInt(_idColumn);
                        image_id = cursor.getInt(image_idColumn);
                        image_path = cursor.getString(dataColumn);

                        LocalImageEntity photoInfo = new LocalImageEntity();
                        photoInfo.photoId = image_id;
//                        photoInfo.setPhotoPath(image_path);
                        photoInfo.thumbPath = image_path;
//                        photoInfo.setSize(100);

                        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
                        Cursor cur = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                                MediaStore.Images.Media._ID + "=" + image_id, null, null);
                        if (cur != null) {
                            cur.moveToFirst();
                            photoInfo.photoPath = (cur.getString(cur
                                    .getColumnIndex(MediaStore.Images.Media.DATA)));
                            photoInfo.size = (cur.getLong(cur
                                    .getColumnIndex(MediaStore.Images.Media.SIZE)));
                        }
                        allFolderList.add(photoInfo);
                        if (allFolderList.size() >= 20) {
                            break;
                        }

                    } while (cursor.moveToNext());

                }
            }
        } catch (Exception ex) {
            ILogger.e(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(allFolderList);
        return new ArrayList<>(linkedHashSet);
    }

    /**
     * 获取讨论组头像
     *
     * @param name
     * @return
     */
    public static int getGroupPhoto(String name) {
        if (!TextUtils.isEmpty(name)) {
            Character oneChar, twoChar;
            int oneIndex, twoIndex;
            List<String> list = TextFormater.firstList();
            if (name.length() > 1) {
                char s1 = name.charAt(0);
                oneChar = TextFormater.getFirstLetter(s1);
                if (!list.contains(String.valueOf(oneChar))) {
                    oneIndex = 27;
                    twoIndex = 0;
                }
                char s2 = name.charAt(1);
                twoChar = TextFormater.getFirstLetter(s2);
                if (!list.contains(String.valueOf(twoChar))) {
                    twoIndex = 0;
                }
                oneIndex = list.indexOf(String.valueOf(oneChar));
                twoIndex = list.indexOf(String.valueOf(twoChar));
                if ("i".equals(String.valueOf(oneChar))) {
                    return groupPhotos[2];
                }


                int index = (oneIndex + twoIndex) / 7;
                if (index >= groupPhotos.length) {
                    index = index / 7;
                }
                return groupPhotos[index];
            } else if (name.length() == 1) {
                oneChar = TextFormater.getFirstLetter(name.charAt(0));
                oneIndex = list.indexOf(String.valueOf(oneChar));
                if ("i".equals(String.valueOf(oneChar))) {
                    return groupPhotos[2];
                }
                int index = oneIndex / 7;
                if (index >= groupPhotos.length) {
                    index = index / 7;
                }
                return groupPhotos[index];
            }
        }
        return 0;
    }

    /**
     * 添加到图库 拍照后的照片保存在内存中 在此，将内存中图片添加到图库（也就是SD卡中）,这样可以在手机的图库程序中看到程序拍摄的照片
     */
    public static void addPictureToGallery(Context context, String path) {

//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
//        Uri uri = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, com.icourt.alpha.BuildConfig.APPLICATION_ID + ".provider", file);
//        } else {
//            uri = Uri.fromFile(file);
//        }
//        mediaScanIntent.setData(uri);
//        context.sendBroadcast(mediaScanIntent);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);//图片插入到系统图库
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));//通知图库刷新
    }
    
}
