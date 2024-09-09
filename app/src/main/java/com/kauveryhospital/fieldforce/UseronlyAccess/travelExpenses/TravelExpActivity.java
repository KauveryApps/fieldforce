package com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.CheckoutActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.getdata.APIInterfaceTrvPlan;
import com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.getdata.GetcheckoutTrv;
import com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.getdata.Result;
import com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.APIClientSavetrv;
import  com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.APIInterfaceSavetrv;
import  com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ResultSaves;
import  com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.getdata.APIClientTrvPlan;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelExpActivity extends Activity implements AdapterView.OnItemSelectedListener, NetworkChangeCallback {
    ImageView backarrow;
    APIInterfaceSavetrv apiInterfaceSavetrv;
    APIInterfaceTrvPlan apiInterfacetrvplan;
    List<ResultSaves> listsave = new ArrayList<>();
    private static final int pic_id = 123;

    Spinner spinner;
    Button btnsave;
    String item,uname,image,pswd,svaddress,svempname,svcustomer,svcheckin,svvisitdate,message,kilometers,svamount;
    TextView empname,txtaddress,txtcusname,txtvisitdate;
    public static final String PREFS_NAME = "loginpref";
    EditText edkilometers,edamount;
    private NetworkChangeReceiver networkChangeReceiver;
    List<Result> listups = new ArrayList<>();
    ImageView captured_image,open_camera;
    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_exp);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        captured_image=findViewById(R.id.closed_camera);
        open_camera=findViewById(R.id.closed_camera_open);
        edkilometers=findViewById(R.id.edkilometers);
        txtaddress=findViewById(R.id.txtaddress);
        btnsave=findViewById(R.id.btnsave);
        txtcusname=findViewById(R.id.txtcusname);
        txtvisitdate=findViewById(R.id.txtvisitdate);
        edamount=findViewById(R.id.edamount);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        backarrow=findViewById(R.id.backarrow);
        empname=findViewById(R.id.empname);

        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Bus");
        categories.add("Cab");
        categories.add("Auto");
        categories.add("Two-Wheeler");
        categories.add("Train");

        Bundle bundle = getIntent().getExtras();
        svempname = bundle.getString("empname");
        svvisitdate = bundle.getString("visitdate");
        svaddress = bundle.getString("address");
        svcustomer = bundle.getString("customer");
        svcheckin=bundle.getString("checkin");
        empname.setText(uname);
        txtaddress.setText(svaddress);
        txtcusname.setText(svcustomer);
        txtvisitdate.setText(svvisitdate);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    validation();
                }
                else {
                    Toast.makeText(getApplicationContext(),"check Internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelExpActivity.this, ListTravelExpensesActivity.class);
                startActivity(intent);
            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelExpActivity.this, TabbedActivity.class);
                startActivity(intent);
            }
        });
        captured_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });
    }
    private void validation() {


        if(edkilometers.getText().toString().trim().length()==0){
            edkilometers.setError("Please enter kilometers");
            edkilometers.requestFocus();
            return;
        }
        if(edamount.getText().toString().trim().length()==0){
            edamount.setError("Please enter Amount");
            edamount.requestFocus();
            return;
        }
        else{
            try {
                postData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
    public void postData() throws JSONException {
        kilometers=edkilometers.getText().toString();
        svamount=edamount.getText().toString();

        final ProgressDialog loading = new ProgressDialog(TravelExpActivity.this);
        loading.setMessage("Saving Travel Expenses...");
        loading.setCancelable(false);
        loading.show();
        // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiInterfaceSavetrv =  APIClientSavetrv.getClient().create(APIInterfaceSavetrv.class);
        JsonObject object = new JsonObject();


        JsonObject jsonObject = new JsonObject();

        JsonObject jsonObject2 = new JsonObject();

        JsonArray array2 = new JsonArray();

        JsonArray array5 = new JsonArray();
        JsonArray array = new JsonArray();

        JsonObject jsonParams1 = new JsonObject();
        JsonObject jsonParams2 = new JsonObject();
        JsonObject jsonParams3 = new JsonObject();


        jsonParams1.addProperty("employeeid", svempname);
        jsonParams1.addProperty("vechicle", item);
        jsonParams1.addProperty("kilometers", kilometers);
        jsonParams1.addProperty("applying_date", svvisitdate);
        jsonParams1.addProperty("contact_name", svcustomer);
        jsonParams1.addProperty("address", svaddress);
        jsonParams1.addProperty("expense",svamount);
        jsonParams1.addProperty("status","pending");




        object.addProperty("rowno", "001");
        object.addProperty("text", "0");
        object.add("columns", jsonParams1);


        jsonParams2.addProperty("axpapp", "fieldforce");
        jsonParams2.addProperty("s", "");
        jsonParams2.addProperty("username", uname);
        jsonParams2.addProperty("password", pswd);

        jsonParams2.addProperty("transid", "travl");
        jsonParams2.addProperty("recordid", "0");

        array5.add(object);
        jsonParams3.add("axp_recid1", array5);
        array.add(jsonParams3);
        jsonParams2.add("recdata", array);
        jsonObject.add("savedata", jsonParams2);

        array2.add(jsonObject);
        jsonObject2.add("_parameters", array2);


        // Enter the correct url for your api service site
        apiInterfaceSavetrv = (APIInterfaceSavetrv) APIClientSavetrv.getClient().create(APIInterfaceSavetrv.class);
        Call<com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ExampleSave> call2 = apiInterfaceSavetrv.getResult(jsonObject2);
        call2.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ExampleSave>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ExampleSave> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ExampleSave> response) {
                loading.dismiss();
                listsave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getError() != null) {
                        message = listsave.get(0).getError().getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResult().get(0).getMessage() != null) {
                        message = listsave.get(0).getMessage().get(0).getMsg();
                        UpdateCout();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(TravelExpActivity.this, TabbedActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.savesdata.ExampleSave> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(TravelExpActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity","Status: " + status);
        if(status==false)
            Toast.makeText(TravelExpActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
    }
    private void UpdateCout() throws JSONException {
        final ProgressDialog loading = new ProgressDialog(TravelExpActivity.this);
        loading.setMessage("Loading checkout...");
        loading.setCancelable(false);
        loading.show();
        apiInterfacetrvplan = APIClientTrvPlan.getClient().create(APIInterfaceTrvPlan.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);

        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "update unplanvisit SET  expense_status='pending' where checkin='" + svcheckin + "' and employee='"+uname+"'");

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

        Call<GetcheckoutTrv> call4 = apiInterfacetrvplan.getResult(jsonObject7);
        call4.enqueue(new Callback<GetcheckoutTrv>() {
            @Override
            public void onResponse(Call<GetcheckoutTrv> call, Response<GetcheckoutTrv> response) {
                loading.dismiss();

                listups = response.body().getResult();
                //numbers = response.body().getResult().get(j).getResult().getRow().size();//

                try {
                    if (response.body().getResult().get(0).getResult().getStatus() != null) {
                        message = listups.get(0).getResult().getStatus();
                        Toast.makeText(TravelExpActivity.this, "Updated " + message, Toast.LENGTH_SHORT).show();
                        // startActivity(new Intent(TravelExpActivity.this, MainActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetcheckoutTrv> call, Throwable t) {
loading.dismiss();
                Toast.makeText(TravelExpActivity.this, "Server problem!!!" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        captured_image.setImageBitmap(photo);
        open_camera.setVisibility(View.INVISIBLE);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG,64,byteArrayOutputStream);
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                displayMessage(getBaseContext(),"Unable to create directory.");
//                return;
//            }
//        }
        File destination = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".png");
        System.out.println("FILE LOCATION"+destination);
        image=destination.toString();
        FileOutputStream fo;

        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(byteArrayOutputStream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}