package com.example.mapcasestudy.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SyncRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Request.Builder builder = request.newBuilder();

        //setHeaders(builder);

        request = builder.build();
        Response response = chain.proceed(request);



        return response;
        //return chain.proceed(builder.build());
    }
}
