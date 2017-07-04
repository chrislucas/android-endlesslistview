package imagineapps.testrequestafterscroll;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.entitiy.Info;
import imagineapps.testrequestafterscroll.http.RequestAuthTwitterAPI;
import imagineapps.testrequestafterscroll.services.ServiceAuthTwitter;
import imagineapps.testrequestafterscroll.services.ServiceDownloadBitmap;
import imagineapps.testrequestafterscroll.services.ServiceSearchTwitterAPI;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Info> list;
    private AdapterListView adapterListView;
    private Button buttonSearch;
    private EditText editTextSearch;
    private String accessToken;

    private boolean isServiceTwitterAuthBinded = false
            ,isServiceTwitterSearchBinded = false
            ,isServiceDownloadBitmapBinded = false;
    private ServiceAuthTwitter serviceAuthTwitter           = null;
    private ServiceSearchTwitterAPI serviceSearchTwitterAPI = null;
    private ServiceDownloadBitmap serviceDownloadBitmap     = null;

    private static final String  bindServiceTwitterAuth     = "bindServiceTwitterAuth";
    private static final String  bindServiceTwitterSearch   = "bindServiceTwitterSearch";
    private static final String  bindServiceDownloadBitmapBinded   = "bindServiceDownloadBitmapBinded";

    public static final int HANDLER_MSG_AUTH_TWITTER        = 0xf0;
    public static final int HANDLER_MSG_TWITTER_SEARCH      = 0xf1;
    public static final int HANDLER_MSG_DOWNLOAD_BITMAP     = 0xf2;
    public static final String BUNDLE_DATA_ARRAYLIST_API    = "BUNDLE_DATA_API";

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
                    updateListInfo(data);
                }
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
                    updateListInfo(data);
                }
                serviceSearchTwitterAPI = null;
                isServiceTwitterSearchBinded = false;
            }
        }
    };

    private void updateListInfo(List<Info> data) {
        if(data != null || data.size() > 0) {
            Iterator<Info> it = list.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            list.addAll(data);
            adapterListView.notifyDataSetChanged();
        }
    }

    private ServiceConnection connectionWithServiceTwitterAuth = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceAuthTwitter = ((ServiceAuthTwitter.LocalBinder) service).getInstance();
            serviceAuthTwitter.setHandler(handlerAuthTwitter);
            serviceAuthTwitter.doRequest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceAuthTwitter = null;
            isServiceTwitterAuthBinded = false;
        }
    };

    private ServiceConnection connectionWithServiceTwitterSearch = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceSearchTwitterAPI = ( (ServiceSearchTwitterAPI.LocalBinder) service).getInstance();
            serviceSearchTwitterAPI.setHandler(handlerTwitterSearch);
            serviceSearchTwitterAPI.doRequest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceSearchTwitterAPI = null;
            isServiceTwitterSearchBinded = false;
        }
    };

    private ServiceConnection connectionWithDownloadBitmap = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceDownloadBitmap = ( (ServiceDownloadBitmap.LocalBinder) service).getInstance();
            serviceDownloadBitmap.setHandler(handlerDownloadBitmap);
            serviceDownloadBitmap.doRequest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceDownloadBitmap = null;
            isServiceDownloadBitmapBinded = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        int resource = android.R.layout.simple_list_item_1;
        adapterListView = new AdapterListView(this, resource, list);
        listView = (ListView) findViewById(R.id.list_data);
        listView.setAdapter(adapterListView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("SCROLL_STATE", String.valueOf(scrollState));
                switch (scrollState) {
                    // O usuario executou o
                    case SCROLL_STATE_FLING:
                        doBindServiceDownloadBitmap();
                        break;
                    // A view nao foi rolada
                    case SCROLL_STATE_IDLE:
                        break;
                    // o usuario rolou a tela e manteve o dedo na tela
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem
                    , int visibleItemCount, int totalItemCount) {
            }
        });
        doBindServiceTwitterAuth();
        editTextSearch  = (EditText) findViewById(R.id.edittext_search);
        buttonSearch    = (Button) findViewById(R.id.button_search);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            outState.putBoolean(bindServiceTwitterAuth, isServiceTwitterAuthBinded);
            outState.putBoolean(bindServiceTwitterSearch, isServiceTwitterSearchBinded);
            outState.putBoolean(bindServiceDownloadBitmapBinded, isServiceDownloadBitmapBinded);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            isServiceTwitterAuthBinded = savedInstanceState.getBoolean(bindServiceTwitterAuth);
            isServiceTwitterSearchBinded = savedInstanceState.getBoolean(bindServiceTwitterSearch);
            isServiceDownloadBitmapBinded = savedInstanceState.getBoolean(bindServiceDownloadBitmapBinded);
        }
    }

    public void search(View view) {
        String text = editTextSearch.getText().toString();
        if(!text.equals("") && accessToken != null) {
            doBindServiceTwitterSearch(text);
        }
    }

    private void doBindServiceTwitterAuth() {
        if(!isServiceTwitterAuthBinded) {
            Intent intent = new Intent(this, ServiceAuthTwitter.class);
            bindService(intent, connectionWithServiceTwitterAuth, Context.BIND_AUTO_CREATE);
            isServiceTwitterAuthBinded = true;
        }
    }

    private void doBindServiceTwitterSearch(String text) {
        if(!isServiceTwitterSearchBinded) {
            Intent intent = new Intent(getApplicationContext(), ServiceSearchTwitterAPI.class);
            Bundle bundle = new Bundle();
            bundle.putString(ServiceSearchTwitterAPI.TEXT_SEARCH, text);
            bundle.putString(ServiceSearchTwitterAPI.TOKEN_AUTHORIZATION, accessToken);
            String url = Uri.parse(String.format("https://api.twitter.com/1.1/search/tweets.json?q=%s&lang=%s&count=%d", text, "pt", 3)).toString();
            bundle.putString(ServiceSearchTwitterAPI.URL, url);
            intent.putExtras(bundle);
            bindService(intent, connectionWithServiceTwitterSearch, Context.BIND_AUTO_CREATE);
            isServiceTwitterSearchBinded = true;
        }
    }

    private void doBindServiceDownloadBitmap() {
        if(!isServiceDownloadBitmapBinded && list.size() > 0) {
            Intent intent = new Intent(getApplicationContext(), ServiceDownloadBitmap.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ServiceDownloadBitmap.BUNDLE_INFO_LIST, (ArrayList<? extends Parcelable>) list);
            intent.putExtras(bundle);
            bindService(intent, connectionWithDownloadBitmap,  Context.BIND_AUTO_CREATE);
            isServiceDownloadBitmapBinded = true;
        }
    }

    private void doUnbindService() {
        if(isServiceTwitterAuthBinded) {
            unbindService(connectionWithServiceTwitterAuth);
            isServiceTwitterAuthBinded = false;
        }
        if(isServiceTwitterSearchBinded) {
            unbindService(connectionWithServiceTwitterSearch);
            isServiceTwitterSearchBinded = false;
        }
        if(isServiceDownloadBitmapBinded) {
            unbindService(connectionWithDownloadBitmap);
            isServiceDownloadBitmapBinded = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
