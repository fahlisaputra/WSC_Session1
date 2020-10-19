package net.fahli.wsc_session1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import net.fahli.wsc_session1.adapter.HistoryAdapter;
import net.fahli.wsc_session1.entity.History;
import net.fahli.wsc_session1.helper.AsyncCallback;
import net.fahli.wsc_session1.helper.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransferHistoryActivity extends AppCompatActivity implements AsyncCallback {
    ListView listView;

    public static final String EXTRA_ID = "extra_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_history);
        setTitle("Asset Transfer History");
        Button btnBack = findViewById(R.id.btnBack);
        listView = findViewById(R.id.listView);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int id = getIntent().getIntExtra(EXTRA_ID, 0);
        new HttpConnection.DownloadData(TransferHistoryActivity.this, 1).execute("http://10.0.2.2/wsc/session1/transfer_history.php?id=" + id);
    }

    @Override
    public void onPostExecute(String data, int type) {
        if (type == 1) {
            ArrayList<History> histories = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    History history = new History();
                    history.setRelocationDate(object.getString("TransferDate"));
                    history.setNewAssetSN(object.getString("ToAssetSN"));
                    history.setOldAssetSN(object.getString("FromAssetSN"));
                    history.setOldDepartment(object.getString("FromDepartment"));
                    history.setNewDepartment(object.getString("ToDepartment"));
                    histories.add(history);
                }

                HistoryAdapter adapter = new HistoryAdapter(histories, TransferHistoryActivity.this);
                listView.setAdapter(adapter);
            } catch (Exception e) {

            }
        }
    }
}