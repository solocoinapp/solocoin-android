package app.solocoin.solocoin.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import app.solocoin.solocoin.R;
import app.solocoin.solocoin.app.MyApplication;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import okhttp3.logging.HttpLoggingInterceptor;

//  APIService service = APIClient.getRetrofitInstance(this).create(APIService.class);
//  JsonObject object = new JsonObject();
//  object.addProperty("mobile", "1234567890");
//  Call<JsonObject> call = service.doMobileLogin(object);
//  call.enqueue(new Callback<JsonObject>() {
//      @Override
//      public void onResponse(@NotNull Call<JsonObject> call,@NonNull Response<JsonObject> response) {
//          //on-success-api-call
//      }
//
//      @Override
//      public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
//          //on-failure-api-call
//      }
//  });
public class APIClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(context.getString(R.string.BASE_URL))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Response response;

                    try {
                        Request request = chain.request();
                        response = chain.proceed(request);
                    } catch (IOException e) {
                        backgroundThreadShortToast(MyApplication.appContext, "Something went wrong. Please try again.");
                        throw e;
                    }

                    return response;
                })
                .build();
    }

    private static void backgroundThreadShortToast(final Context context, final String msg)
    {
        if(context != null && msg != null)
        {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
        }
    }
}
