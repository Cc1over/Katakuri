package com.hebaiyi.www.katakuri.util;

public class StringUtil {

    /**
     *  拼接字符串
     * @param strings 零散字符串
     * @return 拼接后的字符串
     */
    public static String buildString(String... strings){
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

}
