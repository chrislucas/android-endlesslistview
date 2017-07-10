package imagineapps.testrequestafterscroll.rqretrofit.endpoints;

import imagineapps.testrequestafterscroll.entitiies.Token;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by r028367 on 10/07/2017.
 */

public interface EndPointTokenTwitter {
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> get(@Header("Authorization") String auth, @Field("grant_type") String credentials);
}
