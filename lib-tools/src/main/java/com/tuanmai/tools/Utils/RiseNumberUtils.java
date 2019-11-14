package com.tuanmai.tools.Utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by BrodyWu
 * 16/8/8 11:29.
 * desc:滚动数字工具类
 * version:1.0
 * last-update: 16/8/8 11:29
 */
public class RiseNumberUtils {

    private static DecimalFormat dfs = null;

    public static DecimalFormat format(String pattern) {
        if (dfs == null) {
            dfs = new DecimalFormat();
        }
        dfs.setRoundingMode(RoundingMode.DOWN);
        dfs.applyPattern(pattern);
        return dfs;
    }

}
