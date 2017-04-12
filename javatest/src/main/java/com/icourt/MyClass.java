package com.icourt;

public class MyClass {

    public static final String NAME = "name_%s";

    public static void main(String[] args) throws Exception {

        System.out.println("name hash:" + NAME.hashCode());
        String str1 = "h";
        String str2 = "h";

        System.out.println("name hash2:" + String.format(NAME, str1).hashCode());

        System.out.println("name hash2.5:" + String.format(NAME, str2).hashCode());

        System.out.println("name hash3:" + new String(String.format(NAME, str2)).hashCode());
    }
}
