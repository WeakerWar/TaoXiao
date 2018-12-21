package com.example.taoxiao.Http;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static String url = "134.175.149.237";
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
