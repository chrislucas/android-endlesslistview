package imagineapps.testrequestafterscroll;

import android.content.Context;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.db.helpers.TableHelperInfo;
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
    private static final int MIN_RESULT_SEARCH = 7;
    private static final int MIN_SIZE_TO_SAVE  = 14;

    private BuildProgressDialog pDialogSearch;

    public static final String BUNLDE_COMPLETE_INFO_LIST = "BUNLDE_COMPLETE_INFO_LIST";
    public static final String BUNLDE_AUXILIAR_INFO_LIST = "BUNLDE_AUXILIAR_INFO_LIST";
    public static final String BUNLDE_SIZE_INFO_LIST     = "BUNLDE_SIZE_INFO_LIST";
    public static final String BUNDLE_ACCESS_TOKEN       = "BUNDLE_ACCESS_TOKEN";
    public static final String BUNDLE_TEXT_SEARCHED      = "BUNDLE_TEXT_SEARCHED";
    public static final String BUNDLE_DOING_SEARCH       = "BUNDLE_DOING_SEARCH";

    private TableHelperInfo tableHelperInfo = null;

    private boolean doingSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            /**
             * metodo de configuracao da base de dados esta aqui por esse projeto se tratar
             * de um prototipo
             * Lembrar de coloca-lo numa activity que funcione como splash screen
             * */
            configDatabase();
            TableHelperInfo tableHelperInfo = getTableHelperInfo();
            completeList = tableHelperInfo.getAll();
            if(completeList.size() > 0)
                Collections.sort(completeList);
            /**
             * Para o app de verdade, se a lista estiver vazia, implementar
             * um metodo que busque os ultimos N posts da API da empresa
             * */
            auxiliarList = new ArrayList<>();
        }
        else {
            completeList    = savedInstanceState.getParcelableArrayList(BUNLDE_COMPLETE_INFO_LIST);
            auxiliarList    = savedInstanceState.getParcelableArrayList(BUNLDE_AUXILIAR_INFO_LIST);
            countPost       = savedInstanceState.getInt(BUNLDE_SIZE_INFO_LIST);
            accessToken     = savedInstanceState.getString(BUNDLE_ACCESS_TOKEN);
            textSearched    = savedInstanceState.getString(BUNDLE_TEXT_SEARCHED);
        }
        quantityMessage = (TextView) findViewById(R.id.quantity_data);
        int resource    = android.R.layout.simple_list_item_1;
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
                Info firstInfo      = null;
                /**]
                 *
                 * Se a lista auxiliar estiver vazia e ocorrer um scroll
                 * quer dizer que o usuario tinha posts armazenados na base de dados
                 * */
                if(auxiliarList.size() == 0) {
                    // o primeiro da lista completa eh o mais recente
                    firstInfo = completeList.get(0);
                }
                else {
                    firstInfo = auxiliarList.get(auxiliarList.size() - 1);
                }
                String firstInfoId  = firstInfo.getId();
                int diff = (qPosts - lastVisiblePosition);
                switch (scrollState) {
                    // O usuario executou o
                    case SCROLL_STATE_FLING:
                        if(!scrollUp &&  diff > 0 && diff  < 3) {
                            Log.i("UPDATE_POST", "DOWNLOAD_POSTS");
                            if(textSearched != null && !textSearched.equals("") && accessToken != null && ! doingSearch) {
                                research(firstInfoId);
                            }
                        }
                        /**
                         * TODO
                         * implementar funcionalidade para pesquisar na API caso o usuario faça
                         * scroll para cima.
                         * Pesquisar N posts a partir do post com a data mais antiga no app, para tras
                         * */
                        else {

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
                 * Se o usuario chegar ao fim da lista, o listener nao sabera identificar
                 * que o mesmo pode fazer um movimento da tela
                 * */
                if(!scrollUp &&  (diff > 0 && diff  < 3) && ! doingSearch) {
                    Log.i("UPDATE_POST", "FIM DA LISTA. DOWNLOAD_POSTS");
                    if(textSearched != null && ! textSearched.equals("") && accessToken != null) {
                        research(firstInfoId);
                    }
                }
                /**
                 * TODO
                 * implementar funcionalidade para pesquisar na API caso o usuario faça
                 * scroll para cima.
                 * Pesquisar N posts a partir do post com a data mais antiga no app, para tras
                 * */
                else {

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
    }

    private void configDatabase() {
        if(!BuildDatabase.checkDbExists(this)) {
            boolean create = BuildDatabase.createDbIfNotExists(this);
            if(create) {
                /**
                 * Se a Base foi criada, criar a tabela de Posts
                 * */
                TableHelperInfo tableHelperInfo = getTableHelperInfo();
                tableHelperInfo.createTable();
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
            if(doingSearch)
                doingSearch = false;
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

    private TableHelperInfo getTableHelperInfo() {
        if(tableHelperInfo == null) {
            try {
                tableHelperInfo = new TableHelperInfo(this);
                /**
                 * O metodo createTable talvez nao fique aqui numa versao de aplicativo real.
                 * Esse eh so um prototipo
                 * */
                tableHelperInfo.createTable();
            }
            catch (Exception e) {
                Log.e("ERR_TBL_HELPER_INFO", e.getMessage());
            }
        }
        return tableHelperInfo;
    }

    private void showProgressDialog(String title, String message) {
        try {
            pDialogSearch.buildDefault(true, false
                    , title, message).safeShowing();
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
            outState.putBoolean(BUNDLE_DOING_SEARCH, doingSearch);
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
            doingSearch     = savedInstanceState.getBoolean(BUNDLE_DOING_SEARCH);
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
            retroFitSearchTweets.search(accessToken, text, "pt", MIN_RESULT_SEARCH);
            showProgressDialog("Pesquisando.", "Aguarde enquando a pesquisa está sendo realizada.");
            doingSearch = true;
        }
        hiddenKeyBoard();
    }

    /**
     * TODO guando o usuario fizer scroll na lista de post, realizar uma nova pesquisa por
     * mais Posts.
     * */
    private void research(String nextId) {
        doingSearch = true;
        /**
         * TODO para evitar fazer IO no banco a cada scroll vamos implementar
         * uma regra que verifique se a lista de posts tem um tamanho minimo
         * Assim, guaradamos N posts de uma unica vez
         *
         * Para não realizar inserções na base de dados toda vez que o usuairo fizer
         * um scroll e baixar um post, sera implementado u
         *
         * */
        long mod = completeList.size() % MIN_SIZE_TO_SAVE;
        /**
         * Deixar a lista completa ficar no minimo o 2 x a quantidade de posts baixos - 1
         * */
        if(mod == MIN_SIZE_TO_SAVE-1 || mod == 0 ) {
            saveList();
        }
        String text = getTextSearched();
        if( text != null ) {
            RetroFitSearchTweets retroFitSearchTweets = new RetroFitSearchTweets(handler);
            retroFitSearchTweets.update(accessToken, textSearched, "pt", MIN_RESULT_SEARCH, nextId);
            showProgressDialog("Buscando novos Posts.", "Aguarde enquando a pesquisa está sendo realizada.");
        }
    }

    /**
     * TODO implementar algoritmo para salvar a lista de Posts no BANCO
     *
     * Enquanto o usuario executa o scroll na lista, mais pots sao baixados.
     * Precisamos guardar essa informacao no banco de dados para que o usuario
     * nao precise baixar um Post toda a vez que ele quiser ve-lo.
     *
     * */
    private void saveList() {
        Toast.makeText(this, "Salvando posts", Toast.LENGTH_SHORT).show();
        TableHelperInfo tableHelperInfo = getTableHelperInfo();
        if(tableHelperInfo != null) {
            Log.i("SAVING_POSTS", "Iniciando processo de armazenamneto de posts");
            /**
             * Criar uma sublista da lista completa, para salvar somente os posts novos
             * evitando de salvar informacao repitida
             * */
            int size        = completeList.size();
            int higherLimit = size;
            int lowerLimit  = size - MIN_SIZE_TO_SAVE;
            // de l ate h (h sendo exclusivo)
            Log.i("RANGE", String.format("(%d, %d)", lowerLimit, higherLimit));
            List<Info> subList = completeList.subList(lowerLimit, higherLimit);
            tableHelperInfo.insertAll(subList);
        }
    }

    private void saveList2() {
        TableHelperInfo tableHelperInfo = getTableHelperInfo();
        if(tableHelperInfo != null) {
            Log.i("SAVING_POSTS", "Iniciando processo de armazenamneto de posts");
            /**
             * Criar uma sublista da lista completa, para salvar somente os posts novos
             * evitando de salvar informacao repitida
             *
             * */
            tableHelperInfo.insertAll(auxiliarList);
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
            /**
             * Podemos salvar os novos posts assim que forem baixados.
             * */
            auxiliarList = new ArrayList<>();
            auxiliarList.addAll(data);
            int lastIdx = completeList.size() == 0 ? 0 : completeList.size() - 1;
            completeList.addAll(lastIdx, auxiliarList);
            Collections.sort(completeList);
            adapterListView.notifyDataSetChanged();
            updateInfoSizeList();
        }
        else {
            Toast.makeText(this, "Não a mais Posts recentes", Toast.LENGTH_LONG).show();
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


}
