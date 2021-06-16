package com.aomygod.library.network.interfaces;


import retrofit2.adapter.rxjava2.HttpException;

public class NetworkError extends Exception {

    public Throwable throwable;
    public HttpException httpException;

    public NetworkError(String msg) {
        super(msg);
    }

    public NetworkError(String msg, Throwable throwable) {
        this(msg);
        this.throwable = throwable;
        if (throwable instanceof HttpException) {
            httpException = (HttpException) throwable;
        }
    }
}
