package com.aomygod.library.network;

import android.os.Build;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * OKHttp网络请求客户端
 */
public final class NetworkClient {

	private volatile static NetworkClient instance;
    private OkHttpClient client;

	public static NetworkClient getInstance() {
		if (instance == null) {
			synchronized (NetworkClient.class) {
				if (instance == null) {
					instance = new NetworkClient();
				}
			}
		}
		return instance;
	}

	private NetworkClient() {
		if (null == client) {
			client = newOkHttpClient();
		}
	}

	//=====================================================================================
    private static List<Interceptor> interceptors = new ArrayList<>(3);// 拦截器

    /**
     * 增加拦截器
     */
    public static void addInterceptor(Interceptor interceptor) {
        if (null != interceptor
                && !interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
            RetrofitManager.resetService();
        }
    }

	//=====================================================================================
	public OkHttpClient getClient() {
		if (null == client) {
			client = newOkHttpClient();
		}
		return client;
	}

	public void resetClient(){
        client=null;
    }

    private OkHttpClient newOkHttpClient() {
        final Dispatcher dispatcher = new Dispatcher(NetThreadPool.getInstance().createService());
        OkHttpClient.Builder builder = creatAllTrustClient()
                //设置缓存
                // .cache(cache)
                .dispatcher(dispatcher)
                //设置超时
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);;
        //添加各种拦截器
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        /*if (DEBUG) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }*/
        return builder.build();
    }


    /**
     * 创建信任所有证书的OkHttpClient.Builder
     */
    public static OkHttpClient.Builder creatAllTrustClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //不准使用代理,可防止抓包
        //builder.proxy(Proxy.NO_PROXY);
        if(Build.VERSION.SDK_INT < 29 && Build.VERSION.SDK_INT>21){
            builder.sslSocketFactory(createSSLSocketFactory());
        }
        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        return builder;
    }


    //===================================证书相关============================================
    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     */
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
