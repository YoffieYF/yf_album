package plugin.album.utils;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Vector;

public class StringUtils {

    public static final boolean IGNORE_CASE = true;
    public static final boolean IGNORE_WIDTH = true;

    public static boolean isNullOrEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public static boolean isNotNullOrEmpty(CharSequence sequence) {
        return !isNullOrEmpty(sequence);
    }

    public static boolean isAllWhitespaces(String str) {
        boolean ret = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public static boolean isAllDigits(String str) {
        if (str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean equal(String s1, String s2) {
        return equal(s1, s2, false);
    }

    public static boolean equal(String s1, String s2, boolean ignoreCase) {
        if (s1 != null && s2 != null) {
            if (ignoreCase) {
                return s1.equalsIgnoreCase(s2);
            } else {
                return s1.equals(s2);
            }
        } else {
            return (s1 == null && s2 == null);
        }
    }

    public static Vector<String> parseMediaUrls(String str, String beginTag, String endTag) {
        Vector<String> list = new Vector<String>();
        if (!isNullOrEmpty(str)) {
            int beginIndex = str.indexOf(beginTag, 0);
            int endIndex = str.indexOf(endTag, 0);
            while ((beginIndex != -1 && endIndex != -1) && (endIndex > beginIndex)) {
                beginIndex += beginTag.length();
                String imgUrl = str.substring(beginIndex, endIndex);
                if (!isNullOrEmpty(imgUrl) && imgUrl.charAt(0) != '[') {
                    list.add(imgUrl);
                }
                endIndex += endIndex + endTag.length();
                beginIndex = str.indexOf(beginTag, endIndex);
                endIndex = str.indexOf(endTag, endIndex);
            }
        }
        return list;
    }

    /**
     * Safe string finding (indexOf) even the arguments are empty Case sentive ver.
     */
    public static int find(String pattern, String s) {
        return find(pattern, s, !IGNORE_CASE);
    }

    /**
     * Safe string finding (indexOf) even the arguments are empty Case sentive can be parameterized
     */
    public static int find(String pattern, String s, boolean ignoreCase) {
        return find(pattern, s, ignoreCase, !IGNORE_WIDTH);
    }

    /**
     * Safe string finding (indexOf) even the arguments are empty Case sentive and Full/Half width ignore can
     * be parameterized
     */
    @SuppressLint("DefaultLocale")
    public static int find(String pattern, String s, boolean ignoreCase, boolean ignoreWidth) {
        if (isNullOrEmpty(s)) {
            return -1;
        }
        pattern = (pattern == null) ? "" : pattern;
        if (ignoreCase) {
            pattern = pattern.toLowerCase();
            s = s.toLowerCase();
        }
        if (ignoreWidth) {
            pattern = narrow(pattern);
            s = narrow(s);
        }
        return s.indexOf(pattern);
    }

    public static String narrow(String s) {
        if (isNullOrEmpty(s)) {
            return "";
        }
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; ++i)
            cs[i] = narrow(cs[i]);
        return new String(cs);
    }

    public static char narrow(char c) {
        int code = c;
        if (code >= 65281 && code <= 65373)// Interesting range
            return (char) (code - 65248); // Full-width to half-width
        else if (code == 12288) // Space
            return (char) (code - 12288 + 32);
        else if (code == 65377)
            return (char) (12290);
        else if (code == 12539)
            return (char) (183);
        else if (code == 8226)
            return (char) (183);
        else
            return c;
    }

    public static int ord(char c) {
        if ('a' <= c && c <= 'z')
            return (int) c;
        if ('A' <= c && c <= 'Z')
            return c - 'A' + 'a';
        return 0;
    }

    public static int compare(String x, String y) {
        x = (x == null) ? "" : x;
        y = (y == null) ? "" : y;
        return x.compareTo(y);
    }

    public static String toUtf8(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String fromUtf8(String utf8Str) {
        try {
            return new String(utf8Str.getBytes("UTF-8"), Charset.defaultCharset());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static boolean containsFullChar(String param) {
        char[] chs = param.toCharArray();
        for (char ch : chs) {
            if (!(('\uFF61' <= ch) && (ch <= '\uFF9F'))
                    && !(('\u0020' <= ch) && (ch <= '\u007E'))) {
                return true;
            }
        }
        return false;
    }

    public static String ensureNotNull(final String str) {
        if (str != null) {
            return str;
        } else {
            return "";
        }
    }

    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static String remain2bits(double d) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(d);
    }

    public static String remain2bits(String str) {
        try {
            return remain2bits(Double.valueOf(str));
        } catch (NumberFormatException e) {
            return str;
        }
    }

    public static String remain2bits(int i) {
        return remain2bits(Double.valueOf(i));
    }

    public static String subString(String source, int start, int length, String ellipsize) {
        if (source == null || source.length() <= length) {
            return source;
        }
        return source.substring(start, length) + ellipsize;
    }

    public static long parseLong(String s) {
        long l = 0;
        try {
            l = Long.parseLong(s.trim());
        } catch (Exception e) {
            return l;
        }
        return l;
    }

    public static String getDistance1bitValue(double val) {
        BigDecimal bd = new BigDecimal(val);
        BigDecimal i = bd.setScale(1, RoundingMode.HALF_EVEN);
        double value = i.doubleValue();
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(value);
        }
    }

    public static String doubleTrans(double num, int decimalLen) {
        String formatStr = "%." + decimalLen + "f";
        String number1 = String.format(formatStr, num);
        double number2 = Double.parseDouble(number1);
        if (Math.round(number2) - number2 == 0) {
            return String.valueOf((long) number2);
        }
        return String.valueOf(number2);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 简化数字
    ///////////////////////////////////////////////////////////////////////////

    private static final int TEN_MILLION = 10000000;
    private static final int TEN_THOUSAND = 10000;

    static double getDouble(double value, int retain) {
        int coefficient = 1;
        for (int i = 0; i < retain; i++) {
            coefficient *= 10;
        }
        int roundNumber = (int) (value * coefficient);
        return (double) roundNumber / coefficient;
    }

    public static String simplifyNum(long raw) {
        if (raw / TEN_MILLION > 0) {
            return String.format("%.2f千万", getDouble((double) raw / TEN_MILLION, 2));
        } else if (raw / TEN_THOUSAND > 0) {
            return String.format("%.2f万", getDouble((double) raw / TEN_THOUSAND, 2));
        } else {
            return String.valueOf(raw);
        }
    }

    public static String getMinuteTime(long time){
        if(time <= 0) time = 0;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static boolean isHttpUrl(String urls) {
        urls = urls.toLowerCase();
        return urls.indexOf("http") == 0;
    }

}
