package com.icourt.alpha.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-05-05 10:38
 */
public class StringUtils {

    /**
     * 将异常转换成字符串
     *
     * @param throwable
     * @return
     */
    public static String throwable2string(Throwable throwable) {
        String throwableString = null;
        try {
            StringWriter mStringWriter = new StringWriter();
            PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
            throwable.printStackTrace(mPrintWriter);
            mPrintWriter.close();
            throwableString = mStringWriter.toString();
        } catch (Exception e) {
        }
        return throwableString;
    }
}
