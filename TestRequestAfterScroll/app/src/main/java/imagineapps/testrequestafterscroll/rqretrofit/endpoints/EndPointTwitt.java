package imagineapps.testrequestafterscroll.rqretrofit.endpoints;

import java.util.List;


import imagineapps.testrequestafterscroll.rqretrofit.RetroFitGetTwitt;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by r028367 on 10/07/2017.
 */

public interface EndPointTwitt {
    @GET("1.1/search/tweets.json/{q}/{lang}/{count}")
    Call<List<RetroFitGetTwitt.Twitt>> getList();
}
