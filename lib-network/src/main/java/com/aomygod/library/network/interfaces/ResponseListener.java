package com.aomygod.library.network.interfaces;

public interface ResponseListener<T> {

    void onResponse(T response);
    void onErrorResponse(NetworkError error);

}
