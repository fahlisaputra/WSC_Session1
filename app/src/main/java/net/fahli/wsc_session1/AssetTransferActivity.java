package net.fahli.wsc_session1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import net.fahli.wsc_session1.entity.Catalogue;
import net.fahli.wsc_session1.helper.AsyncCallback;
import net.fahli.wsc_session1.helper.HttpConnection;
import net.fahli.wsc_session1.helper.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AssetTransferActivity extends AppCompatActivity implements AsyncCallback {
    Spinner spnDepartment, spnLocation;
    EditText txtAssetName, txtCurrentDepartment, txtAssetSN, txtNewAssetSN;
    Button btnSubmit, btnCancel;

    HashMap<Integer, String> departmentMap = new HashMap<>();
    HashMap<Integer, String> locationMap = new HashMap<>();

    public static final String EXTRA_ASSET = "extra_asset";

    private String departmentID = "";
    private String assetGroupID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_transfer);
        setTitle("Asset Transfer");
        spnDepartment = findViewById(R.id.spnDepartment);
        spnLocation = findViewById(R.id.spnLocation);
        txtAssetName = findViewById(R.id.txtAssetName);
        txtCurrentDepartment = findViewById(R.id.txtDepartmentName);
        txtAssetSN = findViewById(R.id.txtAssetSN);
        txtNewAssetSN = findViewById(R.id.txtNewAssetSN);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new HttpConnection.DownloadData(AssetTransferActivity.this, 1).execute("http://10.0.2.2/wsc/session1/department.php");
        final Catalogue catalogue = getIntent().getParcelableExtra(EXTRA_ASSET);
        txtAssetName.setText(catalogue.getAssetName());
        txtCurrentDepartment.setText(catalogue.getDepartmentName());
        txtAssetSN.setText(catalogue.getAssetSN());
        assetGroupID = String.format("%02d", catalogue.getAssetGroupID());

        spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new HttpConnection.DownloadData(AssetTransferActivity.this, 2).execute("http://10.0.2.2/wsc/session1/location.php?department=" + departmentMap.get(spnDepartment.getSelectedItemPosition()));
                departmentID = String.format("%02d", Integer.parseInt(departmentMap.get(spnDepartment.getSelectedItemPosition())));
                txtNewAssetSN.setText(departmentID + "/" + assetGroupID + "/????");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("AssetID", String.valueOf(catalogue.getID()));
                params.put("FromAssetSN", catalogue.getAssetSN());
                params.put("DepartmentID", String.valueOf(catalogue.getDepartmentID()));
                params.put("AssetGroupID", String.valueOf(catalogue.getAssetGroupID()));
                params.put("FromDepartmentLocationID", String.valueOf(catalogue.getDepartmentLocationID()));
                params.put("ToDepartmentLocationID", locationMap.get(spnLocation.getSelectedItemPosition()));
                saveData("http://10.0.2.2/wsc/session1/transfer.php", params);
            }
        });

    }

    @Override
    public void onPostExecute(String data, int type) {
        if (type == 1) {
            ArrayList<String> departments = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    departmentMap.put(i, object.getString("ID"));
                    departments.add(object.getString("Name"));
                }
                spnDepartment.setAdapter(new ArrayAdapter<String>(AssetTransferActivity.this, android.R.layout.simple_spinner_dropdown_item, departments));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 2) {
            ArrayList<String> locations = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    locationMap.put(i, object.getString("ID"));
                    locations.add(object.getString("Name"));
                }
                spnLocation.setAdapter(new ArrayAdapter<String>(AssetTransferActivity.this, android.R.layout.simple_spinner_dropdown_item, locations));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData(String url, HashMap<String, String> params) {
        class SaveData extends AsyncTask<HashMap<String, String>, Void, String> {
            RequestHandler rh = new RequestHandler();
            String url;

            public SaveData(String url) {
                this.url = url;
            }

            @Override
            protected void onPostExecute(String s) {
                finish();
            }

            @Override
            protected String doInBackground(HashMap<String, String>... hashMaps) {
                HashMap<String, String> data = hashMaps[0];
                return rh.sendPOSTRequest(url, data);
            }
        }

        SaveData saveData = new SaveData(url);
        saveData.execute(params);
    }
}