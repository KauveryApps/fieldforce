package com.kauveryhospital.fieldforce.UserAdmin.unplanvisitAdmin;

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
import com.kauveryhospital.fieldforce.SharedPrefManager;
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

public class unplannedvisit extends AppCompatActivity implements NetworkChangeCallback {
    RecyclerView cusappoint;
    ListunplanAdapter adapter;
    HashMap<String, String> map;
    APIInterfaceadmin apiInterface;
    List<String> PartNameState;
    List<String> PartIdState;
    public static  final String PREFS_NAME="loginpref";
    int j=0;
    List<Resultadmin> list=new ArrayList<>();
    ArrayList<HashMap<String, String>> arraylist;
    ImageView backarrow,loggedout;
    String employee,address,checkin,area,checkout,visitdate,jsonString,uname,pswd;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unplannedvisit);
        loggedout=findViewById(R.id.loggedout);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set=getSharedPreferences(PREFS_NAME,0);


        uname=set.getString("username","");
        pswd=set.getString("password","");
        backarrow=findViewById(R.id.backarrow);
        loggedout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });
        cusappoint=(RecyclerView) findViewById(R.id.recyclerView);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(unplannedvisit.this, TabbedActivity.class));
            }
        });
        if(isConnected()){
            postdatastate();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
        }

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
        final ProgressDialog loading = new ProgressDialog(unplannedvisit.this);
        loading.setMessage("Loading Corporate...");
        loading.show();
        apiInterface = APIClientadmin.getClient().create(APIInterfaceadmin.class);
        JsonObject jsonObject5=new JsonObject();
        JsonObject jsonObject6=new JsonObject();
        JsonObject jsonObject7=new JsonObject();
        JSONArray array=new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s"," ");
        jsonObject5.addProperty("sql","select employee,address,visitdate,checkin,checkouttime from unplanvisit");
        array.put(jsonObject5);
        try {

            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);
            JsonArray array1=new JsonArray();
            array1.add(jsonObject6);
            jsonObject7.add("_parameters",array1);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Call<Exampleadmin> call4=apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Exampleadmin>() {
            @Override
            public void onResponse(Call<Exampleadmin> call, Response<Exampleadmin> response) {
                loading.dismiss();

                list = response.body().getResult();
                arraylist = new ArrayList<HashMap<String, String>>();
                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <numbers; i++) {
                        employee = list.get(0).getResult().getRow().get(i).getEmployee();

                        address = list.get(0).getResult().getRow().get(i).getAddress();
                        visitdate=  list.get(0).getResult().getRow().get(i).getVisitdate();
                        checkin= list.get(0).getResult().getRow().get(i).getCheckin();
                        checkout= list.get(0).getResult().getRow().get(i).getCheckouttime();
                        map = new HashMap<String, String>();
                        map.put("employee",employee);
                        map.put("address",address);
                        map.put("visitdate",visitdate);
                        map.put("checkin",checkin);

                        map.put("checkout",checkout);
                        arraylist.add(map);
                    }
                    adapter = new ListunplanAdapter(unplannedvisit.this, arraylist);
                    cusappoint.setLayoutManager(new LinearLayoutManager(unplannedvisit.this));
                    cusappoint.setAdapter(adapter);
                    loading.dismiss();
                } catch (Exception e)
                {
                    e.printStackTrace(); }

            }
            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(unplannedvisit.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity","Status: " + status);
        if(status==false)
            Toast.makeText(unplannedvisit.this, "check Internet connection", Toast.LENGTH_LONG).show();
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