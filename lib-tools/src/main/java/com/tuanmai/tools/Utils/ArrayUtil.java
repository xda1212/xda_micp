package com.tuanmai.tools.Utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 16:41
 * version 1.0
 * dsc 描述
 */

public class ArrayUtil {
    /**
     * 功能：Object数组转化为集合,已","拼接
     */
    public static <T> String list2Array(List<T> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            int size=list.size();
            for (int i = 0; i < size; i++) {
                if (i < (size - 1)) {
                    sb.append(list.get(i).toString());
                    sb.append(",");
                } else {
                    sb.append(list.get(i).toString());
                }
            }
        }
        return sb.toString().trim();
    }
    /**
     * 功能：Object数组转化为集合,已"&"拼接
     */
    public static <T> String listToArray(List<T> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            int size=list.size();
            for (int i = 0; i < size; i++) {
                if (i < (size - 1)) {
                    sb.append(list.get(i).toString());
                    sb.append("&");
                } else {
                    sb.append(list.get(i).toString());
                }
            }
        }
        return sb.toString().trim();
    }

    /**
     * 将List差分成若干个小list
     */
    public static List<List<Long>>  divideList(List<Long> targe,int size) {
        List<List<Long>> listArr = new ArrayList<List<Long>>();
        //获取被拆分的数组个数
        int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;
        for(int i=0;i<arrSize;i++) {
            List<Long>  sub = new ArrayList<Long>();
            //把指定索引数据放入到list中
            for(int j=i*size;j<=size*(i+1)-1;j++) {
                if(j<=targe.size()-1) {
                    sub.add(targe.get(j));
                }
            }
            listArr.add(sub);
        }
        return listArr;
    }

    /**
     * 功能：Object数组转化为集合（带上括号）
     */
    public static <T> String list2ArrayWithBrackets(List<T> list) {
        StringBuffer sb = new StringBuffer("[");
        if (list != null && list.size() > 0) {
            int size=list.size();
            for (int i = 0; i < size; i++) {
                if (i < (size - 1)) {
                    sb.append(list.get(i).toString());
                    sb.append(",");
                } else {
                    sb.append(list.get(i).toString());
                }
            }
        }
        sb.append("]");
        return sb.toString().trim();
    }


    /**
     * 功能：集合转化为String数组
     */
    public static List<String> array2List(String arr) {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(arr)) {
            String[] array = arr.split(",");
            list.addAll(Arrays.asList(array));
        }
        return list;
    }
}
