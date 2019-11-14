package com.tuanmai.tools.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 文件帮助类
 * 
 * @FileName cn.eshore.wepi.utils.FileHelper.java
 * @version V1.0
 */
public class FileUtil {

	/**
	 * 获取磁盘的图片
	 */
	public static Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 判断是否是文件
	 */
	public static boolean isFile(String path) {
		File file = new File(path);
        return file.exists() && !file.isDirectory();
	}

	/**
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @param sizeType
	 *            获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getDirSize(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fcalculateSize(blockSize,sizeType);
	}

	/**
	 * 获取指定文件夹大小
	 * 
	 * @param dirFile
	 * @return
	 * @throws Exception
	 */
	public static long getDirSize(File dirFile) throws Exception {
		long size = 0;
		File flist[] = dirFile.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getDirSize(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
		}
		return size;
	}


    /**
     *
     * 计算文件大小
     *
     * @param size
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     *
     * @return String
     */
    public static double fcalculateSize(long size,int sizeType) {
        long SIZE_KB = 1024;//1KB等于1024个字节
        long SIZE_MB = SIZE_KB * 1024;
        long SIZE_GB = SIZE_MB * 1024;
        switch (sizeType){
            case 2:
                size=size/SIZE_KB;
                break;

            case 3:
                size=size/SIZE_MB;
                break;

            case 4:
                size=size/SIZE_GB;
                break;
        }
        return size;
    }



	/**
	 * 删除文件或目录
	 * @Title: clearCache
	 * @Description: 清空缓存
	 * @param path 文件夹完整绝对路径
	 * @throws
	 */
	public static void delAllFile(String path) {
        delAllFile(new File(path));
	}

    /**
     * 删除文件或目录
     * @Title: clearCache
     * @Description: 清空缓存
     * @param file
     * @throws
     */
    public static void delAllFile(File file) {
        if (null==file || !file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            file.delete();
            return;
        }

        File[] files=file.listFiles();
        for (File itemFile:files) {
            if(itemFile.isDirectory()){
                delAllFile(itemFile);
            }
            itemFile.delete();
        }
    }


	/**
	 * 保存图片
	 * @param bm
	 *            下载完得到的Bitmap
	 * @param path
	 *            保存路径
     * @param name
     *            文件名
	 * @returnType void
	 */
	public static boolean saveFile(Bitmap bm, String path,String name,CompressFormat format) {
		try {
			File file = new File(path, name);
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			bm.compress(format, 100, outStream);
			outStream.flush();
			outStream.close();
            return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

    public static boolean savePngFile(Bitmap bm, String path, String name) {
        return saveFile(bm,path,name,CompressFormat.PNG);
    }

    public static boolean saveJpgFile(Bitmap bm, String path, String name) {
        return saveFile(bm,path,name,CompressFormat.JPEG);
    }

	/**
	 * 保存文件
	 */
	public static boolean saveFile(InputStream inputStream, String path,String name) {
		if (null != inputStream) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(path,name);
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buffer = new byte[512];
                int count;
                while ((count = inputStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, count);
                }
                outStream.flush();
                outStream.close();
                inputStream.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
		}
        return false;
	}


	/**
	 * 根据url获取文件名
	 * @param url
	 * @return
	 * @returnType String
	 */
	public static String getNameByUrl(String url) {
		return url.substring(url.lastIndexOf('/') + 1,url.length());
	}

	/**
	 * 判断SD卡是否存在
	 * @return
	 * @returnType boolean
	 */
	public static boolean existSDCard() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		return sdCardExist;

	}

	/**
	 * 拷贝文件
	 * @param srcFilePath
	 *            目标文件路径
	 * @param destFilePath
	 *            新文件保存路径
	 * @param rewrite
	 * @return
	 * @returnType boolean
	 */
	public static boolean copyfile(String srcFilePath, String destFilePath,Boolean rewrite) {
		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);
		return copyfile(srcFile, destFile, rewrite);
	}

	/**
	 * 拷贝文件
	 * @param fromFile
	 * @param toFile 新文件
	 * @param rewrite
	 * @return
	 * @returnType boolean
	 */
	public static boolean copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (null==fromFile || !fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
			return false;
		}
		try {
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && rewrite) {
                toFile.delete();
            }

			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param path
	 *            文件绝对路径
	 * @return 文件名
	 * @returnType String
	 */
	public static String getFileName(String path) {
		if (!TextUtils.isEmpty(path)) {
            String[] array=path.split("/");
			return array[array.length - 1];
		}
		return path;
	}

	/**
	 * 
	 * 通过Intent打开SD卡内文件
	 * 
	 * @param context
	 * @param uri
	 *            Intent getData
	 * @return String
	 * @returnType String
	 */
	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			try {
                Cursor cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static Intent getFileIntent(String allPath) {
		if (allPath.endsWith(".ppt")) {
			return getPptFileIntent(allPath);
		}
		if (allPath.endsWith(".xlsx")) {
			return getExcelFileIntent(allPath);
		}

		if (allPath.endsWith(".doc") || allPath.endsWith(".docx")) {
			return getWordFileIntent(allPath);
		}

		if (allPath.endsWith(".txt")) {
			return getTextFileIntent(allPath, false);
		}

		if (allPath.endsWith(".png") || allPath.endsWith(".jpg")
				|| allPath.endsWith(".jpeg")) {
			return getImageFileIntent(allPath);
		}
		return null;
	}

	// android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}


	public static File createNewFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}


}
