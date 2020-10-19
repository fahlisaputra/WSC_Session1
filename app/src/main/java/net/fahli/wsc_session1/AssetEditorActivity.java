package net.fahli.wsc_session1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.fahli.wsc_session1.adapter.ListAdapter;
import net.fahli.wsc_session1.adapter.PictureAdapter;
import net.fahli.wsc_session1.entity.Catalogue;
import net.fahli.wsc_session1.entity.Picture;
import net.fahli.wsc_session1.helper.AsyncCallback;
import net.fahli.wsc_session1.helper.HttpConnection;
import net.fahli.wsc_session1.helper.PictureCallback;
import net.fahli.wsc_session1.helper.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.sql.PooledConnection;

public class AssetEditorActivity extends AppCompatActivity implements AsyncCallback, PictureCallback {
    Button btnBrowse, btnCapture, btnSubmit, btnCancel;

    TextView txtAssetSN;
    EditText txtAssetName, txtDescription, txtExpired;
    Spinner spnDepartment, spnLocation, spnAssetGroup, spnAccountable;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_IMAGE_CAPTURE = 2;

    public static final String EXTRA_ASSET = "extra_asset";

    private ArrayList<Picture> pictures = new ArrayList<>();
    private ArrayList<Picture> removedPictures = new ArrayList<>();

    ProgressDialog loadingDialog;
    private ListView listImg;

    // HASH MAP
    private HashMap<Integer, String> departmentMap = new HashMap<>();
    private HashMap<Integer, String> locationMap = new HashMap<>();
    private HashMap<Integer, String> assetGroupMap = new HashMap<>();
    private HashMap<Integer, String> accountableMap = new HashMap<>();

