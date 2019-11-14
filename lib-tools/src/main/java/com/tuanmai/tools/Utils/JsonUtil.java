package com.tuanmai.tools.Utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public final class JsonUtil {

	/**
	 * 将java对象转换成json对象
	 * 
	 * @param obj
	 * @return
	 */
	public static String parseObj2Json(Object obj) {
		if (null != obj){
		    try{
                return new Gson().toJson(obj);
            }catch (Exception e){}
        }
        return null;
	}

	/**
	 * 将json对象转换成java对象
	 * 
	 * @param <T>
	 * @param jsonData
	 * @param c
	 * @return
	 */
	public static <T> Object parseJson2Obj(String jsonData, Class<T> c) {
		if (null != jsonData){
            try {
                return new Gson().fromJson(jsonData.trim(), c);
            } catch (Exception e) {}
        }
		return null;
	}

	/**
	 * 将json对象转换成数组对象
	 * 
	 * @param <T>
	 * @param jsonData
	 * @param c
	 * @return
	 * @throws JSONException
	 */
	public static <T> ArrayList<T> parseJson2List(String jsonData, Class<T> c) {
		ArrayList<T> list = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonData)){
            try {
                JSONArray jsonArray = new JSONArray(jsonData.trim());
                int length = jsonArray.length();
                Gson gson = new Gson();
                for (int i = 0; i < length; i++) {
                    list.add(gson.fromJson(jsonArray.getString(i), c));
                }
            }catch (Exception e){

            }
        }
		return list;
	}

}
