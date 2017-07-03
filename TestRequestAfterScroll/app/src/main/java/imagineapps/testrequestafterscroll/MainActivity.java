package imagineapps.testrequestafterscroll;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;
import imagineapps.testrequestafterscroll.entitiy.Info;
import imagineapps.testrequestafterscroll.services.ServiceAuthTwitter;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Info> list;
    private boolean isBinded = false;
    private ServiceAuthTwitter serviceAuthTwitter = null;


    public static final int INTERNAL_MSG = 0xf0;
    public static final String BUNDLE_DATA_API = "BUNDLE_DATA_API";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == INTERNAL_MSG) {
                Bundle bundle = msg.getData();
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceAuthTwitter = ((ServiceAuthTwitter.LocalBinder) service).getInstance();
            serviceAuthTwitter.setHandler(handler);
            serviceAuthTwitter.doRequest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceAuthTwitter = null;
            isBinded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        int resource = android.R.layout.simple_list_item_1;
        AdapterListView adapterListView = new AdapterListView(this, resource, list);
        listView = (ListView) findViewById(R.id.list_data);
        listView.setAdapter(adapterListView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem
                    , int visibleItemCount, int totalItemCount) {

            }
        });
        doBindService();
    }


    private void doBindService() {
        Intent intent = new Intent(this, ServiceAuthTwitter.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        isBinded = true;
    }

    private void doUnbindService() {
        if(isBinded) {
            unbindService(serviceConnection);
            isBinded = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
