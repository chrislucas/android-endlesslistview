package imagineapps.testrequestafterscroll;

import android.content.Context;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.rqretrofit.RetroFitAuthTwitter;
import imagineapps.testrequestafterscroll.rqretrofit.RetroFitSearchTweets;
import imagineapps.testrequestafterscroll.utils.BuildDatabase;
import imagineapps.testrequestafterscroll.utils.BuildProgressDialog;

/**
 * Implementar a mesma ideia da MainActivity porem usando RetroFit para aprender
 * a usar a biblioteca
 *
 * */

public class Main2Activity extends AppCompatActivity {

    // VIEWS
    private AdapterListView adapterListView;
    private ListView listView;
    private Button buttonSearch;
    private EditText editTextSearch;
    private TextView quantityMessage;

    // TYPES
    private String accessToken, textSearched;
    private List<Info> completeList
    /**
     * A busca por novos posts eh feita de N em N itens.
     * A lista auxiliar serve para armazenar os ultimos N elementos
     * que foram pesquisados na API do TWITTER e colocalos na lista completa dos twitts baixados
     */
        ,auxiliarList;
    private int countPost;
    private static final int LIMIT_SEARCH = 7;
    private BuildProgressDialog pDialogSearch;

    public static final String BUNLDE_COMPLETE_INFO_LIST = "BUNLDE_COMPLETE_INFO_LIST";
    public static final String BUNLDE_AUXILIAR_INFO_LIST = "BUNLDE_AUXILIAR_INFO_LIST";
    public static final String BUNLDE_SIZE_INFO_LIST     = "BUNLDE_SIZE_INFO_LIST";
    public static final String BUNDLE_ACCESS_TOKEN       = "BUNDLE_ACCESS_TOKEN";
    public static final String BUNDLE_TEXT_SEARCHED      = "BUNDLE_TEXT_SEARCHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            completeList = new ArrayList<>();
        }

        else {
            completeList = savedInstanceState.getParcelableArrayList(BUNLDE_COMPLETE_INFO_LIST);
            auxiliarList = savedInstanceState.getParcelableArrayList(BUNLDE_AUXILIAR_INFO_LIST);
            countPost = savedInstanceState.getInt(BUNLDE_SIZE_INFO_LIST);
            accessToken = savedInstanceState.getString(BUNDLE_ACCESS_TOKEN);
            textSearched = savedInstanceState.getString(BUNDLE_TEXT_SEARCHED);
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
                int qPosts = completeList.size();
                Log.i("SCROLL", String.format("%s First: %d Last: %d Posts %d"
                        , (scrollUp ? "UP" : "DOWN")
                        , firstVisiblePosition
                        , lastVisiblePosition
                        , qPosts)
                );
                // a lista vem em ordem decrescente por data
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
                                research(firstInfoId);
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
                        research(firstInfoId);
                    }
                    else {}
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem
                    , int visibleItemCount, int totalItemCount) {
            }
        });
        editTextSearch  = (EditText) findViewById(R.id.edittext_search);
        buttonSearch    = (Button) findViewById(R.id.button_search);
        pDialogSearch   = new BuildProgressDialog(this);
        if(accessToken == null)
            getAccessToken();
        updateInfoSizeList();
        configDatabase();
    }

    private void configDatabase() {
        if(!BuildDatabase.checkDbExists(this)) {
            boolean create = BuildDatabase.createDbIfNotExists(this);
            if(create) {
                /**
                 * Se a Base foi criada, criar a tabela de Posts
                 * */
            }
        }
    }

    private Handler handler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle   = msg.getData();
            List<Info> data = null;
            switch (msg.what) {
                case RetroFitAuthTwitter.HANDLER_MESSAGE:
                    String token  = bundle.getString(RetroFitAuthTwitter.HANDLER_MESSAGE_TOKEN);
                    if(token != null)
                        accessToken = token;
                    break;
                case RetroFitSearchTweets.HANDLER_MESSAGE_GET_TWEET:
                    data = bundle.getParcelableArrayList(RetroFitSearchTweets.HANDLER_BUNDLE_LIST_TWEET);
                    updateListInfo(data);
                    break;
                case RetroFitSearchTweets.HANDLER_MESSAGE_GET_NEW_TWEET:
                    data = bundle.getParcelableArrayList(RetroFitSearchTweets.HANDLER_BUNDLE_LIST_TWEET);
                    updateListInfo(data);
                    break;
            }
            dismissProgressDialog();
        }
    };


    private void getAccessToken() {
        /**
         * TODO
         * verificar se ha conexao com a internet
         * */
        RetroFitAuthTwitter retroFitAuthTwitter = new RetroFitAuthTwitter(handler);
        retroFitAuthTwitter.getToken();
    }


    private void showProgressDialog() {
        try {
            pDialogSearch.buildDefault(true, false
                    , "Pesquisando.", "Aguarde enquando a pesquisa está sendo realizada.").safeShowing();
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage());
        }
    }

    private void dismissProgressDialog() {
        try {
            pDialogSearch.safeDismiss();
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            outState.putParcelableArrayList(BUNLDE_COMPLETE_INFO_LIST, (ArrayList<? extends Parcelable>) completeList);
            outState.putParcelableArrayList(BUNLDE_AUXILIAR_INFO_LIST, (ArrayList<? extends Parcelable>) auxiliarList);
            outState.putInt(BUNLDE_SIZE_INFO_LIST, countPost);
            outState.putString(BUNDLE_ACCESS_TOKEN, accessToken);
            outState.putString(BUNDLE_TEXT_SEARCHED, textSearched);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            completeList    = savedInstanceState.getParcelableArrayList(BUNLDE_COMPLETE_INFO_LIST);
            auxiliarList    = savedInstanceState.getParcelableArrayList(BUNLDE_AUXILIAR_INFO_LIST);
            countPost       = savedInstanceState.getInt(BUNLDE_SIZE_INFO_LIST);
            accessToken     = savedInstanceState.getString(BUNDLE_ACCESS_TOKEN);
            textSearched    = savedInstanceState.getString(BUNDLE_TEXT_SEARCHED);
        }
    }

    private String getTextSearched() {
        textSearched = editTextSearch.getText().toString();
        if(!textSearched.equals("") && accessToken != null) {
           return textSearched.replaceAll("\\s", "%20");
        }
        return null;
    }

    public void search(View view) {
        String text = getTextSearched();
        if( text != null) {
            RetroFitSearchTweets retroFitSearchTweets = new RetroFitSearchTweets(handler);
            retroFitSearchTweets.search(accessToken, text, "pt", LIMIT_SEARCH);
            showProgressDialog();
        }
        hiddenKeyBoard();
    }

    private void research(String nextId) {
        String text = getTextSearched();
        if( text != null ) {
            RetroFitSearchTweets retroFitSearchTweets = new RetroFitSearchTweets(handler);
            retroFitSearchTweets.update(accessToken, textSearched, "pt", LIMIT_SEARCH, nextId);
            showProgressDialog();
        }
    }

    public void hiddenKeyBoard() {
        View view = getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

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
}
