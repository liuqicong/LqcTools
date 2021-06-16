package com.aomygod.library.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aomygod.library.network.interfaces.RequestType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trello.rxlifecycle2.LifecycleTransformer;

import androidx.core.util.Pools;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络访问控制中心
 */
public final class RetrofitManager {

    private static final Pools.SynchronizedPool<RetrofitManager> sPool = new Pools.SynchronizedPool<RetrofitManager>(6);

    private volatile static String baseUrl = "";
    private volatile static RetrofitService retrofitService;

    private int requestType;
    private String url;
    private NetMap headers;
    private NetMap requestParams;
    private String cacheKey;
    private NetworkSubscriber subscriber;
    private Gson gson;

    public static RetrofitManager obtain(Context context) {
        RetrofitManager instance = sPool.acquire();
        if(null==instance){
            instance=new RetrofitManager();
        }
        instance.reset(context);
        return instance;
    }

    private void reset(Context context) {
        this.context = context;
        cacheSeconds=0;
    }

    public void recycle() {
        sPool.release(this);
    }


    //======================================================================
    private LifecycleTransformer transformer;// 网络访问生命周期
    private int cacheSeconds;// 缓存时间（单位：秒）：在cacheSeconds时间内会返回缓存，并不进行执行网络访问
    private Context context;

    public RetrofitManager setTransformer(LifecycleTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public RetrofitManager setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds>0?cacheSeconds:0;
        return this;
    }

    public RetrofitManager setRequestType(int requestType) {
        this.requestType = requestType;
        return this;
    }

    public RetrofitManager setNewUrl(String url) {
        this.url = url;
        String bUrl = new StringBuilder(url.split(".cn/")[0]).append(".cn/").toString();
        if (!baseUrl.equals(bUrl)) {
            baseUrl = bUrl;
            retrofitService = createService(RetrofitService.class, baseUrl);
        }
        return this;
    }

    public RetrofitManager setRequestParams(NetMap requestParams) {
        this.requestParams = requestParams;
        cacheKey=ACache.Utils.string2MD5(requestParams.toString());
        this.requestParams.remove("method");
        this.requestParams.remove("cacheParams");
        return this;
    }

    public RetrofitManager setHeaders(NetMap headers) {
        this.headers=headers;
        return this;
    }

    public RetrofitManager setSubscriber(NetworkSubscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    /**
     * 重置retrofit服务
     */
    public static void resetService() {
        if (null != retrofitService && !TextUtils.isEmpty(baseUrl)) {
            NetworkClient.getInstance().resetClient();
            retrofitService = createService(RetrofitService.class, baseUrl);
        }
    }

    //======================================================================================
    private static <T> T createService(final Class<T> service, String url) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(GSONUtil.GSON))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(NetworkClient.getInstance().getClient())
                    .build();
            return retrofit.create(service);
        } catch (Exception e) {
            //Log.e("lqc","===createService======>>>"+e.toString());
            return null;
        }
    }

    private void recycleMap(){
        if(null!=requestParams) requestParams.recycle();
        if(null!=headers) headers.recycle();
        recycle();
    }

    // ------------------------------------------------------------------------------------
    /**
     * 执行网络访问
     */
    public void doExecute() {
        if (null != context && cacheSeconds > 0) {
            //读缓存
            String content = ACache.get(context).getAsString(cacheKey);
            if (!TextUtils.isEmpty(content)) {
                try {
                    Object object = GSONUtil.GSON.fromJson(content, subscriber.getCls());
                    subscriber.onNext(object);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                recycleMap();
                return;
            }
        }

        if (null == retrofitService || TextUtils.isEmpty(url)) {
            subscriber.onError(new Throwable("url is null"));
            recycleMap();
            return;
        }

        Observable<Object> observable;
        switch (requestType){
            case RequestType.GET:
                observable = retrofitService.doGetNewUrl(url, requestParams, headers);
                break;
            case RequestType.POST:
                if(null==gson) gson = new GsonBuilder().setPrettyPrinting().create();
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        gson.toJson(requestParams));
                observable = retrofitService.doPostNewUrl(url, body, headers);
                break;
            /*case RequestType.BODY:
                observable = retrofitService.doPostNewUrl(url, queryParams, body, headers);
                break;*/
            default:
                subscriber.onError(new NullPointerException("Error: request has not such method!"));
                recycleMap();
                return;
        }

        try {
            observable.compose(new ObservableTransformer<Object, Object>() {
                @Override
                public ObservableSource<Object> apply(Observable<Object> upstream) {
                    if (null != transformer) {
                        upstream= upstream.compose(transformer);
                    }
                    return upstream.subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            }).compose(new ObservableTransformer<Object, Object>() {

                @Override
                public ObservableSource<Object> apply(Observable<Object> upstream) {
                    return upstream.map(new Function<Object, Object>() {
                        @Override
                        public Object apply(Object o) {
                            return o;
                        }
                    })
                            .onErrorResumeNext(new Function<Throwable, ObservableSource<?>>() {
                                @Override
                                public ObservableSource<?> apply(Throwable throwable) {
                                    return Observable.error(throwable);
                                }
                            });
                }
            }).subscribe(new Observer<Object>() {
                @Override
                public void onSubscribe(Disposable d) {}

                @Override
                public void onNext(Object value) {
                    onNextResult(value);
                }

                @Override
                public void onError(Throwable e) {
                    /*if(BuildConfig.DEBUG){
                        Log.e("net_data","=onError====>>>"+e.toString());
                    }*/
                    if (null != subscriber) {
                        subscriber.onError(e);
                    }
                }

                @Override
                public void onComplete() {
                    if (null != subscriber) {
                        subscriber.onComplete();
                    }
                    recycleMap();
                }
            });
        } catch (Exception e) {
            if (null != subscriber) {
                subscriber.onError(e);
            }
        }
    }


    // -----------------------------------------------------------------
    private void onNextResult(Object value) {
        if (null != subscriber) {
            if (null == value) {
                subscriber.onError(new Throwable("result is null!!!"));
            } else {
                try {
                    String dataStr = GSONUtil.GSON.toJson(value);
                    /*if(BuildConfig.DEBUG){
                        Log.e("net_data","---->"+dataStr);
                    }*/
                    Object object = GSONUtil.GSON.fromJson(dataStr, subscriber.getCls());
                    subscriber.onNext(object);
                    //处理缓存
                    if (null != context
                            && cacheSeconds > 0
                            && (dataStr.contains("\"success\":true") || dataStr.contains("\"success\":TRUE"))) {
                        ACache.get(context).put(cacheKey, dataStr, cacheSeconds);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }
    }
}