package imagineapps.testrequestafterscroll.rqretrofit.endpoints;


import imagineapps.testrequestafterscroll.rqretrofit.RetroFitSearchTweets;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by r028367 on 10/07/2017.
 */

public interface EndPointTweet {

    @GET("1.1/search/tweets.json")
    Call<RetroFitSearchTweets.Data> searchTwitterAPI(@Header("Authorization") String auth
            , @Query("q") String query, @Query("lang") String lang, @Query("count") int count);

    @GET("1.1/search/tweets.json")
    Call<RetroFitSearchTweets.Data> updateSearchTwitterAPI(@Header("Authorization") String auth
            , @Query("q") String query, @Query("lang") String lang, @Query("count") int count, @Query("max_id") String maxId);
}
