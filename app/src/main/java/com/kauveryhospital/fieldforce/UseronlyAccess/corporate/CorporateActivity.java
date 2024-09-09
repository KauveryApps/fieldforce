package com.kauveryhospital.fieldforce.UseronlyAccess.corporate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.Globaldeclare.APIClient;
import com.kauveryhospital.fieldforce.Globaldeclare.APIInterface;
import com.kauveryhospital.fieldforce.Globaldeclare.Example;
import com.kauveryhospital.fieldforce.Globaldeclare.Result;
import com.kauveryhospital.fieldforce.Globaldeclare.Result_;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.corporate.savedata.APIClientSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.corporate.savedata.APIInterfaceSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.corporate.savedata.ExampleSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.corporate.savedata.ResultSave;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CorporateActivity extends AppCompatActivity implements NetworkChangeCallback {
    Toolbar toolbars;
    Bitmap myBitmap;
    ArrayAdapter<String> adapter;
    Uri picUri;
    ImageView backarrow;
    ListCorporateAdapter adapters;
    EditText etcorpname, etcorpaddress;
    APIInterface apiInterface;
    APIInterfaceSave apiInterfaceSave;
    Button btncorpsubmit;
    HashMap<String, String> map;
    String name, address, city, area, pincode, state;
    Spinner PartSpinnerState, PartSpinnerCity, PartSpinnerArea, PartSpinnerPincode;
    public static final String PREFS_NAME = "loginpref";
    static final String PREFS_NAMES = "preferenceName";
    List<String> PartNameState;
    List<String> PartIdState;

    List<String> PartNameCity;
    List<String> PartIdCity;

    List<String> PartNameArea;
    List<String> PartIdArea;

    List<String> PartNamePincode;
    List<String> PartIdPincode;

    List<Result> list = new ArrayList<>();
    List<ResultSave> listsave = new ArrayList<>();
    int j = 0;
    List<Result_> list1 = new ArrayList<>();
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    ImageView viewlist;
    ArrayList<HashMap<String, String>> arraylist;
    CheckBox checkBox;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private NetworkChangeReceiver networkChangeReceiver;
    String savecorpname, savecorpaddress, pswd, statuscheckbox, latitude, longitude, message, uname, Statename, stateId, part_IdState, part_NameState, Cityname, cityId, part_IdCity, part_NameCity, Areaname, areaId, part_IdArea, part_NameArea, Pincodename, pincodeId, part_IdPincode, part_NamePincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporate);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");

        viewlist = findViewById(R.id.viewlist);
        btncorpsubmit = findViewById(R.id.btnLogin);
        checkBox = findViewById(R.id.checkBox);

        SharedPreferences settings = getSharedPreferences(PREFS_NAMES, 0);
        latitude = settings.getString("curlatitude", "");
        longitude = settings.getString("curlongitude", "");

        PartSpinnerState = (Spinner) findViewById(R.id.state);
        PartNameState = new ArrayList<>();
        PartIdState = new ArrayList<>();
        postdatastate();
        PartSpinnerCity = (Spinner) findViewById(R.id.city);
        PartNameCity = new ArrayList<>();
        PartIdCity = new ArrayList<>();

        PartSpinnerArea = (Spinner) findViewById(R.id.area);
        PartNameArea = new ArrayList<>();
        PartIdArea = new ArrayList<>();

        PartSpinnerPincode = (Spinner) findViewById(R.id.pincode);
        PartNamePincode = new ArrayList<>();
        PartIdPincode = new ArrayList<>();

        backarrow = findViewById(R.id.backarrow);

        etcorpname = findViewById(R.id.corpname);
        etcorpaddress = findViewById(R.id.corpaddress);
        btncorpsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    validation();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CorporateActivity.this, ListCorporateActivity.class);
                startActivity(intent);
            }
        });
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CorporateActivity.this, TabbedActivity.class);
                startActivity(intent);
            }
        });

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void validation() {
        if (etcorpname.getText().toString().trim().length() == 0) {
            etcorpname.setError("Username is not entered");
            etcorpname.requestFocus();
            return;
        }
        if(etcorpaddress.getText().toString().trim().length()==0){
            etcorpaddress.setError("Address is not entered");
            etcorpaddress.requestFocus();
            return;
        }
        else {
            statuscheckbox = "active";
            savecorpname = etcorpname.getText().toString();
            savecorpaddress = etcorpaddress.getText().toString();
            try {
                postData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    boolean validateSpinner(Spinner spinner, String error) {
        View SelectedView=spinner.getSelectedView();
        if(SelectedView!=null && SelectedView instanceof TextView){
            TextView selectedTextView=(TextView)SelectedView;
            if (((TextView) SelectedView).getText().equals("Select State")){
                selectedTextView.setError(error);
                Toast.makeText(this,error,Toast.LENGTH_LONG);
                return false;
            }
        }
        return true;
    }

    private void postdatastate() {
        final ProgressDialog loading = new ProgressDialog(CorporateActivity.this);
        loading.setMessage("Loading State...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select state_name,stateid from STATE where ACTIVE = 'T' order by state_name");
        array.put(jsonObject5);
        try {

            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);
            JsonArray array1 = new JsonArray();
            array1.add(jsonObject6);
            jsonObject7.add("_parameters", array1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Example> call4 = apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                loading.dismiss();

                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {
                        Statename = list.get(0).getResult().getRow().get(i).getstate_name();
                        stateId = list.get(0).getResult().getRow().get(i).getstateid();
                        PartNameState.add(Statename);
                        PartIdState.add(stateId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new ArrayAdapter<String>(CorporateActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameState);
                PartSpinnerState.setAdapter(adapter);
                PartSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        part_IdState = PartIdState.get(position);
                        part_NameState = PartNameState.get(position);
                        int SelectedItemOfMySpinner=PartSpinnerState.getSelectedItemPosition();
                        String actualposition=(String) PartSpinnerState.getItemAtPosition(SelectedItemOfMySpinner);
                        if(actualposition.isEmpty()){
                            setSpinnerError(PartSpinnerState,"errrrors");
                        }
                        postdatacity();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorporateActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }
    private  void setSpinnerError(Spinner spinner,String error){
        View selectedView=spinner.getSelectedView();
        if(selectedView!=null && selectedView instanceof TextView){
            spinner.requestFocus();
            TextView selectedTextView=(TextView)selectedView;
            selectedTextView.setError("Error");
            selectedTextView.setText(error);
            spinner.performClick();
        }

    }
    private void postdatacity() {
        final ProgressDialog loading = new ProgressDialog(CorporateActivity.this);
        loading.setMessage("Loading City...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select cityid,city_name from city where STATEID = '" + part_IdState + "' and active = 'T' order by city_name");
        array.put(jsonObject5);
        try {

            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);
            JsonArray array1 = new JsonArray();
            array1.add(jsonObject6);
            jsonObject7.add("_parameters", array1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Example> call4 = apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                loading.dismiss();

                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {

                        Cityname = list.get(0).getResult().getRow().get(i).getCity_name();
                        cityId = list.get(0).getResult().getRow().get(i).getCityid();
                        PartNameCity.add(Cityname);
                        PartIdCity.add(cityId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CorporateActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameCity);
                PartSpinnerCity.setAdapter(adapter);
                PartSpinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        part_IdCity = PartIdCity.get(position);
                        part_NameCity = PartNameCity.get(position);
                        postdataArea();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorporateActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdataArea() {
        final ProgressDialog loading = new ProgressDialog(CorporateActivity.this);
        loading.setMessage("Loading Area...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");

        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select area_name,area_masterid from AREA_MASTER  where CITY = '" + part_IdCity + "' order by area_name");
        array.put(jsonObject5);
        try {
            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);
            JsonArray array1 = new JsonArray();
            array1.add(jsonObject6);
            jsonObject7.add("_parameters", array1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Example> call4 = apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                loading.dismiss();

                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {

                        Areaname = list.get(0).getResult().getRow().get(i).getArea_name();
                        areaId = list.get(0).getResult().getRow().get(i).getArea_masterid();
                        PartNameArea.add(Areaname);
                        PartIdArea.add(areaId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CorporateActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameArea);
                PartSpinnerArea.setAdapter(adapter);
                PartSpinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        part_IdArea = PartIdArea.get(position);
                        part_NameArea = PartNameArea.get(position);
                        // Toast.makeText(getApplicationContext(),part_IdArea,Toast.LENGTH_SHORT);
                        postdataPincode();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorporateActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdataPincode() {
        final ProgressDialog loading = new ProgressDialog(CorporateActivity.this);
        loading.setMessage("Loading pincode...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", "");
        jsonObject5.addProperty("sql", "select pin_code from PINCODE  where AREA = '" + part_IdArea + "' and ACTIVE = 'T'");
        array.put(jsonObject5);
        try {

            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);
            JsonArray array1 = new JsonArray();
            array1.add(jsonObject6);
            jsonObject7.add("_parameters", array1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Example> call4 = apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                loading.dismiss();

                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {

                        Pincodename = list.get(0).getResult().getRow().get(i).getPin_code();
                        // pincodeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNamePincode.add(Pincodename);
                        // PartIdPincode.add(pincodeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CorporateActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNamePincode);
                PartSpinnerPincode.setAdapter(adapter);
                PartSpinnerPincode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //  part_IdPincode = PartIdPincode.get(position);
                        part_NamePincode = PartNamePincode.get(position);
                        //  Toast.makeText(getApplicationContext(),part_IdPincode,Toast.LENGTH_SHORT);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorporateActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void postData() throws JSONException {
        final ProgressDialog loading = new ProgressDialog(CorporateActivity.this);
        loading.setMessage("Save Corporate...");
        loading.show();
        loading.setCancelable(false);
        // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiInterfaceSave = APIClientSave.getClient().create(APIInterfaceSave.class);
        JsonObject object = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonObject2 = new JsonObject();
        JsonArray array2 = new JsonArray();
        JsonArray array5 = new JsonArray();
        JsonArray array = new JsonArray();
        JsonObject jsonParams1 = new JsonObject();
        JsonObject jsonParams2 = new JsonObject();
        JsonObject jsonParams3 = new JsonObject();
        jsonParams1.addProperty("name", savecorpname);
        jsonParams1.addProperty("address", savecorpaddress);
        jsonParams1.addProperty("state", part_NameState);
        jsonParams1.addProperty("city", part_NameCity);
        jsonParams1.addProperty("area", part_NameArea);
        jsonParams1.addProperty("pincode", part_NamePincode);
        // jsonParams1.addProperty("active",statuscheckbox);
        jsonParams1.addProperty("latitude", latitude);
        jsonParams1.addProperty("longitude", longitude);
        jsonParams1.addProperty("img_board", "");
        jsonParams1.addProperty("emp_name", "");


        object.addProperty("rowno", "001");
        object.addProperty("text", "0");
        object.add("columns", jsonParams1);
        jsonParams2.addProperty("axpapp", "fieldforce");
        jsonParams2.addProperty("s", "");
        jsonParams2.addProperty("username", uname);
        jsonParams2.addProperty("password", pswd);

        jsonParams2.addProperty("transid", "corp1");
        jsonParams2.addProperty("recordid", "0");

        array5.add(object);
        jsonParams3.add("axp_recid1", array5);
        array.add(jsonParams3);
        jsonParams2.add("recdata", array);
        jsonObject.add("savedata", jsonParams2);
        array2.add(jsonObject);
        jsonObject2.add("_parameters", array2);


        // Enter the correct url for your api service site
        apiInterfaceSave = APIClientSave.getClient().create(APIInterfaceSave.class);
        Call<ExampleSave> call2 = apiInterfaceSave.getResult(jsonObject2);
        loading.dismiss();
        call2.enqueue(new Callback<ExampleSave>() {
            @Override
            public void onResponse(Call<ExampleSave> call, Response<ExampleSave> response) {
                listsave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getError() != null) {
                        message = listsave.get(0).getError().getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResult().get(0).getMessage() != null) {
                        message = listsave.get(0).getMessage().get(0).getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CorporateActivity.this, TabbedActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ExampleSave> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorporateActivity.this, "server problem!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity", "Status: " + status);
        if (status == false)
            Toast.makeText(CorporateActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }
}