package com.rahul.seriousx.buyhatkeassignment.retrofit;

import android.content.Context;
import android.os.Build;

import com.rahul.seriousx.buyhatkeassignment.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BuildRetrofit {
    private static Retrofit mRetrofit = null;
    private Context context;

    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            mRetrofit = buildRetrofit();
        }
        return mRetrofit;
    }

    private static Retrofit buildRetrofit() {
        OkHttpClient.Builder httpClient;
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(70, TimeUnit.SECONDS)
                .readTimeout(70, TimeUnit.SECONDS)
                .writeTimeout(70, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                String flow = "app";
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("X-User-Agent", "android/" + String.valueOf(Build.VERSION.SDK_INT))
                        .header("X-Device-Id", "")
                        .header("X-API-Version", "" + BuildConfig.VERSION_CODE)
                        .header("flow", flow)
                        .method(original.method(), original.body()).build();

                return chain.proceed(request);
            }
        });
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            httpClient.addInterceptor(logging);
        }
        OkHttpClient client = httpClient.build();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
