package com.icourt;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/6/29
 * version 2.0.0
 */

public class StringFormatTest {

    public static void main(String[] args) throws Exception {
        String name1 = "@a改 你好的阿妹啊等；卢卡斯的1";
        String name2 = "@1吧 你好的阿妹啊等；卢卡斯的2";
        String name3 = "@分三 你好的阿妹啊等；卢卡斯的3";
        String name4 = "@分三你好的阿妹啊等；卢卡斯的4";
        String name5 = "@分三 @你好 的阿妹啊等；卢卡斯的5";
        String name6 = "@分三 @你好的阿妹啊等；卢卡斯的6";
        String name7 = "@分三@你好 的阿妹啊等；卢卡斯的7";
        String name8 = "@分三@你好的阿妹啊等；卢卡斯的8";

        String regularcode = "@\\d{2}";
        System.out.print("");

    }

}
