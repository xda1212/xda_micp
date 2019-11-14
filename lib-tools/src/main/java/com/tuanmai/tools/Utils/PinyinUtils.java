package com.tuanmai.tools.Utils;


import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class PinyinUtils {

    /**
     * 根据传入的字符串(包含汉字),得到拼音
     *
     * @param str 字符串
     * @return
     */
    public static String getPinyin(String str) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        char[] charArray = str.toCharArray();
        int i = 0;
        while (i < charArray.length) {
            char c = charArray[i];
            // 如果是空格, 跳过
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            if (c < 128) {
                // 肯定不是汉字
                sb.append(c);
            } else {
                String s = "";
                try {
                    // 通过char得到拼音集合. 单 -> dan, shan
                    String[] strings = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (strings != null && strings.length > 0) {
                        s = strings[0];
                    }
                    sb.append(s);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    sb.append(s);
                }
            }
            i++;
        }
        return sb.toString();
    }

}
