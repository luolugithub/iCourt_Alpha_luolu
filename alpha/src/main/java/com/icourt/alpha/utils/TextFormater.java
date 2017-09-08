package com.icourt.alpha.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icourt on 16/12/7.
 */

@Deprecated
public class TextFormater {
    static final char[] firstLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z'};
    static final char[] numberLetters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static List<String> firstList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < numberLetters.length; i++) {
            list.add(String.valueOf(numberLetters[i]));
        }
        for (int i = 0; i < firstLetters.length; i++) {
            list.add(String.valueOf(firstLetters[i]));
        }
        return list;
    }

}
