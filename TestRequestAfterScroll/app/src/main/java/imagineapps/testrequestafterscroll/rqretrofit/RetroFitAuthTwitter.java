package imagineapps.testrequestafterscroll.rqretrofit;

import android.util.Base64;
import android.util.Log;

import imagineapps.testrequestafterscroll.rqretrofit.endpoints.EndPointTokenTwitter;
import imagineapps.testrequestafterscroll.entitiies.Token;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by r028367 on 10/07/2017.
 */

public class RetroFitAuthTwitter {
    private Token token;
    public Token getToken() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        EndPointTokenTwitter endPointTokenTwitter = retrofit.create(EndPointTokenTwitter.class);
        Call<Token> call = endPointTokenTwitter.get("Basic " + generate(), "client_credentials");
        call.enqueue(new retrofit2.Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                token = response.body();
                if(token != null) {
                    Log.i("TOKEN", token.getAccessToken());
                }
                Request request = call.request();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable throwable) {
                if(throwable != null) {
                    Log.e("ON_FAILURE", throwable.getMessage());
                }
            }
        });
        return token;
    }


    private static String generate() {
        String token    = String.format("%s:%s", Constants.KEY, Constants.SECRET);
        String base64   = Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
        return base64;
    }

}
