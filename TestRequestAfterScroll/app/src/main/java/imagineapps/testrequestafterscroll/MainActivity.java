package imagineapps.testrequestafterscroll;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.entitiy.Info;
import imagineapps.testrequestafterscroll.http.RequestAuthTwitterAPI;
import imagineapps.testrequestafterscroll.services.ServiceAuthTwitter;
import imagineapps.testrequestafterscroll.services.ServiceDownloadBitmap;
import imagineapps.testrequestafterscroll.services.ServiceSearchTwitterAPI;
import imagineapps.testrequestafterscroll.services.connection.ConnectionWithDownloadBitmap;
import imagineapps.testrequestafterscroll.services.connection.ConnectionWithServiceTwitterAuth;
import imagineapps.testrequestafterscroll.services.connection.ConnectionWithServiceTwitterSearch;
import imagineapps.testrequestafterscroll.services.connection.ConnectionWithServiceUpdateTwitterSearch;
import imagineapps.testrequestafterscroll.utils.UtilsBitmap;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    /**
     * Duas lista de Post
     * completeList: lista de post baixados
     * auxiliarList: lista auxiliar que guarda os ultimos posts baixados
     *
     * Quando o usuario faz scrollDown na listview, o aplicativo procura por
     * mais posts na API. Se houver mais, a lista de posts e atualizada, e as
     * imagens relacionadas aos novos posts sao baixadas. Para que so as imagens
     * dos novos posts sejam baixadas, temos a lista auxiliar, que mantem sempre
     * os ultimos N posts baixados
     * */
    private List<Info> completeList
            ,auxiliarList;
    private AdapterListView adapterListView;
    private Button buttonSearch;
    private EditText editTextSearch;
    private TextView quantityMessage;
    private String accessToken, textSearched;
    private int countPost;

    private boolean
             isServiceTwitterAuthBinded = false
            ,isServiceTwitterSearchBinded = false
            ,isServiceDownloadBitmapBinded = false
            ,isServiceUpdateTwitterSearchBinded = false;

    private ServiceAuthTwitter serviceAuthTwitter    = null;
    private ServiceSearchTwitterAPI serviceSearchTwitterAPI = null;
    private ServiceDownloadBitmap serviceDownloadBitmap     = null;

    private static final String BIND_SERVICE_TWITTER_AUTH           = "BIND_SERVICE_TWITTER_AUTH";
    private static final String BIND_SERVICE_TWITTER_SEARCH         = "BIND_SERVICE_TWITTER_SEARCH";
    private static final String BIND_SERVICE_DOWNLOAD_BITMAP_BINDED = "BIND_SERVICE_DOWNLOAD_BITMAP_BINDED";
    private static final String BIND_SERVICE_UPDATE_TWITTER_SEARCH  = "BIND_SERVICE_UPDATE_TWITTER_SEARCH";

    private static final String BUNDLE_STRING_SEARCH    = "BUNDLE_STRING_SEARCH";
    private static final String BUNDLE_STRING_TOKEN     = "BUNDLE_STRING_TOKEN";
    private static final String BUNDLE_QUANTITY_POST    = "BUNDLE_QUANTITY_POST";
    private static final String BUNDLE_LIST_RESULT      = "BUNDLE_LIST_RESULT";
    private static final String BUNDLE_LIST_AUXILIAR    = "BUNDLE_LIST_AUXILIAR";

    public static final int HANDLER_MSG_AUTH_TWITTER        = 0xf0;
    public static final int HANDLER_MSG_TWITTER_SEARCH      = 0xf1;
    public static final int HANDLER_MSG_DOWNLOAD_BITMAP     = 0xf2;
    public static final String BUNDLE_DATA_ARRAYLIST_API    = "BUNDLE_DATA_API";

    private static final int LIMIT_SEARCH = 7;

    private Handler handlerAuthTwitter = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == HANDLER_MSG_AUTH_TWITTER) {
                Bundle bundle   = msg.getData();
                ArrayList<RequestAuthTwitterAPI.AuthTwitter> list = bundle.getParcelableArrayList(BUNDLE_DATA_ARRAYLIST_API);
                if(list != null && list.size() > 0) {
                    RequestAuthTwitterAPI.AuthTwitter authTwitter = list.get(0);
                    if(authTwitter != null) {
                        try {
                            JSONObject jsonObject = authTwitter.getJsonObject();
                            accessToken = jsonObject.getString("access_token");
                        }
                        catch (JSONException e) {
                            Log.e("JSON_EXCP", e.getMessage());
                        }
                    }
                }
                unBindTwitterAuthService();
            }
        }
    };

    private Handler handlerTwitterSearch = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == HANDLER_MSG_TWITTER_SEARCH) {
                Bundle bundle = msg.getData();
                if(bundle != null) {
                    ArrayList<Info> data = bundle.getParcelableArrayList(BUNDLE_DATA_ARRAYLIST_API);
                    if(data != null && data.size() > 0) {
                        updateListInfo(data);
                        doBindServiceDownloadBitmap();
                    }
                }
                unBindTwitterSearchService();
            }
        }
    };

    private Handler handlerTwitterSearchUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == HANDLER_MSG_TWITTER_SEARCH) {
                Bundle bundle = msg.getData();
                if(bundle != null) {
                    ArrayList<Info> data = bundle.getParcelableArrayList(BUNDLE_DATA_ARRAYLIST_API);
                    if(data != null && data.size() > 0) {
                        updateListInfo(data);
                        adapterListView.notifyDataSetChanged();
                        doBindServiceDownloadBitmap();
                        updateInfoSizeList();
                    }
                }
                unBindServiceUpdateTwitterSearch();
            }
        }
    };

    private Handler handlerDownloadBitmap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == HANDLER_MSG_DOWNLOAD_BITMAP) {
                Bundle bundle = msg.getData();
                if(bundle != null) {
                    ArrayList<Info> data = bundle.getParcelableArrayList(BUNDLE_DATA_ARRAYLIST_API);
                    if(data != null || data.size() > 0) {
                        /**
                         * TODO
                         * apos baixa as imagens dos ultimos N posts, adicione as imagens a
                         * cada post
                         * */
                        for(Info infoRecovered : data) {
                            byte [] buffer = infoRecovered.getImageBuffer();
                            if(buffer != null) {
                                Bitmap bitmap = UtilsBitmap.uncompress(buffer);
                                infoRecovered.setImage(bitmap);
                            }
                            for(int i=0; i<completeList.size(); i++) {
                                Info oldInfo = completeList.get(i);
                                if( oldInfo.getId().equals(infoRecovered.getId()) ) {
                                    completeList.remove(i);
                                    completeList.add(i, infoRecovered);
                                    break;
                                }
                            }
                        }
                        adapterListView.notifyDataSetChanged();
                    }
                }
                unBindDownloadBitmapService();
            }
        }
    };

    private void updateListInfo(List<Info> data) {
        if(data != null || data.size() > 0) {
            auxiliarList = new ArrayList<>();
            auxiliarList.addAll(data);
            int lastIdx = completeList.size() == 0 ? 0 : completeList.size() - 1;
            completeList.addAll(lastIdx, auxiliarList);
            Collections.sort(completeList);
            adapterListView.notifyDataSetChanged();
        }
        updateInfoSizeList();
    }

    public void hiddenKeyBoard() {
        View view = getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void updateInfoSizeList() {
        countPost = completeList.size();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String message = countPost == 0 ? "Nenhuma Mensagem"
                        : String.format("%d %s", countPost, countPost > 0 ? "Mensagens" : "Mensagem");
                quantityMessage.setText(message);
            }
        });
    }

    /**
     * Ponto interessante sobre serviços vinculados
     *
     * Se criarmos um serviço vinculado, o desenolvedor nao precisa
     * se preocupar com o ciclo de vida dele. O sistema operacional
     * se encarrega de encerrar os serviços vinculados, a não ser que ele tenha
     * sido iniciado através do metodo onStartCommand, que eh executado quando
     * o serviso e iniciado atraves do metodo startService
     *
     * */
    private ServiceConnection connectionWithServiceTwitterAuth = null;
    private ServiceConnection connectionWithServiceTwitterSearch = null;
    private void initConnectionWithServiceTwitterSearch() {
        if(connectionWithServiceTwitterSearch == null) {
            connectionWithServiceTwitterSearch = new ConnectionWithServiceTwitterSearch(handlerTwitterSearch);
        }
    }

    private ServiceConnection connectionWithServiceUpdateTwitterSearch = null;
    private void initConnectionWithServiceUpdateTwitterSearch() {
        if(connectionWithServiceUpdateTwitterSearch == null) {
            connectionWithServiceUpdateTwitterSearch = new ConnectionWithServiceUpdateTwitterSearch(handlerTwitterSearchUpdate);
        }
    }

    private ServiceConnection connectionWithDownloadBitmap = null;
    public void initializeConnectionWithDownloadBitmap() {
        if(connectionWithDownloadBitmap == null) {
            connectionWithDownloadBitmap = new ConnectionWithDownloadBitmap(handlerDownloadBitmap);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            completeList = new ArrayList<>();
            doBindServiceTwitterAuth();
        }
        else {
            isServiceTwitterAuthBinded      = savedInstanceState.getBoolean(BIND_SERVICE_TWITTER_AUTH);
            isServiceTwitterSearchBinded    = savedInstanceState.getBoolean(BIND_SERVICE_TWITTER_SEARCH);
            isServiceDownloadBitmapBinded       = savedInstanceState.getBoolean(BIND_SERVICE_DOWNLOAD_BITMAP_BINDED);
            isServiceUpdateTwitterSearchBinded  = savedInstanceState.getBoolean(BIND_SERVICE_UPDATE_TWITTER_SEARCH);
            textSearched    = savedInstanceState.getString(BUNDLE_STRING_SEARCH);
            accessToken     = savedInstanceState.getString(BUNDLE_STRING_TOKEN);
            completeList    = savedInstanceState.getParcelableArrayList(BUNDLE_LIST_RESULT);
            countPost       = savedInstanceState.getInt(BUNDLE_QUANTITY_POST);
            updateInfoSizeList();
        }
        quantityMessage = (TextView) findViewById(R.id.quantity_data);
        int resource = android.R.layout.simple_list_item_1;
        adapterListView = new AdapterListView(this, resource, completeList);
        listView = (ListView) findViewById(R.id.list_data);
        listView.setAdapter(adapterListView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastVisiblePosition = 0;
            private boolean scrollUp = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView listView  = (ListView) view;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                /**
                 * Quando o usuario faz o movimento para cima para deslizar a lista
                 * isso representa um scroll down. Se o primeiro elemento que aparece na
                 * lista for maior do que o ultimo registrado, quer dizer que o usuario
                 * esta descendo na lista de elementos
                 * */
                scrollUp = firstVisiblePosition < lastVisiblePosition || (firstVisiblePosition | lastVisiblePosition) == 0;
                //
                int qPosts = completeList.size();
                //Log.i("SCROLL_STATE", String.valueOf(scrollState));
                Log.i("SCROLL", String.format("%s First: %d Last: %d Posts %d"
                    , (scrollUp ? "UP" : "DOWN")
                    , firstVisiblePosition
                    , lastVisiblePosition
                    , qPosts)
                );
                // ultimo elemento da lista dos ultimos pesquisados
                Info lastInfo       = completeList.get(0);
                Info firstInfo      = auxiliarList.get(auxiliarList.size() - 1);
                String lastInfoId   = lastInfo.getId();
                String firstInfoId  = firstInfo.getId();
                switch (scrollState) {
                    // O usuario executou o
                    case SCROLL_STATE_FLING:
                        if(!scrollUp && (qPosts - lastVisiblePosition) < 5) {
                            Log.i("UPDATE_POST", "DOWNLOAD_POSTS");
                            if(textSearched != null && !textSearched.equals("") && accessToken != null) {
                                requestNewInformation(lastInfoId, firstInfoId);
                            }
                        }
                        break;
                    // A view nao foi rolada
                    case SCROLL_STATE_IDLE:
                        break;
                    // o usuario rolou a tela e manteve o dedo na tela
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
                lastVisiblePosition = firstVisiblePosition;
                /**
                 * Se o usuario chager ao fim da lista, o listener nao sabera identificar
                 * que o mesmo pode fazer um movimento da tela
                 * */
                if((qPosts - lastVisiblePosition) < 5) {
                    Log.i("UPDATE_POST", "FIM DA LISTA. DOWNLOAD_POSTS");
                    if(textSearched != null && ! textSearched.equals("") && accessToken != null) {
                        requestNewInformation(lastInfoId, firstInfoId);
                    }
                    else {

                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem
                    , int visibleItemCount, int totalItemCount) {
            }
        });
        editTextSearch  = (EditText) findViewById(R.id.edittext_search);
        buttonSearch    = (Button) findViewById(R.id.button_search);
    }

    private void requestNewInformation(String lastInfoId, String maxId) {
        // max_id = ultimo id processado na timeline do twitter
        // since_id
        String url = Uri.parse(
            String.format(
                //"https://api.twitter.com/1.1/search/tweets.json?q=%s&lang=%s&count=%d&since_id=%s&max_id=%s"
                "https://api.twitter.com/1.1/search/tweets.json?q=%s&lang=%s&count=%d&max_id=%s"
                ,textSearched
                ,"pt"
                ,LIMIT_SEARCH
                //,lastInfoId
                ,maxId
            )
        ).toString();
        url = url.replaceAll("\\s", "%20");
        if(!isServiceUpdateTwitterSearchBinded) {
            initConnectionWithServiceUpdateTwitterSearch();
            isServiceUpdateTwitterSearchBinded = doBindServiceTwitterSearch(connectionWithServiceUpdateTwitterSearch, textSearched, url);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        doUnbindServices();
        if(outState != null) {
            outState.putBoolean(BIND_SERVICE_TWITTER_AUTH, isServiceTwitterAuthBinded);
            outState.putBoolean(BIND_SERVICE_TWITTER_SEARCH, isServiceTwitterSearchBinded);
            outState.putBoolean(BIND_SERVICE_UPDATE_TWITTER_SEARCH, isServiceUpdateTwitterSearchBinded);
            outState.putBoolean(BIND_SERVICE_DOWNLOAD_BITMAP_BINDED, isServiceDownloadBitmapBinded);
            outState.putString(BUNDLE_STRING_SEARCH, textSearched);
            outState.putString(BUNDLE_STRING_TOKEN, accessToken);
            outState.putParcelableArrayList(BUNDLE_LIST_RESULT, (ArrayList<? extends Parcelable>) completeList);
            outState.putParcelableArrayList(BUNDLE_LIST_AUXILIAR, (ArrayList<? extends Parcelable>) auxiliarList);
            outState.putInt(BUNDLE_QUANTITY_POST, countPost);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            isServiceTwitterAuthBinded          = savedInstanceState.getBoolean(BIND_SERVICE_TWITTER_AUTH);
            isServiceTwitterSearchBinded        = savedInstanceState.getBoolean(BIND_SERVICE_TWITTER_SEARCH);
            isServiceUpdateTwitterSearchBinded  = savedInstanceState.getBoolean(BIND_SERVICE_UPDATE_TWITTER_SEARCH);
            isServiceDownloadBitmapBinded       = savedInstanceState.getBoolean(BIND_SERVICE_DOWNLOAD_BITMAP_BINDED);
            textSearched = savedInstanceState.getString(BUNDLE_STRING_SEARCH);
            accessToken  = savedInstanceState.getString(BUNDLE_STRING_TOKEN);
            completeList = savedInstanceState.getParcelableArrayList(BUNDLE_LIST_RESULT);
            auxiliarList = savedInstanceState.getParcelableArrayList(BUNDLE_LIST_AUXILIAR);
            countPost    = savedInstanceState.getInt(BUNDLE_QUANTITY_POST);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void search(View view) {
        textSearched = editTextSearch.getText().toString();
        if(!textSearched.equals("") && accessToken != null /* && ! isServiceTwitterSearchBinded */) {
            String url = Uri.parse(String.format("https://api.twitter.com/1.1/" +
                    "search/tweets.json?q=%s&lang=%s&count=%d", textSearched, "pt", LIMIT_SEARCH)).toString();
            url = url.replaceAll("\\s", "%20");
            initConnectionWithServiceTwitterSearch();
            isServiceTwitterSearchBinded = doBindServiceTwitterSearch(connectionWithServiceTwitterSearch, textSearched, url);
        }
        hiddenKeyBoard();
    }

    private void doBindServiceTwitterAuth() {
        if(!isServiceTwitterAuthBinded) {
            connectionWithServiceTwitterAuth = new ConnectionWithServiceTwitterAuth(handlerAuthTwitter);
            Intent intent = new Intent(this, ServiceAuthTwitter.class);
            // startService(intent);
            // BIND_AUTO_CREATE
            isServiceTwitterAuthBinded = bindService(intent, connectionWithServiceTwitterAuth, Context.BIND_AUTO_CREATE);
            Log.i("MAIN_ACTIVITY", "BIND_SERVICE_TWITTER_AUTH");
        }
    }

    private boolean doBindServiceTwitterSearch(ServiceConnection serviceConnection, String text, String url) {
        Intent intent = new Intent(getApplicationContext(), ServiceSearchTwitterAPI.class);
        Bundle bundle = new Bundle();
        bundle.putString(ServiceSearchTwitterAPI.TEXT_SEARCH, text);
        bundle.putString(ServiceSearchTwitterAPI.TOKEN_AUTHORIZATION, accessToken);
        bundle.putString(ServiceSearchTwitterAPI.URL, url);
        intent.putExtras(bundle);
        return bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *  E/JavaBinder: !!! FAILED BINDER TRANSACTION !!!  (parcel size = 3418456)
     *  Erro relacionado ao tamanho da imagem. Programadores sugerem utilizar LruCache
     *  para fazer cache de imagens
     * */
    private void doBindServiceDownloadBitmap() {
        if(!isServiceDownloadBitmapBinded && completeList.size() > 0) {
            initializeConnectionWithDownloadBitmap();
            Intent intent = new Intent(getApplicationContext(), ServiceDownloadBitmap.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ServiceDownloadBitmap.BUNDLE_INFO_LIST, (ArrayList<? extends Parcelable>) auxiliarList);
            intent.putExtras(bundle);
            isServiceDownloadBitmapBinded = bindService(intent, connectionWithDownloadBitmap,  Context.BIND_AUTO_CREATE);
        }
    }

    private void doUnbindServices() {
        unBindTwitterAuthService();
        unBindTwitterSearchService();
        unBindServiceUpdateTwitterSearch();
        unBindDownloadBitmapService();
    }

    private void unBindTwitterAuthService() {
        if(isServiceTwitterAuthBinded && connectionWithServiceTwitterAuth != null) {
            unbindService(connectionWithServiceTwitterAuth);
            isServiceTwitterAuthBinded = false;
            Log.i("MAIN_ACTIVITY", "UNBIND_TWITTER_AUTH_SERVICE");
        }
    }

    private void unBindTwitterSearchService() {
        if(isServiceTwitterSearchBinded && connectionWithServiceTwitterSearch != null) {
            unbindService(connectionWithServiceTwitterSearch);
            isServiceTwitterSearchBinded = false;
        }
    }

    private void unBindServiceUpdateTwitterSearch() {
        if(isServiceUpdateTwitterSearchBinded && connectionWithServiceUpdateTwitterSearch != null) {
            unbindService(connectionWithServiceUpdateTwitterSearch);
            isServiceUpdateTwitterSearchBinded = false;
        }
    }

    private void unBindDownloadBitmapService() {
        if(isServiceDownloadBitmapBinded && connectionWithDownloadBitmap != null) {
            unbindService(connectionWithDownloadBitmap);
            isServiceDownloadBitmapBinded = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
