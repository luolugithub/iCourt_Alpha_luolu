package com.icourt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyClass {

    public static final String NAME = "name_%s";
    private static Comparator<Long> longComparator = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2);
            }
            return 0;
        }
    };
    private static Comparator<Long> longComparator2 = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            if (o1 != null && o2 != null) {
                return o2.compareTo(o1);
            }
            return 0;
        }
    };
    public static void main(String[] args) throws Exception {

        System.out.println("name hash:" + NAME.hashCode());
        String str1 = "h";
        String str2 = "h";

        System.out.println("name hash2:" + String.format(NAME, str1).hashCode());

        System.out.println("name hash2.5:" + String.format(NAME, str2).hashCode());

        System.out.println("name hash3:" + new String(String.format(NAME, str2)).hashCode());
        System.out.println("isPic:" + isPIC("http://."));
        System.out.println("isPic2:" + isPIC("http://.doc"));
        System.out.println("isPic3:" + isPIC("http://xx.jpg"));
        System.out.println("isPic4:" + isPIC("http://"));
        System.out.println("isPic4:" + isPIC("http://.q"));


        List<Long> list=new ArrayList<>();
        list.add(3L);
        list.add(1L);
        list.add(4L);
        list.add(2L);
        log("------list:"+list);
        Collections.sort(list,longComparator);
        log("------list2:"+list);
        Collections.sort(list,longComparator2);
        log("------list3:"+list);

    }

    private static void log(String log) {
        System.out.println(log);
    }

    /**
     * 是否是图片
     *
     * @param url
     * @return
     */
    public static final boolean isPIC(String url) {
        int pointIndex = url.lastIndexOf(".");
        if (pointIndex >= 0 && pointIndex < url.length()) {
            String fileSuffix = url.substring(pointIndex, url.length());
            System.out.println("fileSuffix :" + fileSuffix);
            return getPICSuffixs().contains(fileSuffix);
        }
        return false;
    }

    /**
     * 图片后缀
     *
     * @return
     */
    public static final List<String> getPICSuffixs() {
        return Arrays.asList(".png", ".jpg", ".gif", ".jpeg", ".PNG", ".JPG", ".GIF", ".JPEG");
    }
}
