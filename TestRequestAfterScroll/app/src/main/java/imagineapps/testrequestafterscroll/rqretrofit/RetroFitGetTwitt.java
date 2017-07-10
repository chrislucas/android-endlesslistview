package imagineapps.testrequestafterscroll.rqretrofit;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.rqretrofit.endpoints.EndPointTwitt;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by r028367 on 10/07/2017.
 */

public class RetroFitGetTwitt {

    private List<Twitt> twitts;

    public static class Twitt {

    }

    public List<Twitt> getTwitt() {
        twitts = new ArrayList<>();
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        Retrofit.Builder retrofiBuilder = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofiBuilder.client(okHttpClient.build()).build();
        EndPointTwitt endPointTwitt = retrofit.create(EndPointTwitt.class);
        Call<List<Twitt>> callListTwitt = endPointTwitt.getList();
        callListTwitt.enqueue(new Callback<List<Twitt>>() {
            @Override
            public void onResponse(Call<List<Twitt>> call, Response<List<Twitt>> response) {
                List<Twitt> twitts = response.body();

            }

            @Override
            public void onFailure(Call<List<Twitt>> call, Throwable throwable) {
                if(throwable != null) {
                    Log.e("ON_FAILURE", throwable.getMessage());
                }
            }
        });

        return twitts;
    }
}
