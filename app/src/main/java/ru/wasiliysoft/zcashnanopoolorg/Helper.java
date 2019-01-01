package ru.wasiliysoft.zcashnanopoolorg;

import java.text.DecimalFormat;

/**
 * Created by WasiliySoft on 26.11.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Helper {

    public static String getRoundEarnings(Double d) {
        if (d == null) {
            return "0";
        }
        int round = 5;

        String suffix = "";
        if (d > 1000) {
            suffix = "k";
            d = d / 1000;
            round = 1;
        } else if (d > 1) {
            round = 2;
        } else if (d > 0.1) {
            round = 3;
        } else if (d > 0.01) {
            round = 4;
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(round);
        df.setMinimumFractionDigits(round);
        df.setGroupingSize(3);
//        Log.d("getRound", d.toString());

        return String.valueOf(df.format(d)) + suffix;
    }
}
