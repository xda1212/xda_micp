package com.tuanmai.tools.Utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	// MD5変換
	public static String Md5(String str) {
		if (str != null && !str.equals("")) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
						'9', 'a', 'b', 'c', 'd', 'e', 'f' };
				byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Byte.length; i++) {
					sb.append(HEX[(md5Byte[i] & 0xff) / 16]);
					sb.append(HEX[(md5Byte[i] & 0xff) % 16]);
				}
				str = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				LogUtil.e(e);
			} catch (Exception e) {
				LogUtil.e(e);
			}
		}
		return str;
	}

	/**
	 * Get the md5 value of the filepath specified file
	 * 
	 * @param filePath
	 *            The filepath of the file
	 * @return The md5 value
	 */
	public static String fileToMD5(String filePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			byte[] buffer = new byte[1024 * 2]; // The buffer to read the file
			MessageDigest digest = MessageDigest.getInstance("MD5");
			int numRead = 0; // Record how many bytes have been read
			while (numRead != -1) {
				numRead = inputStream.read(buffer);
				if (numRead > 0)
					digest.update(buffer, 0, numRead); // Update the digest
			}
			byte[] md5Bytes = digest.digest(); // Complete the hash computing
			return convertHashToString(md5Bytes); // Call the function to
													// convert to hex digits
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close(); // Close the InputStream
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Convert the hash bytes to hex digits string
	 * 
	 * @param hashBytes
	 * @return The converted hex digits string
	 */
	private static String convertHashToString(byte[] hashBytes) {
		String returnVal = "";
		for (int i = 0; i < hashBytes.length; i++) {
			returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return returnVal.toLowerCase();
	}

}
