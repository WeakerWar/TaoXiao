package com.example.taoxiao.Http;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
