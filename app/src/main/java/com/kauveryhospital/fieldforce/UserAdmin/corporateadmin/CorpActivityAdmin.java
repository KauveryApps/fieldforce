package com.kauveryhospital.fieldforce.UserAdmin.corporateadmin;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class CorpActivityAdmin extends AppCompatActivity implements NetworkChangeCallback {
    RecyclerView cusappoint;

    private RecyclerView.LayoutManager mLayoutManager;
    HashMap<String, String> map;
    APIInterfaceadmin apiInterface;
    List<String> PartNameState;
    List<String> PartIdState;
    public static  final String PREFS_NAME="loginpref";
    int j=0;
    List<Resultadmin> list=new ArrayList<>();
    ArrayList<HashMap<String, String>> arraylist;
    ImageView backarrow;
    private ArrayList<ExampleItem> mExampleList;
    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
EditText edittextsrc;
    private NetworkChangeReceiver networkChangeReceiver;
    String name,address,city,area,pincode,state,jsonString,uname,pswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        mRecyclerView = findViewById(R.id.recyclerViewtxt);
        backarrow=findViewById(R.id.backarrow);
        edittextsrc=findViewById(R.id.edittextsrc);
        SharedPreferences set=getSharedPreferences(PREFS_NAME,0);


        uname=set.getString("username","");
        pswd=set.getString("password","");
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CorpActivityAdmin.this, TabbedActivity.class));
            }
        });
        if(isConnected()){
            postdatastate();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
        }




        edittextsrc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }
    private void filter(String text) {
        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (ExampleItem item : mExampleList) {
            if (item.getmName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            if(item.getmCity().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            if(item.getmAddress().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            if(item.getmState().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            if(item.getmPincode().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
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
        final ProgressDialog loading = new ProgressDialog(CorpActivityAdmin.this);
        loading.setMessage("Loading Corporate...");
        loading.setCancelable(false);
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
        jsonObject5.addProperty("sql","select state_name,address,city_name,pincode,a.name from CORPORATE a join STATE b on b.stateid = a.state join CITY c on c.CITYID = a.CITY where a.ACTIVE= 'T' order by a.NAME");
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
                mExampleList = new ArrayList<>();
                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <numbers; i++)
                    {
                        name = list.get(0).getResult().getRow().get(i).getName();
                        state=  list.get(0).getResult().getRow().get(i).getstate_name();
                        address = list.get(0).getResult().getRow().get(i).getAddress();
                        city= list.get(0).getResult().getRow().get(i).getCity_name();
                        pincode= list.get(0).getResult().getRow().get(i).getPincode();
                      //  map = new HashMap<String, String>();
                      //  map.put("name",name);
                        // map.put("state",state);
                       // map.put("city",city);
                      //  map.put("address",address);
                      //  map.put("pincode",pincode);
                     //   arraylist.add(map);
                        mExampleList.add(new ExampleItem(name, state,address,city,pincode));
                    }
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mAdapter = new ExampleAdapter(mExampleList);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                   // buildRecyclerView();
                  //adapter = new ListCorporateAdapteradmin(MainActivityTest.this, arraylist);
                   // cusappoint.setLayoutManager(new LinearLayoutManager(MainActivityTest.this));
                  //  cusappoint.setAdapter(adapter);
                    loading.dismiss();
                } catch (Exception e)
                {
                    e.printStackTrace(); }

            }
            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CorpActivityAdmin.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity","Status: " + status);
        if(status==false)
            Toast.makeText(CorpActivityAdmin.this, "check Internet connection", Toast.LENGTH_LONG).show();
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