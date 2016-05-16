package com.liberty.common;


import org.jsoup.select.Elements;

import static com.liberty.common.LoggingUtil.error;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class ValueParser {

    public static int parseInt(String toParse) {
        try {
            return Integer.parseInt(toParse);
        } catch (Exception e) {
            error(ValueParser.class, e);
        }
        return 0;
    }

    public static int parseInt(Elements toParse) {
        try {
            if (toParse != null && toParse.first() != null)
                return Integer.parseInt(toParse.first().text());
        } catch (Exception e) {
            error(ValueParser.class, e);
        }
        return 0;
    }
}
