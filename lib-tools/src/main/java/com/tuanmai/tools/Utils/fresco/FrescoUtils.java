package com.tuanmai.tools.Utils.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tuanmai.tools.Utils.LogUtil;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * @author BrodyWu
 * @version 1.0.0
 * @time 2016/11/1
 * @des fresco工具类
 * @last-update 2016/11/1
 */
public final class FrescoUtils {

    public static MemoryListener memoryListener;

    public interface MemoryListener{
        void onMemory(long max,long used);
    }


    public static void init(Context context,MemoryListener listener){
        /*int DISK_CACHE_SIZE_HIGH=100 * ByteConstants.MB;
        int DISK_CACHE_SIZE_LOW=20 * ByteConstants.MB;
        int DISK_CACHE_SIZE_VERY_LOW=20*ByteConstants.MB;

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(application.getApplicationContext())
                .setMaxCacheSize(DISK_CACHE_SIZE_HIGH)
                .setMaxCacheSizeOnLowDiskSpace(DISK_CACHE_SIZE_LOW)
                .setMaxCacheSizeOnVeryLowDiskSpace(DISK_CACHE_SIZE_VERY_LOW)
                .setBaseDirectoryPath(application.getApplicationContext().getCacheDir())
                .setBaseDirectoryName("image_cache")
                .build();

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(application)
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                int MAX_MEMORY_CACHE_SIZE=(int) Runtime.getRuntime().maxMemory() / 8;
                                return new MemoryCacheParams(
                                        MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                                        Integer.MAX_VALUE,     // Max entries in the cache
                                        MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                                        Integer.MAX_VALUE,     // Max length of eviction queue
                                        Integer.MAX_VALUE);    // Max cache entry size;
                            }
                        })
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(application, config);*/

        if(!Fresco.hasBeenInitialized()){
            ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(context, newOkHttpClient())
                    .setDownsampleEnabled(true)
                    .build();
            Fresco.initialize(context, config);
        }
        memoryListener=listener;
    }

