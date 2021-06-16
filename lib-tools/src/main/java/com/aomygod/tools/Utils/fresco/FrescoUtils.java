package com.aomygod.tools.Utils.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.aomygod.library.network.NetThreadPool;
import com.aomygod.library.network.NetworkClient;
import com.aomygod.tools.Utils.FileUtil;
import com.aomygod.tools.Utils.MD5;
import com.aomygod.tools.Utils.ScreenUtil;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
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
    public static String mCachPath="/sdcard/ImgCache";

    public interface MemoryListener {
        void onMemory(long max, long used);
    }


    public static void init(Context context, String cachePath,MemoryListener listener) {
        if(!TextUtils.isEmpty(cachePath) && cachePath.startsWith("/")){
            mCachPath=cachePath;
        }
        if (!Fresco.hasBeenInitialized()) {
           /* ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(context, newOkHttpClient())
                    .setDownsampleEnabled(true)
                    .build();
            Fresco.initialize(context, config);*/

           //硬盘缓存
            final int DISK_CACHE_SIZE_HIGH = 2048 * ByteConstants.MB;
            final int DISK_CACHE_SIZE_LOW = 1024 * ByteConstants.MB;
            final int DISK_CACHE_SIZE_VERY_LOW = 512 * ByteConstants.MB;
            DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context.getApplicationContext())
                    .setMaxCacheSize(DISK_CACHE_SIZE_HIGH)
                    .setMaxCacheSizeOnLowDiskSpace(DISK_CACHE_SIZE_LOW)
                    .setMaxCacheSizeOnVeryLowDiskSpace(DISK_CACHE_SIZE_VERY_LOW)
                    .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                    .setBaseDirectoryName("image_cache")
                    .build();

            Supplier<MemoryCacheParams> supplier= new Supplier<MemoryCacheParams>() {
                public MemoryCacheParams get() {
                    int MAX_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() >> 20 / 5);
                    return new MemoryCacheParams(
                            MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                            Integer.MAX_VALUE,     // Max entries in the cache
                            MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                            Integer.MAX_VALUE,     // Max length of eviction queue
                            Integer.MAX_VALUE);    // Max cache entry size;
                }
            };

            ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                    .setBitmapMemoryCacheParamsSupplier(supplier)
                    .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                    .setMainDiskCacheConfig(diskCacheConfig)
                    .setDownsampleEnabled(true)
                    .setResizeAndRotateEnabledForNetwork(true)
                    .setBitmapsConfig(Bitmap.Config.RGB_565)
                    .build();
            Fresco.initialize(context, config);
        }
        memoryListener = listener;
    }

    /**
     * 兼容HTTPS
     */
    private static OkHttpClient newOkHttpClient() {
        final Dispatcher dispatcher = new Dispatcher(NetThreadPool.getInstance().createService());
        OkHttpClient.Builder builder = NetworkClient.creatAllTrustClient()
                //设置缓存
                // .cache(cache)
                .dispatcher(dispatcher)
                //设置超时
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                //.writeTimeout(12, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 适配错误链接
     */
    public static String getUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http")) {
                if (url.startsWith("://")) {
                    url = "https" + url;
                } else if (url.startsWith("//")) {
                    url = "https:" + url;
                } else {
                    url = "https://" + url;
                }
            }
            return url;
        } else {
            return "";
        }
    }

    /**
     * 下载图片监听
     */
    public static void downImageListener(SimpleDraweeView draweeView, String url,
                                         ControllerListener listener) {
        if (null != draweeView && !TextUtils.isEmpty(url) && null != listener) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(url))
                    .setControllerListener(listener)
                    .setOldController(draweeView.getController())
                    .build();
            draweeView.setController(controller);
        }
    }

    public static void downImg(Context context, String url, final IResultBitmap linstener) {
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
                if (null != linstener) linstener.getBitMap(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (null != linstener) linstener.getBitMap(null);
            }

        }, CallerThreadExecutor.getInstance());
    }


    //========================================================================================
    /**
     * 直接获取位图
     */
    public static void getBitMap(Context context, String url, final IResultBitmap linstener) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(getUrl(url)))
                .setProgressiveRenderingEnabled(true)
                .build();
        Fresco.getImagePipeline()
                .fetchDecodedImage(imageRequest, context)
                .subscribe(new BaseBitmapDataSubscriber() {
                               @Override
                               public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                   //!!!注意,这里获取的位图必须马上使用,因为数据源会立刻回收
                                   if (null != linstener) {
                                       linstener.getBitMap(bitmap);
                                   }
                               }

                               @Override
                               public void onFailureImpl(DataSource dataSource) {
                                   if (null != linstener) {
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
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                        }

                    })
                    .setUri(Uri.parse(getUrl(url)))
                    .build();
            draweeView.setController(controller);
        }
    }

    public interface FrescoInfoListener {
        void getImageInfo(ImageInfo info);
    }

    //======================================path or url======================================
    /**
     * 图片大小默认屏幕宽度的五分之二
     *
     * @param draweeView
     * @param path       网址或本地地址
     */
    public static void setDraweeImg(SimpleDraweeView draweeView, String path) {
        if (null != draweeView && !TextUtils.isEmpty(path)) {
            int width = draweeView.getWidth();
            int height = draweeView.getHeight();
            if(width<=0) width=400;
            if(height<=0) height=400;
            setDraweeImg(draweeView, path, width, height);
        }
    }


    private static SimpleArrayMap<String,Boolean> downMap=new SimpleArrayMap<>();
    /**
     * 确认图片大小
     *
     * @param draweeView
     * @param path       网址或本地地址
     */
    public static void setDraweeImg(SimpleDraweeView draweeView, final String path, int width, int height) {
        if (null != draweeView && !TextUtils.isEmpty(path) && width > 0 && height > 0) {
            final File file = new File(path);
            if (file.exists()) {
                setDraweeImg(draweeView, Uri.fromFile(file), width, height);
            } else {
                //setDraweeImg(draweeView, Uri.parse(getUrl(path)), width, height);
                final String md5=MD5.Md5(path);
                final String imgName=md5+".webp";
                final File imgFile=new File(getImgPath(),imgName);
                if(imgFile.exists()){
                    setDraweeImg(draweeView, Uri.fromFile(imgFile), width, height);
                }else{
                    setDraweeImg(draweeView, Uri.parse(getUrl(path)), width, height);
                    if(!downMap.containsKey(md5)){
                        downMap.put(md5,true);
                        //下载一份
                        downImg(draweeView.getContext(), path, new IResultBitmap() {
                            @Override
                            public void getBitMap(Bitmap bitmap) {
                                String name=md5+".tmp";
                                File tmpFile=new File(getImgPath(),name);
                                if(null!=tmpFile){
                                    FileUtil.saveWebpFile(bitmap,getImgPath().getAbsolutePath(), name);
                                    tmpFile.renameTo(imgFile);
                                    downMap.remove(md5);
                                }
                            }
                        });
                    }
                }
            }
        }
    }


    /**
     * 获取缓存图片路径
     */
    public static File getImgPath(){
        File dirFile=new File(mCachPath,"ImgCache");
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        return dirFile;
    }


    /**
     * 设置固定尺寸的图片
     */
    private static void setDraweeImg(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (null != draweeView && null != uri && !TextUtils.isEmpty(uri.toString())) {
            //内存检测
            checkMemory();

            if (width <= 0 || height <= 0) {
                int side = ScreenUtil.getScreenWidth() * 2 / 5;
                width = side;
                height = side;
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


    /**
     * 内存监听
     */
    private static void checkMemory() {
        if (null != memoryListener) {
            //long free=Runtime.getRuntime().freeMemory()>>20;
            memoryListener.onMemory(
                    Runtime.getRuntime().maxMemory() >> 20,
                    Runtime.getRuntime().totalMemory() >> 20);
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
        if (null == draweeView || null == imagePath) return;
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                        if (null != imageInfo) {
                            int height = imageInfo.getHeight();
                            int width = imageInfo.getWidth();
                            ViewGroup.LayoutParams layoutParams = draweeView.getLayoutParams();
                            layoutParams.width = imageWidth;
                            layoutParams.height = (int) ((float) (imageWidth * height) / (float) width);
                            draweeView.setLayoutParams(layoutParams);
                        }
                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .setUri(imagePath)
                .build();
        draweeView.setController(controller);
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

    /**
     * 清除该图片缓存
     */
    public static void rmCache(Uri uri) {
        try {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            // 从内存中删除
            imagePipeline.evictFromMemoryCache(uri);
            // 从磁盘中删除
            imagePipeline.evictFromDiskCache(uri);
            // 同时从文件和磁盘中删除当前uri的缓存
            imagePipeline.evictFromCache(uri);
        }catch (Exception e){}
    }

    /**
     * 清理缓存
     */
    public static void cleanCache(){
        try{
            Fresco.getImagePipeline().clearMemoryCaches();
        }catch (Exception e){}
    }

    //======================================================================================
    public static void file(SimpleDraweeView draweeView, String filePath) {
        Uri uri = Uri.parse("file://" + filePath);
        setDraweeImg(draweeView, uri, 0, 0);
    }

    public static void content(SimpleDraweeView draweeView, String contentPatn) {
        Uri uri = Uri.parse("content://" + contentPatn);
        setDraweeImg(draweeView, uri, 0, 0);
    }

    public static void asset(SimpleDraweeView draweeView, String assetPath) {
        Uri uri = Uri.parse("asset://" + assetPath);
        setDraweeImg(draweeView, uri, 0, 0);
    }

    public static void res(SimpleDraweeView draweeView, String resPath) {
        Uri uri = Uri.parse("res://" + resPath);
        setDraweeImg(draweeView, uri, 0, 0);
    }

    public static void mime(SimpleDraweeView draweeView, String mimePath) {
        Uri uri = Uri.parse("data:mime/type;base64" + mimePath);
        setDraweeImg(draweeView, uri, 0, 0);
    }

    public static void resId(SimpleDraweeView draweeView, int id) {
        resId(draweeView, id, 0, 0);
    }

    public static void resId(SimpleDraweeView draweeView, int id, int width, int height) {
        if (null != draweeView) {
            String packageName = draweeView.getContext().getPackageName();
            Uri uri = Uri.parse("res://" + packageName + "/" + id);
            setDraweeImg(draweeView, uri, width, height);
        }
    }


}
