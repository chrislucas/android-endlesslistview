package imagineapps.testrequestafterscroll.rqretrofit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.rqretrofit.endpoints.EndPointTweet;
import imagineapps.testrequestafterscroll.utils.UtilsSimpleFormatDate;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by r028367 on 10/07/2017.
 */

public class RetroFitSearchTweets {

    private Handler handler;

    public static final String HANDLER_BUNDLE_LIST_TWEET    = "HANDLER_BUNDLE_LIST_TWEET";
    public static final int HANDLER_MESSAGE_GET_TWEET       = 0x0d;
    public static final int HANDLER_MESSAGE_GET_NEW_TWEET   = 0x0e;

    public static class Data {
        @SerializedName("statuses")
        @Expose
        private List<Post> posts;
        @SerializedName("errors")
        @Expose
        private Object [] errors;
        public Data(List<Post> posts, Object [] errors) {
            this.posts = posts;
            this.errors = errors;
        }

        public List<Post> getPosts() {
            return posts;
        }

        public Object[] getErrors() {
            return errors;
        }
    }

    public static class Post {
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user")
        @Expose
        private User user;

        public Post(String createdAt, String text, String id, User user) {
            this.createdAt  = createdAt;
            this.text       = text;
            this.id         = id;
            this.user       = user;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getText() {
            return text;
        }

        public String getId() {
            return id;
        }

        public User getUser() {
            return user;
        }
    }


    public static class User {
        @SerializedName("profile_background_image_url")
        @Expose
        private String pathImageBackground;
        @SerializedName("name")
        @Expose
        private String name;
        public User(String pathImageBackground, String name) {
            this.pathImageBackground = pathImageBackground;
            this.name = name;
        }

        public String getPathImageBackground() {
            return pathImageBackground;
        }

        public String getName() {
            return name;
        }
    }

    public RetroFitSearchTweets(Handler handler) {
        this.handler = handler;
    }

    public void search(String accessToken, String query, String lang, int count) {
        OkHttpClient.Builder okHttpClient   = new OkHttpClient.Builder();
        Retrofit.Builder retrofiBuilder     = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofiBuilder.client(okHttpClient.build()).build();

        EndPointTweet endPointTweet = retrofit.create(EndPointTweet.class);

        Call<Data> callListTwitt = endPointTweet.searchTwitterAPI("Bearer " + accessToken
                , query, lang, count);

        final Message message = new Message();
        message.what = HANDLER_MESSAGE_GET_TWEET;
        callListTwitt.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Data data = response.body();
                if(data != null) {
                    List<Post> posts = data.getPosts();
                    if(posts != null) {
                        List<Info> tweets = buildTweetList(posts);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(HANDLER_BUNDLE_LIST_TWEET
                                ,(ArrayList<? extends Parcelable>) tweets);
                        message.setData(bundle);
                    }
                }
                sendMessage();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable throwable) {
                if(throwable != null) {
                    Log.e("ON_FAILURE", throwable.getMessage());
                }
                sendMessage();
            }

            private void sendMessage() {
                handler.sendMessage(message);
            }
        });
        return;
    }

    public void update(String accessToken, String query, String lang, int count, String maxId) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        Retrofit.Builder retrofiBuilder = new Retrofit
                .Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofiBuilder.client(okHttpClient.build()).build();
        EndPointTweet endPointTweet = retrofit.create(EndPointTweet.class);

        final Message message = new Message();
        message.what = HANDLER_MESSAGE_GET_NEW_TWEET;
        Call<Data> callListTwitt = endPointTweet.updateSearchTwitterAPI("Bearer "
                + accessToken, query, lang, count, maxId);
        callListTwitt.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Data data = response.body();
                if(data != null) {
                    List<Post> posts = data.getPosts();
                    if(posts != null) {
                        List<Info> tweets = buildTweetList(posts);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(HANDLER_BUNDLE_LIST_TWEET
                                ,(ArrayList<? extends Parcelable>) tweets);
                        message.setData(bundle);
                    }
                }
                sendMessage();
            }
            @Override
            public void onFailure(Call<Data> call, Throwable throwable) {
                if(throwable != null) {
                    Log.e("ON_FAILURE", throwable.getMessage());
                }
                sendMessage();
            }

            private void sendMessage() {
                handler.sendMessage(message);
            }
        });
        return;
    }

    private List<Info> buildTweetList(List<Post> posts) {
        List<Info> tweets = new ArrayList<>();
        for(Post post : posts) {
            Info info = new Info();
            info.setId(post.getId());
            info.setUrlImage(post.getUser().getPathImageBackground());
            info.setText(post.getText());
            info.setTitle(post.getUser().getName());
            info.setSubtitle("Subtitulo exemplo");
            String createAt = post.getCreatedAt();
            long time       = UtilsSimpleFormatDate.convertUTCToMilliseconds(createAt, "EEE MMM d HH:mm:ss Z yyyy");
            info.setDate(time);
            tweets.add(info);
        }
        return tweets;
    }
}
