package com.tuanmai.tools.Utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;

import com.tuanmai.tools.Utils.LogUtil;
import com.tuanmai.tools.Utils.ScreenUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {
	/**
	 * InputStream转换为byte[]数组
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] openStream(InputStream is) throws IOException {
		if (null != is){
            try {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024]; // 用数据装
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    outstream.write(buffer, 0, len);
                }
                outstream.close();
                is.close();
                return outstream.toByteArray();

            } catch (Exception exception) {
                LogUtil.e(exception);
            }
        }
		return null;

	}

	public static final String sdPath = Environment
			.getExternalStorageDirectory() + "/";

	/**
	 * 将图片处理为圆形图
	 * 
	 * @param bitmap
	 * @return Bitmap //返回圆形位图
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getHeight(),
				bitmap.getWidth(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true); // 抗锯齿
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int diameter = 0; // 圆形角的直径
		Rect rect = null;
		if (width > height) {
			diameter = height;
			rect = new Rect((width - height) / 2, 0, width - (width - height)
					/ 2, height);
		} else {
			diameter = width;
			rect = new Rect(0, (height - width) / 2, width, height
					- (height - width) / 2);
		}
		RectF rectF = new RectF(rect); // 正方形的画图区域
		canvas.drawRoundRect(rectF, diameter / 2, diameter / 2, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rectF, paint);
		return output;
	}

	/**
	 * 外部文件中获取位图
	 * 
	 * @param iamgePath
	 * @return bitmap
	 */
	public static Bitmap getBitmapFromSd(String iamgePath) {

		return BitmapFactory.decodeFile(iamgePath);

	}

	/**
	 * 将bitmap保存到SD卡中
	 * 
	 * @param bitmap
	 * @param filename
	 * @return bool
	 */

	public static boolean saveBitmap2Sd(Bitmap bitmap, String filepath,String filename) {

		FileOutputStream outputStream = null;
		try {
			File fileDir = new File(filepath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			File file = new File(filepath, filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
		} catch (Exception e) {
			LogUtil.e(e);
			return false;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
					outputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return true;
	}

	/**
	 * 
	 * 重命名文件
	 */
	public static boolean renamefile(String srcFilePath, String destFilePath) {
		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);
		return copyfile(srcFile, destFile, true);
	}

	/**
	 * 
	 * 拷贝文件
	 */
	public static boolean copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return false;
		}
		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		// 当文件不存时，canWrite一直返回的都是false
		// if (!toFile.canWrite()) {
		// MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" +
		// toFile.getPath());
		// Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
		// return ;
		// }
		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(
					fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception ex) {
			LogUtil.e(ex);
		}
		return false;
	}

	public static Bitmap bytes2Bitmap(byte[] bytes) {

		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

	}

	public static byte[] BitmapBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static Bitmap getImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = Float.valueOf(ScreenUtil.getScreenHeight());
		float ww = Float.valueOf(ScreenUtil.getScreenHeight());
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
        newOpts.inPurgeable = true;// 同时设置才会有效  
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap,200);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image,int imageSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		int bpSize = baos.toByteArray().length / 1024;
		// 判断图片大小
		int tmpV = imageSize;
		if (bpSize > tmpV) {
			tmpV = imageSize;
		} else if (bpSize > tmpV/2) {
			tmpV = tmpV/2;
		} else {
			tmpV = tmpV/4;
		}
		while (baos.toByteArray().length / 1024 > tmpV) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			if (options == 10) {
				break;
			}
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
			if (options == 0)
				break;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	
	
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getCircleBitmap(Bitmap bitmap, int width, int height) {
		Bitmap croppedBitmap = scaleCenterCrop(bitmap, width, height);
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, width, height);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		int radius = (width > height) ? height : width;
		radius /= 2;

		canvas.drawCircle(width / 2, height / 2, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(croppedBitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight,
			int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		RectF targetRect = new RectF(left, top, left + scaledWidth, top
				+ scaledHeight);

		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
				source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}

	/**
	 * 
	 * @date 2015-5-18 下午3:55:53
	 * @author 刘洲
	 * @Description: 下载图片并缓存到SD卡
	 * @param strUrl
	 *            图片URL地址
	 * @param storagePath
	 *            文件存储路径
	 * @return 是否下载完成
	 * @throws IOException
	 */
	public static boolean downloadImage(String strUrl, String storagePath)
			throws IOException {
		URL url = new URL(strUrl);
		File outFile = new File(storagePath);
		if (outFile.exists()) {
			return true;
		}
		outFile.getParentFile().mkdirs();
		boolean isOK = false;
		FileOutputStream out = new FileOutputStream(outFile);
		try {
			byte[] buffer = new byte[4096 * 4];

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			try {
				InputStream in;
				try {
					in = conn.getInputStream();
				} catch (IOException e) {
					outFile.delete();
					e.printStackTrace();
					return false;
				}
				try {
					for (;;) {
						int len = in.read(buffer);
						if (len > 0) {
							out.write(buffer, 0, len);
						} else {
							in.close();
							break;
						}
					}
					isOK = true;
				} finally {
					in.close();
				}
			} finally {
				conn.disconnect();
			}
		} finally {
			out.close();
		}
		if (!isOK) {
			outFile.delete();
			return false;
		}

		return true;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		return BitmapFactory.decodeResource(context.getResources(), resId, opt);
	}
}
