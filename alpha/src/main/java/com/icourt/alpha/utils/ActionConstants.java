package com.icourt.alpha.utils;

import com.icourt.alpha.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by icourt on 16/11/18.
 */

@Deprecated
public class ActionConstants {
    public static final String APK_NAME = "alpha_release.apk";


    public static Map<String, Integer> resourcesDocumentIcon = new HashMap<String, Integer>() {
        {
            put("doc", R.mipmap.filetype_doc);
            put("wps", R.mipmap.filetype_doc);
            put("rtf", R.mipmap.filetype_doc);
            put("docx", R.mipmap.filetype_doc);

            put("jpg", R.mipmap.filetype_image_40);
            put("jpeg", R.mipmap.filetype_image_40);
            put("png", R.mipmap.filetype_image_40);
            put("gif", R.mipmap.filetype_image_40);
            put("pic", R.mipmap.filetype_image_40);

            put("pdf", R.mipmap.filetype_pdf);
            put("ppt", R.mipmap.filetype_ppt);
            put("pptx", R.mipmap.filetype_ppt);

            put("xls", R.mipmap.filetype_excel);
            put("xlsx", R.mipmap.filetype_excel);
            put("xlsm", R.mipmap.filetype_excel);

            put("zip", R.mipmap.filetype_zip);
            put("rar", R.mipmap.filetype_zip);

            put("mp3", R.mipmap.filetype_music);
            put("wav", R.mipmap.filetype_music);

            put("mp4", R.mipmap.filetype_video);
            put("avi", R.mipmap.filetype_video);
            put("ram", R.mipmap.filetype_video);
            put("rm", R.mipmap.filetype_video);
            put("mpg", R.mipmap.filetype_video);
            put("mpeg", R.mipmap.filetype_video);
            put("wmv", R.mipmap.filetype_video);

            put("httpd/unix-directory", R.mipmap.folder);
        }
    };


    public static final int DEFAULT_PAGE_SIZE = 20;//每页加载数量

    public static final String FILE_DOWNLOAD_PATH = "alpha_download";//下载文件保存路径

}
