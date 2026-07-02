package com.useditemmarket.util;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public final class StringUtils
{
    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private static final String RANDOM_CHARS = "qw2ert1yui6opa7s3df9ghj5klz0x4cv8bnmQWERTYUIOPASDFGHJKLZXCVBNM";

    private StringUtils()
    {
    }

    public static String getRandomChar()
    {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = RANDOM_CHARS.toCharArray();
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(chars[random.nextInt(RANDOM_CHARS.length())]);
        }
        return stringBuilder.toString();
    }

    public static boolean isPhone(String phone)
    {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }
}
