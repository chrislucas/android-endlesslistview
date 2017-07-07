package imagineapps.testrequestafterscroll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import imagineapps.testrequestafterscroll.adapter.AdapterListView;

/**
 * Implementar a mesma ideia da MainActivity porem usando RetroFit para aprender
 * a usar a biblioteca
 *
 * */

public class Main2Activity extends AppCompatActivity {

    private AdapterListView adapterListView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