    /**
     *  兼容HTTPS
     * @return
     */
    private static OkHttpClient newOkHttpClient() {
        final Dispatcher dispatcher = new Dispatcher(NetThreadPool.getInstance().createService());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置缓存
                // .cache(cache)
                .dispatcher(dispatcher)
                //设置超时
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                //.writeTimeout(12, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);

        //https证书
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] x509Certificates = new X509Certificate[0];
                        return x509Certificates;
                    }
                }
        };
        SSLContext sslContext = null;
        try {
            sslContext  = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {}
        final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        if (null != sslContext && null != hostnameVerifier) {
            builder.sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(hostnameVerifier);
        }
        return builder.build();
    }

    /**
     * 适配错误链接
     * @param url
     * @return
     */
    private static String getUrl(String url) {
        if (null != url) {
            if (!url.startsWith("http")) {
                if (url.startsWith("://")) {
                    url = "https" + url;
                } else if (url.startsWith("//")) {
                    url = "https:" + url;
                } else {
                    url = "https://" + url;
                }
            }
            return initUrl(url);
        } else {
            return "";
        }
    }

    /**
     * 限制下载图片宽度不超过屏幕分辨率
     * url形式为：http://img04.aomygod.com/1611bf3fa97_bc_8ffcbbc28e5d045eb95a6d36838bbaef_1242x613.jpeg
     * @param url
     * @return
     */
    private static String initUrl(String url) {
        try {
            String[] temp = url.split("_");
            if (null != temp && temp.length > 0) {
                String[] sizeString = temp[temp.length - 1].split("\\.");
                if (null != sizeString && sizeString.length > 0) {
                    String[] size = sizeString[0].split("x");
                    if (null != size && size.length > 0) {
                        int wide = 0;
                        try {
                            wide = Integer.valueOf(size[0]);
                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
                        }
                        if (ScreenUtil.getScreenWidth() > wide) {
                            return url;
                        } else {
                            //限制图片下载大小
                            return url + "?imageView2/2/w/" + ScreenUtil.getScreenWidth();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return url;
    }

    /**
     * 下载图片监听
     */
    public static void downImageListener(SimpleDraweeView draweeView, String url,
                                  ControllerListener listener) {
        if(null!=draweeView && !TextUtils.isEmpty(url) && null!=listener){
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(url))
                    .setControllerListener(listener)
                    .setOldController(draweeView.getController())
                    .build();
            draweeView.setController(controller);
        }
    }

    /**
     * 清除该图片缓存
     */
    public static void rmCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        // 从内存中删除
        imagePipeline.evictFromMemoryCache(uri);
        // 从磁盘中删除
        imagePipeline.evictFromDiskCache(uri);
        // 同时从文件和磁盘中删除当前uri的缓存
        imagePipeline.evictFromCache(uri);
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
        // 同时清除内存和磁盘缓存
        imagePipeline.clearCaches();
    }


    /**
     * 判断图片是否缓存
     */
    public static boolean isInCache(String url) {
        final boolean[] isInCache = {false};
        Fresco.getImagePipeline()
                .isInDiskCache(Uri.parse(getUrl(url)))
                .subscribe(new BaseDataSubscriber<Boolean>() {
                               @Override
                               protected void onNewResultImpl(DataSource<Boolean> dataSource) {
                                   if (dataSource.isFinished()) {
                                       isInCache[0] = dataSource.getResult();
                                   }
                               }

                               @Override
                               protected void onFailureImpl(DataSource<Boolean> dataSource) {
                                   isInCache[0] = false;
                               }
                           },
                        CallerThreadExecutor.getInstance());
        return isInCache[0];
    }

    public static synchronized void downImg(Context context,String url,final IResultBitmap linstener) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(getUrl(url)))
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                if(null!=linstener){
                    linstener.getBitMap(bitmap);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                linstener.getBitMap(null);
            }
        }, CallerThreadExecutor.getInstance());
    }


    //========================================================================================
    /**
     * 直接获取位图
     */
    public static void getBitMap(Context context,String url,final IResultBitmap linstener) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(getUrl(url)))
                .setProgressiveRenderingEnabled(true)
                .build();
        Fresco.getImagePipeline()
                .fetchDecodedImage(imageRequest, context)
                .subscribe(new BaseBitmapDataSubscriber() {
                               @Override
                               public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                   // You can use the bitmap in only limited ways
                                   // No need to do any cleanup.
                                   //!!!注意,这里获取的位图必须马上使用,因未数据源会立刻回收
                                   if ( null != linstener){
                                       linstener.getBitMap(bitmap);
                                   }
                               }

                               @Override
                               public void onFailureImpl(DataSource dataSource) {
                                   if ( null != linstener){
                                       linstener.getBitMap(null);
                                   }
                               }
                           },
                        CallerThreadExecutor.getInstance());
    }

    public interface IResultBitmap {
        void getBitMap(Bitmap bitmap);
    }

    //========================================================================================
    /**
     * 获取图片信息
     */
    public static void getImageInfo(String url, SimpleDraweeView draweeView,
                                    final FrescoInfoListener listener) {
        if (!TextUtils.isEmpty(url)) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable ImageInfo imageInfo,
                                @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            if (listener != null) {
                                listener.getImageInfo(imageInfo);
                            }
                        }

                        @Override
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {}

                        @Override
                        public void onFailure(String id, Throwable throwable) {}

                    })
                    .setUri(Uri.parse(getUrl(url)))
                    .build();
            draweeView.setController(controller);
        }
    }

    public interface FrescoInfoListener {
        void getImageInfo(ImageInfo info);
    }

    //====================================uri============================================
    public static void setDraweeByUri(SimpleDraweeView draweeView,Uri uri) {
        if(null!=draweeView){
            int width=draweeView.getWidth();
            int height=draweeView.getHeight();
            setDraweeByUri(draweeView,uri,width,height);
        }
    }

    public static void setDraweeByUri(SimpleDraweeView draweeView,Uri uri,int width,int height) {
        setDraweeImg(draweeView,uri,width,height);
    }

    //======================================path or url======================================
    /**
     * 图片大小默认屏幕宽度的五分之二
     * @param draweeView
     * @param path 网址或本地地址
     */
    public static void setDraweeImg(SimpleDraweeView draweeView, String path) {
        if(null!=draweeView && !TextUtils.isEmpty(path)){
            int width=draweeView.getWidth();
            int height=draweeView.getHeight();
            setDraweeImg(draweeView, path, width, height);
        }
    }


    /**
     * 确认图片大小
     * @param draweeView
     * @param path 网址或本地地址
     */
    public static void setDraweeImg(SimpleDraweeView draweeView, String path,int width, int height) {
        if (null!=draweeView && !TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                setDraweeImg(draweeView, Uri.fromFile(file), width, height);
            }else{
                setDraweeImg(draweeView, Uri.parse(getUrl(path)), width, height);
            }
        }
    }


    /**
     * 设置固定尺寸的图片
     */
    private static void setDraweeImg(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (null!=draweeView && null != uri && !TextUtils.isEmpty(uri.toString())) {
            //内存检测
            checkMemory();

            if(width<=0 || height<=0){
                int side = ScreenUtil.getScreenWidth()*2/ 5;
                width=side;
                height=side;
            }else{
                //width=width*4/5;
                //height=height*4/5;
            }
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .setAutoRotateEnabled(true)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(draweeView.getController())
                            .setAutoPlayAnimations(true)
                            .build();
            draweeView.setController(controller);
        }
    }


    private static void checkMemory() {
        if(null!=memoryListener){
            long max=Runtime.getRuntime().maxMemory()>>20;
            long total=Runtime.getRuntime().totalMemory()>>20;
            //long free=Runtime.getRuntime().freeMemory()>>20;
            memoryListener.onMemory(max,total);
        }
    }

    


    /**
     * 通过imageWidth 的宽度，自动适应高度
     * * @param simpleDraweeView view
     * * @param imagePath  Uri
     * * @param imageWidth width
     */
    public static void setControllerListener(final SimpleDraweeView draweeView,
                                             Uri imagePath, final int imageWidth) {
        if(null==draweeView || null==imagePath) return;
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                        if (null!=imageInfo) {
                            int height = imageInfo.getHeight();
                            int width = imageInfo.getWidth();
                            ViewGroup.LayoutParams layoutParams = draweeView.getLayoutParams();
                            layoutParams.width = imageWidth;
                            layoutParams.height = (int) ((float) (imageWidth * height) / (float) width);
                            draweeView.setLayoutParams(layoutParams);
                        }
                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {}

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .setUri(imagePath)
                .build();
        draweeView.setController(controller);
    }



    //======================================================================================
    public static void file(SimpleDraweeView draweeView, String filePath) {
        Uri uri=Uri.parse("file://" + filePath);
        setDraweeImg(draweeView,uri,0,0);
    }

    public static void content(SimpleDraweeView draweeView, String contentPatn) {
        Uri uri=Uri.parse("content://" + contentPatn);
        setDraweeImg(draweeView,uri,0,0);
    }

    public static void asset(SimpleDraweeView draweeView, String assetPath) {
        Uri uri=Uri.parse("asset://" + assetPath);
        setDraweeImg(draweeView,uri,0,0);
    }

    public static void res(SimpleDraweeView draweeView, String resPath) {
        Uri uri=Uri.parse("res://" + resPath);
        setDraweeImg(draweeView,uri,0,0);
    }

    public static void mime(SimpleDraweeView draweeView, String mimePath) {
        Uri uri=Uri.parse("data:mime/type;base64" + mimePath);
        setDraweeImg(draweeView,uri,0,0);
    }

    public static void resId(SimpleDraweeView draweeView, int id) {
        resId(draweeView,id,0,0);
    }

    public static void resId(SimpleDraweeView draweeView, int id,int width,int height) {
        if(null!=draweeView){
            String packageName = draweeView.getContext().getPackageName();
            Uri uri=Uri.parse("res://" + packageName + "/" + id);
            setDraweeImg(draweeView,uri,width,height);
        }
    }


}
