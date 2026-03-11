package app.solocoin.solocoin.api;

import android.content.Context;
import app.solocoin.solocoin.R;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                 //   .client(client)
                    .build();
        }
        return retrofit;
    }
}
