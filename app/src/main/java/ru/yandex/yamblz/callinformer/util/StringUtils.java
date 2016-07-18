package ru.yandex.yamblz.callinformer.util;

/**
 * Created by root on 7/18/16.
 */
public class StringUtils {
    public static final String trim(String str, int maxLength) {
        if(str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }
}
