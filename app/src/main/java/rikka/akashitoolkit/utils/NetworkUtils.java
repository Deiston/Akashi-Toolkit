package rikka.akashitoolkit.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Rikka on 2016/8/31.
 */
public class NetworkUtils {

    private static OkHttpClient sForceCacheClient;
    private static OkHttpClient sCacheClient;
    private static OkHttpClient sClient;

    public static void init(Context context) {
        File file = new File(context.getCacheDir(), "api_cache");
        Cache cache = new Cache(file, 10 * 1024 * 1024);

        sForceCacheClient = new OkHttpClient
                .Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request()
                                .newBuilder()
                                .cacheControl(new CacheControl.Builder().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build())
                                .build());
                    }
                })
                .build();

        sCacheClient = new OkHttpClient
                .Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request()
                                .newBuilder()
                                .cacheControl(new CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build())
                                .build());
                    }
                })
                .build();

        sClient = new OkHttpClient
                .Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request()
                                .newBuilder()
                                //.header("Cache-Control", "public, max-age=" + 60)
                                .build());
                    }
                })
                .build();
    }

    /**
     * 返回一个使用缓存的 OkHttpClient
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getForceCacheClient() {
        return sForceCacheClient;
    }

    /**
     * 返回一个使用最大一天缓存的 OkHttpClient
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getCacheClient() {
        return sCacheClient;
    }

    /**
     * 返回一个使用缓存的 OkHttpClient 或是普通的 OkHttpClient
     *
     * @param force_cache 是否使用缓存
     * @return OkHttpClient
     */
    public static OkHttpClient getClient(boolean force_cache) {
        return force_cache ? sForceCacheClient : sClient;
    }

    /**
     * 返回一个普通的 OkHttpClient
     * @return OkHttpClient
     */
    public static OkHttpClient getClient() {
        return sClient;
    }
}
