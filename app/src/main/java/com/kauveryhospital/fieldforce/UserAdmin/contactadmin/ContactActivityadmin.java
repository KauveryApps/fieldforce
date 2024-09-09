package com.kauveryhospital.fieldforce.UserAdmin.contactadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIClientadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIInterfaceadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Exampleadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Resultadmin;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivityadmin extends AppCompatActivity implements NetworkChangeCallback {
    RecyclerView cusappoint;
    ContactTaskAdapteradmin adapter;
    HashMap<String, String> map;
    APIInterfaceadmin apiInterface;
    public static  final String PREFS_NAME="loginpref";
    int j = 0;
    List<Resultadmin> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> arraylist;
    ImageView backarrow,loggedout;
    private NetworkChangeReceiver networkChangeReceiver;
    String address, uname,pswd,city_name, area, pincode, state_name, cont_corpid,contype, salutation, contactname, corporate, ambulance, specialization, portfolio, phone, active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_activityadmin);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set=getSharedPreferences(PREFS_NAME,0);
        uname=set.getString("username","");
        pswd=set.getString("password","");
        backarrow=findViewById(R.id.backarrow);

        cusappoint = (RecyclerView) findViewById(R.id.recyclerView);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactActivityadmin.this, TabbedActivity.class));
            }
        });
        if(isConnected()){
            postdatastate();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
        }

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
            Toast.makeText(ContactActivityadmin.this, "check Internet connection", Toast.LENGTH_LONG).show();
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
    private void postdatastate() {
        final ProgressDialog loading = new ProgressDialog(ContactActivityadmin.this);
        loading.setMessage("Loading ContactList...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClientadmin.getClient().create(APIInterfaceadmin.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);

        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select cont_corpid,contype,salutation,contactname,corporate,ambulance,specialization,portfolio,address,state,city,area,pincode,phone,active from cont_corp where ACTIVE = 'T'");
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

        Call<Exampleadmin> call4 = apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Exampleadmin>() {
            @Override
            public void onResponse(Call<Exampleadmin> call, Response<Exampleadmin> response) {


                list = response.body().getResult();
                arraylist = new ArrayList<HashMap<String, String>>();
                int numbers = response.body().getResult().get(j).getResult().getRow().size();

                try {
                    for (int i = 0; i < numbers; i++) {
                        contype = list.get(0).getResult().getRow().get(i).getContype();
                        salutation = list.get(0).getResult().getRow().get(i).getSalutation();
                        contactname = list.get(0).getResult().getRow().get(i).getContactname();
                        corporate = list.get(0).getResult().getRow().get(i).getCorporate();
                        ambulance = list.get(0).getResult().getRow().get(i).getAmbulance();
                        specialization = list.get(0).getResult().getRow().get(i).getSpecialization();
                        portfolio = list.get(0).getResult().getRow().get(i).getPortfolio();
                        phone = list.get(0).getResult().getRow().get(i).getPhone();
                        cont_corpid=list.get(0).getResult().getRow().get(i).getCont_corpid();
                        address = list.get(0).getResult().getRow().get(i).getAddress();
                        city_name = list.get(0).getResult().getRow().get(i).getCity_name();
                        area = list.get(0).getResult().getRow().get(i).getArea();
                        pincode = list.get(0).getResult().getRow().get(i).getPincode();
                        state_name = list.get(0).getResult().getRow().get(i).getstate_name();
                        map = new HashMap<String, String>();
                        map.put("contype", contype);
                        map.put("salutation", salutation);
                        map.put("contactname", contactname);
                        map.put("corporate", corporate);
                        map.put("ambulance", ambulance);
                        map.put("specialization", specialization);
                        map.put("portfolio", portfolio);
                        map.put("phone", phone);
                        map.put("cont_corpid",cont_corpid);
                        map.put("address", address);
                        map.put("city", city_name);
                        map.put("area", area);
                        map.put("pincode", pincode);
                        map.put("state", state_name);
                        arraylist.add(map);
                    }
                    adapter = new ContactTaskAdapteradmin(ContactActivityadmin.this, arraylist);
                    cusappoint.setLayoutManager(new LinearLayoutManager(ContactActivityadmin.this));
                    cusappoint.setAdapter(adapter);
                    loading.dismiss();
                }
                catch (Exception e) { e.printStackTrace(); }

            }

            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivityadmin.this, "server problem!!!", Toast.LENGTH_LONG).show();
            }
        });

    }
}