package com.gokulsundar4545;

import java.util.Date;

public class ClsGlobal {
    static final long DAY = 24 * 60 * 60 * 1000;

    public static boolean isDateGreaterThen24Hours(long givenDateTime) {
        return givenDateTime < System.currentTimeMillis() - DAY;
    }
}
