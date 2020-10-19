package net.fahli.wsc_session1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.fahli.wsc_session1.adapter.ListAdapter;
import net.fahli.wsc_session1.entity.Catalogue;
import net.fahli.wsc_session1.helper.AsyncCallback;
import net.fahli.wsc_session1.helper.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AsyncCallback {
    private ListAdapter adapter;
    private Spinner department_name, asset_group;
    ListView lv;
    private TextView txtCounter;
    private EditText txtStartDate, txtEndDate, txtSearch;
    DatePickerDialog picker;
    FloatingActionButton fab;
    ProgressDialog progressDialog;

    // SAVED VARIABLE
    private String SELECTED_DEPARTMENT = "";
    private String SELECTED_GROUP = "";
    private String START_DATE = "";
    private String END_DATE = "";
    private String SEARCH = "";

    // HASH MAP
    private HashMap<Integer, String> departmentMap = new HashMap<>();
    private HashMap<Integer, String> assetGroupMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.list_view);
        department_name = findViewById(R.id.department_name);
        asset_group = findViewById(R.id.assetGroup);
        txtCounter = findViewById(R.id.counter);
        txtStartDate = findViewById(R.id.start_date);
        txtEndDate = findViewById(R.id.end_date);
        fab = findViewById(R.id.fab);
        txtSearch = findViewById(R.id.txtSearch);
        adapter = new net.fahli.wsc_session1.adapter.ListAdapter(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait while we doing something...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new HttpConnection.DownloadData(MainActivity.this, 1).execute("http://10.0.2.2/wsc/session1/department.php");
        new HttpConnection.DownloadData(MainActivity.this, 2).execute("http://10.0.2.2/wsc/session1/assets_group.php");
        new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);

        try {
            asset_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //SELECTED_GROUP = asset_group.getSelectedItem().toString();
                    SELECTED_GROUP = assetGroupMap.get(asset_group.getSelectedItemPosition());
                    new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            department_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //SELECTED_DEPARTMENT = department_name.getSelectedItem().toString();
                    SELECTED_DEPARTMENT = departmentMap.get(department_name.getSelectedItemPosition());
                    new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            txtStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    picker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            START_DATE = year + "-" + (month + 1) + "-" + dayOfMonth;
                            try {
                                txtStartDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } catch (Exception ignored) {}

                            new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
                        }
                    }, year, month, day);
                    picker.show();
                }
            });

            txtEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    picker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            END_DATE = year + "-" + (month + 1) + "-" + dayOfMonth;
                            try {
                                txtEndDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } catch (Exception ignored) {}

                            new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
                        }
                    }, year, month, day);
                    picker.show();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, AssetEditorActivity.class));
                }
            });

            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    SEARCH = String.valueOf(s);
                    new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception ignored) {}



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        new HttpConnection.DownloadData(MainActivity.this, 0).execute("http://10.0.2.2/wsc/session1/catalogue.php?search=" + SEARCH);
    }

    @Override
    public void onPostExecute(String data, int type) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (type == 0) {
            int totalAsset = 0;
            int filteredAsset = 0;
            ArrayList<Catalogue> catalogues = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONArray photos = object.getJSONArray("photos");
                    totalAsset++;
                    if (SELECTED_DEPARTMENT.equals(object.getString("DepartmentID")) && SELECTED_GROUP.equals(object.getString("AssetGroupID"))) {
                        if (START_DATE.equals("") && END_DATE.equals("")) {
                            Catalogue catalogue = new Catalogue();
                            catalogue.setID(object.getInt("ID"));
                            catalogue.setAssetSN(object.getString("AssetSN"));
                            catalogue.setAssetName(object.getString("AssetName"));
                            catalogue.setWarrantyDate(object.getString("warrantyDate"));
                            catalogue.setDepartmentName(object.getString("DepartmentName"));
                            catalogue.setAssetGroupName(object.getString("AssetGroupName"));
                            catalogue.setDepartmentLocationID(object.getInt("DepartmentLocationID"));
                            catalogue.setAssetGroupID(object.getInt("AssetGroupID"));
                            catalogue.setEmployeeID(object.getInt("EmployeeID"));
                            catalogue.setDescription(object.getString("Description"));
                            catalogue.setDepartmentID(object.getInt("DepartmentID"));

                            ArrayList<String> photoList = new ArrayList<>();
                            for (int a = 0; a < photos.length(); a++) {
                                JSONObject item = photos.getJSONObject(a);
                                photoList.add(item.getString("id"));
                            }
                            catalogue.setPhotos(photoList);

                            catalogues.add(catalogue);
                            filteredAsset++;
                        } else {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
                            Date warranty = format1.parse(object.getString("warrantyDate"));
                            Date startDate = format.parse(START_DATE);
                            Date endDate = format.parse(END_DATE);
                            if (!warranty.before(startDate) && !warranty.after(endDate)) {
                                Catalogue catalogue = new Catalogue();
                                catalogue.setID(object.getInt("ID"));
                                catalogue.setAssetSN(object.getString("AssetSN"));
                                catalogue.setAssetName(object.getString("AssetName"));
                                catalogue.setWarrantyDate(object.getString("warrantyDate"));
                                catalogue.setDepartmentName(object.getString("DepartmentName"));
                                catalogue.setAssetGroupName(object.getString("AssetGroupName"));
                                catalogue.setDepartmentLocationID(object.getInt("DepartmentLocationID"));
                                catalogue.setAssetGroupID(object.getInt("AssetGroupID"));
                                catalogue.setEmployeeID(object.getInt("EmployeeID"));
                                catalogue.setDescription(object.getString("Description"));
                                catalogue.setDepartmentID(object.getInt("DepartmentID"));

                                ArrayList<String> photoList = new ArrayList<>();
                                for (int a = 0; a < photos.length(); a++) {
                                    JSONObject item =photos.getJSONObject(a);
                                    photoList.add(item.getString("id"));
                                }
                                catalogue.setPhotos(photoList);

                                catalogues.add(catalogue);
                                filteredAsset++;
                            }
                        }

                    }

                }
                adapter.setCatalogues(catalogues);
                lv.setAdapter(adapter);
                txtCounter.setText(String.valueOf(filteredAsset) + " assets from " + String.valueOf(totalAsset));
            } catch (Exception ignored) {

            }
        } else if (type == 1) {
            ArrayList<String> department = new ArrayList<String>();

            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    department.add(object.getString("Name"));
                    departmentMap.put(i, object.getString("ID"));
                }
                department_name.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, department));
            } catch (Exception ignored) {

            }
        } else if (type == 2) {
            ArrayList<String> groups = new ArrayList<String>();
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    groups.add(jsonObject.getString("Name"));
                    assetGroupMap.put(i, jsonObject.getString("ID"));
                }
                asset_group.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, groups));
            } catch (Exception ignored) {

            }
        }

    }


}