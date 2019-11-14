package com.tuanmai.tools.Utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

public class ChineseUtil {
	static final int GB_SP_DIFF=160;
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 
    	3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 }; 
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 
    	'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z' }; 
	
    /**
     * return The first letter,if Not Chinese characters,return #
     * **/
    public static String getSpells(String characters) throws Exception{

        characters=characters.trim();
		if (TextUtils.isEmpty(characters)) return "#";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < characters.length(); i++) {
			char ch = characters.charAt(i);
			if ((ch >> 7) == 0) {
				return "#";
			} else {
				char spell = getFirstLetter(ch);
				buffer.append(String.valueOf(spell));
			}
		}
		return buffer.toString();
    }
    
   /**
    * return first Chinese characters,return #
    * @param ch
    * @return Character
    */
    public static Character getFirstLetter(char ch) throws UnsupportedEncodingException{ 
        byte[] uniCode = String.valueOf(ch).getBytes("GBK"); 
        if (uniCode[0] < 128 && uniCode[0] > 0) { //not chinese
            return null; 
        } else { 
            return convert(uniCode); 
        } 
    } 
    /**
     *Match the conversion
     */ 
    static char convert(byte[] bytes) { 
        char result = '-'; 
        int secPosValue;
        int i; 
        for (i = 0; i < bytes.length; i++) { 
            bytes[i] -= GB_SP_DIFF; 
        } 
        secPosValue = bytes[0] * 100 + bytes[1]; 
        for (i = 0; i < 23; i++) { 
            if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) { 
                result = firstLetter[i]; 
                break; 
            } 
        } 
        return result; 
    } 
    
    
    /**
     * The Angle to Half Angle
     * */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
    
}
