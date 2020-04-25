package com.bishe.netdisc.common.utils.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author third_e
 * @create 2020/4/21 0021-下午 10:48
 */
public class DateUtil {

    public static String getDateByFormatString(String formatString, Date stringDate) {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        String date = dateFormat.format(stringDate);
        return date;
    }

}