    Catalogue catalogue = null;
    PictureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_editor);
        setTitle("Asset Information");
        btnBrowse = findViewById(R.id.btnBrowse);
        btnCapture = findViewById(R.id.btnCapture);
        listImg = findViewById(R.id.listImg);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        txtAssetSN = findViewById(R.id.txtAssetSN);

        txtAssetName = findViewById(R.id.txtAssetName);
        txtDescription = findViewById(R.id.txtDescription);
        txtExpired = findViewById(R.id.txtExpired);

        spnDepartment = findViewById(R.id.spinnerDepartment);
        spnLocation = findViewById(R.id.spinnerLocation);
        spnAssetGroup = findViewById(R.id.spinnerAssetGroup);
        spnAccountable = findViewById(R.id.spinnerAccountableParty);

        // Load spinner data
        new HttpConnection.DownloadData(AssetEditorActivity.this, 1).execute("http://10.0.2.2/wsc/session1/department.php");
        new HttpConnection.DownloadData(AssetEditorActivity.this, 2).execute("http://10.0.2.2/wsc/session1/assets_group.php");
        new HttpConnection.DownloadData(AssetEditorActivity.this, 3).execute("http://10.0.2.2/wsc/session1/accountable.php");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new HttpConnection.DownloadData(AssetEditorActivity.this, 4).execute("http://10.0.2.2/wsc/session1/location.php?department=" + departmentMap.get(spnDepartment.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            catalogue = getIntent().getParcelableExtra(EXTRA_ASSET);
        } catch (Exception ignored) {}

        if (catalogue != null) {
            txtAssetName.setText(catalogue.getAssetName());
            txtAssetSN.setText(catalogue.getAssetSN());
            txtDescription.setText(catalogue.getDescription());
            txtExpired.setText(catalogue.getWarrantyDate());

            ArrayList<String> photos = catalogue.getPhotos();
            for(int i = 0; i < photos.size(); i++) {
                String id = photos.get(i);
                Picture picture = new Picture();
                picture.setId(Integer.parseInt(id));
                picture.setType("Online");
                pictures.add(picture);
            }

        }
        adapter = new PictureAdapter(AssetEditorActivity.this, pictures, AssetEditorActivity.this);
        listImg.setAdapter(adapter);
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE"};
                int requestCode = 300;
                if (ContextCompat.checkSelfPermission(AssetEditorActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                    Intent picker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(picker, RESULT_LOAD_IMAGE);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissions, requestCode);
                    }
                }


            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permissions[] = {"android.permission.CAMERA"};


                if (ContextCompat.checkSelfPermission(AssetEditorActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                } else {
                    int requestCode = 200;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissions, requestCode);
                    }
                }


            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtAssetName.getText().toString().equals("") || txtDescription.getText().toString().equals("")) {
                    AlertDialog dialog = new AlertDialog.Builder(AssetEditorActivity.this).create();
                    dialog.setTitle("Warning");
                    dialog.setMessage("Asset name and description cannot empty");
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }
                loadingDialog = ProgressDialog.show(AssetEditorActivity.this, null, "Please wait", true, false);
                if (catalogue != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("type", "update");
                    params.put("AssetID", String.valueOf(catalogue.getID()));
                    params.put("EmployeeID", String.valueOf(accountableMap.get(spnAccountable.getSelectedItemPosition())));
                    params.put("AssetName", txtAssetName.getText().toString());
                    params.put("Description", txtDescription.getText().toString());
                    params.put("WarrantyDate", txtExpired.getText().toString());
                    saveData("http://10.0.2.2/wsc/session1/catalogue_crud.php", params);
                    uploadImage(catalogue.getID());
                    removeImage();
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("type", "insert");
                    params.put("AssetName", txtAssetName.getText().toString());
                    params.put("DepartmentLocationID", String.valueOf(locationMap.get(spnLocation.getSelectedItemPosition())));
                    params.put("DepartmentID", String.valueOf(departmentMap.get(spnDepartment.getSelectedItemPosition())));
                    params.put("EmployeeID", String.valueOf(accountableMap.get(spnAccountable.getSelectedItemPosition())));
                    params.put("AssetGroupID", String.valueOf(assetGroupMap.get(spnAssetGroup.getSelectedItemPosition())));
                    params.put("Description", txtDescription.getText().toString());
                    params.put("WarrantyDate", txtExpired.getText().toString());
                    saveData("http://10.0.2.2/wsc/session1/catalogue_crud.php", params);
                    removeImage();
                }

            }
        });

        txtExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(AssetEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtExpired.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                picker.show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
            }
        } else if (requestCode == 300) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent picker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picker, RESULT_LOAD_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Picture picture = new Picture();
            picture.setId(-1);
            picture.setType("Local");
            picture.setBitmap(BitmapFactory.decodeFile(picturePath));
            pictures.add(picture);
            adapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Picture picture = new Picture();
            picture.setId(-1);
            picture.setType("Local");
            picture.setBitmap(bitmap);
            pictures.add(picture);
            adapter.notifyDataSetChanged();
        }
    }

    public String getImageBinary(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        //return new String(imageBytes, StandardCharsets.UTF_8);
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }
    private void saveData(String url, HashMap<String, String> params) {
        class SaveData extends AsyncTask<HashMap<String, String>, Void, String> {
            RequestHandler rh = new RequestHandler();
            String url;
            WeakReference<AsyncCallback> callback;
            public SaveData(String url, AsyncCallback callback) {
                this.url = url;
                this.callback = new WeakReference<>(callback);
            }

            @Override
            protected void onPostExecute(String s) {
                AsyncCallback listener = this.callback.get();
                if (listener != null) {
                    listener.onPostExecute(s, 10);
                }
            }

            @Override
            protected String doInBackground(HashMap<String, String>... hashMaps) {
                HashMap<String, String> data = hashMaps[0];
                return rh.sendPOSTRequest(url, data);
            }
        }

        SaveData saveData = new SaveData(url, AssetEditorActivity.this);
        saveData.execute(params);
    }
    private void uploadImage(int id) {
        class UploadImage extends AsyncTask<ArrayList<Picture>, Void, String> {
            RequestHandler rh = new RequestHandler();
            String url;
            String id;
            public UploadImage(String url, String id) {
                this.url = url;
                this.id = id;
            }

            @Override
            protected String doInBackground(ArrayList<Picture>... arrayLists) {
                ArrayList<Picture> bitmaps = arrayLists[0];
                String response = "100";
                for (int i = 0; i < bitmaps.size(); i++) {
                    Picture picture = bitmaps.get(i);
                    if (picture.getType().equals("Local")) {
                        String binaryImage = getImageBinary(picture.getBitmap());
                        HashMap<String, String> data = new HashMap<>();
                        data.put("image", binaryImage);
                        data.put("id", id);
                        String result = rh.sendPOSTRequest(url, data);
                    }

                    response = "200";
                }
                return response;
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onPostExecute(String s) {

            }
        }

        UploadImage uploadImage = new UploadImage("http://10.0.2.2/wsc/session1/upload.php", String.valueOf(id));
        uploadImage.execute(pictures);

    }

    private void removeImage() {
        class RemoveImage extends AsyncTask<ArrayList<Picture>, Void, String> {
            RequestHandler rh = new RequestHandler();
            String url;

            public RemoveImage(String url) {
                this.url = url;
            }

            @Override
            protected void onPostExecute(String s) {
                loadingDialog.dismiss();
                finish();
            }

            @Override
            protected String doInBackground(ArrayList<Picture>... arrayLists) {
                ArrayList<Picture> pictures = arrayLists[0];
                for (int i = 0; i < pictures.size(); i++) {
                    Picture pic = pictures.get(i);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(pic.getId()));
                    String result = rh.sendPOSTRequest(url, params);
                }
                return "success";
            }
        }

        RemoveImage removeImage = new RemoveImage("http://10.0.2.2/wsc/session1/remove_image.php");
        removeImage.execute(removedPictures);

    }

    @Override
    public void onPostExecute(String data, int type) {
        if (type == 1) {
            ArrayList<String> department = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    department.add(object.getString("Name"));
                    departmentMap.put(i, object.getString("ID"));
                }

                spnDepartment.setAdapter(new ArrayAdapter<String>(AssetEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, department));

                if (catalogue != null) {
                    ArrayAdapter adapter = (ArrayAdapter) spnDepartment.getAdapter();
                    int position = adapter.getPosition(catalogue.getDepartmentName());
                    spnDepartment.setSelection(position);
                    spnDepartment.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 2) {
            ArrayList<String> group = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    group.add(object.getString("Name"));
                    assetGroupMap.put(i, object.getString("ID"));
                }

                spnAssetGroup.setAdapter(new ArrayAdapter<String>(AssetEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, group));
                if (catalogue != null) {
                    ArrayAdapter adapter = (ArrayAdapter) spnAssetGroup.getAdapter();
                    int position = adapter.getPosition(catalogue.getAssetGroupName());
                    spnAssetGroup.setSelection(position);
                    spnAssetGroup.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 3) {
            ArrayList<String> accountable = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                int savedID = -1;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    accountable.add(object.getString("FirstName") + " " + object.getString("LastName"));
                    accountableMap.put(i, object.getString("ID"));
                    if (catalogue != null) {
                        if (catalogue.getEmployeeID() == object.getInt("ID")) {
                            savedID = i;
                        }
                    }
                }
                spnAccountable.setAdapter(new ArrayAdapter<String>(AssetEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, accountable));
                if (catalogue != null) {
                    spnAccountable.setSelection(savedID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 4) {
            ArrayList<String> location = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(data);
                int savedID = -1;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    location.add(object.getString("Name"));
                    locationMap.put(i, object.getString("ID"));
                    if (catalogue != null) {
                        if (catalogue.getDepartmentLocationID() == object.getInt("ID")) {
                            savedID = i;
                        }
                    }
                }
                spnLocation.setAdapter(new ArrayAdapter<String>(AssetEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, location));
                if (catalogue != null) {
                    spnLocation.setSelection(savedID);
                    spnLocation.setEnabled(false);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 10) {
            try {
                if (catalogue == null) {
                    JSONObject object = new JSONObject(data);
                    int id = object.getInt("id");
                    uploadImage(id);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRemoveClicked(Picture picture) {
        pictures.remove(picture);
        if (picture.getType().equals("Online")) {
            removedPictures.add(picture);
        }
        adapter.notifyDataSetChanged();
    }
}