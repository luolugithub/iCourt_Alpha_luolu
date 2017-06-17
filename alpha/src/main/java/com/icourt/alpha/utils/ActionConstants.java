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
    public static Map<String, Integer> resourcesMap = new HashMap<String, Integer>() {
        {
            put("doc", R.mipmap.filetype_doc_20);
            put("wps", R.mipmap.filetype_doc_20);
            put("rtf", R.mipmap.filetype_doc_20);
            put("docx", R.mipmap.filetype_doc_20);

            put("jpg", R.mipmap.filetype_image_20);
            put("jpeg", R.mipmap.filetype_image_20);
            put("png", R.mipmap.filetype_image_20);
            put("gif", R.mipmap.filetype_image_20);
            put("pic", R.mipmap.filetype_image_20);

            put("pdf", R.mipmap.filetype_pdf_20);
            put("ppt", R.mipmap.filetype_ppt_20);
            put("pptx", R.mipmap.filetype_ppt_20);

            put("xls", R.mipmap.filetype_excel_20);
            put("xlsx", R.mipmap.filetype_excel_20);
            put("xlsm", R.mipmap.filetype_excel_20);

            put("zip", R.mipmap.filetype_zip_20);
            put("rar", R.mipmap.filetype_zip_20);

            put("mp3", R.mipmap.filetype_music_20);
            put("wav", R.mipmap.filetype_music_20);

            put("mp4", R.mipmap.filetype_video_20);
            put("avi", R.mipmap.filetype_video_20);
            put("ram", R.mipmap.filetype_video_20);
            put("rm", R.mipmap.filetype_video_20);
            put("mpg", R.mipmap.filetype_video_20);
            put("mpeg", R.mipmap.filetype_video_20);
            put("wmv", R.mipmap.filetype_video_20);

            put("httpd/unix-directory", R.mipmap.filetype_folder_20);
        }
    };

    public static Map<String, Integer> resourcesMap40 = new HashMap<String, Integer>() {
        {
            put("doc", R.mipmap.filetype_doc_40);
            put("wps", R.mipmap.filetype_doc_40);
            put("rtf", R.mipmap.filetype_doc_40);
            put("docx", R.mipmap.filetype_doc_40);

            put("jpg", R.mipmap.filetype_image_40);
            put("jpeg", R.mipmap.filetype_image_40);
            put("png", R.mipmap.filetype_image_40);
            put("gif", R.mipmap.filetype_image_40);
            put("pic", R.mipmap.filetype_image_40);

            put("pdf", R.mipmap.filetype_pdf_40);
            put("ppt", R.mipmap.filetype_ppt_40);
            put("pptx", R.mipmap.filetype_ppt_40);

            put("xls", R.mipmap.filetype_excel_40);
            put("xlsx", R.mipmap.filetype_excel_40);
            put("xlsm", R.mipmap.filetype_excel_40);

            put("zip", R.mipmap.filetype_zip_40);
            put("rar", R.mipmap.filetype_zip_40);

            put("mp3", R.mipmap.filetype_music_40);
            put("wav", R.mipmap.filetype_music_40);

            put("mp4", R.mipmap.filetype_video_40);
            put("avi", R.mipmap.filetype_video_40);
            put("ram", R.mipmap.filetype_video_40);
            put("rm", R.mipmap.filetype_video_40);
            put("mpg", R.mipmap.filetype_video_40);
            put("mpeg", R.mipmap.filetype_video_40);
            put("wmv", R.mipmap.filetype_video_40);

            put("httpd/unix-directory", R.mipmap.filetype_folder_40);
        }
    };

    public static final int DEFAULT_PAGE_SIZE = 20;//每页加载数量

    public static final String FILE_DOWNLOAD_PATH = "alpha_download";//下载文件保存路径

}
