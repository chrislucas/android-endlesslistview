package imagineapps.testrequestafterscroll;

import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.rqretrofit.RetroFitAuthTwitter;
import imagineapps.testrequestafterscroll.utils.BuildProgressDialog;

/**
 * Implementar a mesma ideia da MainActivity porem usando RetroFit para aprender
 * a usar a biblioteca
 *
 * */

public class Main2Activity extends AppCompatActivity {
    private AdapterListView adapterListView;
    private ListView listView;
    private List<Info> completeList, auxiliarList;
    private Button buttonSearch;
    private EditText editTextSearch;
    private TextView quantityMessage;
    private String accessToken, textSearched;
    private int countPost;
    private static final int LIMIT_SEARCH = 7;
    private BuildProgressDialog pDialogSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            completeList = new ArrayList<>();
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
                                //requestNewInformation(lastInfoId, firstInfoId);
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
                        // requestNewInformation(lastInfoId, firstInfoId);
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
        getAccessToken();
    }

    private void getAccessToken() {
        RetroFitAuthTwitter retroFitAuthTwitter = new RetroFitAuthTwitter();
        this.accessToken = retroFitAuthTwitter.getToken().getAccessToken();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void search(View view) {

    }
}
