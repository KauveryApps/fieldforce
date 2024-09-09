package com.kauveryhospital.fieldforce.UseronlyAccess.planned;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.getdata.APIClientPlan;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.getdata.APIInterfacePlan;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.getdata.Getcheckout;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.getdata.Result;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.APIClientvisit;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.APIInterfacevisit;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements NetworkChangeCallback {
    APIInterfacePlan apiInterfaceplan;
    int j = 0;
    APIInterfacevisit apiInterfacevisit;


    List<String> PartNameVisitpurpose;
    List<String> PartIdVisitpurpose;
    List<Result> list = new ArrayList<Result>();
    List<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Result>listvisit=new ArrayList<>();
    static final String PREFS_NAMES = "preferenceName";

    List<Result> listsaves = new ArrayList<>();
    List<Result>listsupdate=new ArrayList<>();
    public static final String PREFS_NAME = "loginpref";
    ImageView backarrow;
    private NetworkChangeReceiver networkChangeReceiver;
    Button btnLogin,btnLogina;
    EditText input;
    List<com.kauveryhospital.fieldforce.Globaldeclare.Result> lists = new ArrayList<>();
    Spinner PartSpinnerVisitpurpose;
    TextView getemployee, getcustomer;
    String GetEmployeename, uname,latitude,longitude, pswd,inputval, currentDateTimeString, Getvisitpurpose, Getunplanvisitid, Getcheckin, Getcheckouttime, VisitpurrposeName, GetCustomer, message;
    TextView unplanvisitid, checkintime, checkout_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        btnLogina=findViewById(R.id.btnLogina);
        backarrow = findViewById(R.id.backarrow);
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        currentDateTimeString = formatter.format(todayDate);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        getemployee = findViewById(R.id.getemployee);
        getcustomer = findViewById(R.id.getcustomer);
        unplanvisitid = findViewById(R.id.unplanvisitid);
        checkintime = findViewById(R.id.checkintime);
        checkout_time = findViewById(R.id.checkout_time);
        btnLogin = findViewById(R.id.btnLogin);
        PartSpinnerVisitpurpose = findViewById(R.id.visitpurpose);
        PartNameVisitpurpose = new ArrayList<>();
        PartIdVisitpurpose = new ArrayList<>();
        SharedPreferences settings = getSharedPreferences(PREFS_NAMES, 0);
        latitude = settings.getString("curlatitude", "");
        longitude = settings.getString("curlongitude", "");
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckoutActivity.this, TabbedActivity.class));
            }
        });

        postdatacontactname();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {
                    try {
                        UpdateCout();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check Internet connection", Toast.LENGTH_SHORT);
                }
            }
        });
        btnLogina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GetEmployeename.trim().length()==0){
                    Toast.makeText(CheckoutActivity.this, "No employee to checkout early" , Toast.LENGTH_SHORT).show();
                }
                else{
                    withEditText(v);

                }
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


    private void postdatacontactname() {
        final ProgressDialog loading = new ProgressDialog(CheckoutActivity.this);
        loading.setMessage("Loading checkout...");
        loading.setCancelable(false);
        loading.show();
        apiInterfaceplan = APIClientPlan.getClient().create(APIInterfacePlan.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select count(*)cnt,visit_purpose,customer,employee,unplanvisitid,checkin,checkouttime from unplanvisit where employee = '" + uname + "' and checkouttime is null group by visit_purpose,employee,unplanvisitid,checkin,checkouttime,customer");
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

        Call<Getcheckout> call4 = apiInterfaceplan.getResult(jsonObject7);
        call4.enqueue(new Callback<Getcheckout>() {
            @Override
            public void onResponse(Call<Getcheckout> call, Response<Getcheckout> response) {
                loading.dismiss();

                list = response.body().getResult();
                //numbers = response.body().getResult().get(j).getResult().getRow().size();//

                try {
                    if (response.body().getResult().get(0).getResult().getRow().get(0).getCnt().equals("0")) {

                    } else if (response.body().getResult().get(0).getResult().getRow().get(0).getCnt().equals("1")) {
                        getemployee.clearFocus();
                        unplanvisitid.clearFocus();
                        checkintime.clearFocus();
                        checkout_time.clearFocus();
                        getcustomer.clearFocus();
                        GetEmployeename = list.get(0).getResult().getRow().get(0).getEmployee();
                        Getunplanvisitid = list.get(0).getResult().getRow().get(0).getUnplanvisitid();
                        Getvisitpurpose = list.get(0).getResult().getRow().get(0).getVisitPurpose();
                        Getcheckin = list.get(0).getResult().getRow().get(0).getCheckin();
                        Getcheckouttime = list.get(0).getResult().getRow().get(0).getCheckouttime();
                        GetCustomer = list.get(0).getResult().getRow().get(0).getCustomer();

                        getemployee.setText(GetEmployeename);
                        unplanvisitid.setText(Getunplanvisitid);
                        checkintime.setText(Getcheckin);
                        checkout_time.setText(Getcheckouttime);
                        getcustomer.setText(GetCustomer);
                        postdataVisitpurpose();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Getcheckout> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CheckoutActivity.this, "No Records Found" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postdataVisitpurpose() {
        PartNameVisitpurpose.clear();
        PartNameVisitpurpose.add(Getvisitpurpose);
        final ProgressDialog loading = new ProgressDialog(CheckoutActivity.this);
        loading.setMessage("Loading Visit Purpose...");
        loading.setCancelable(false);
        loading.show();
        apiInterfacevisit = APIClientvisit.getClient().create(APIInterfacevisit.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select visit_purposeid,visitpurpose from visit_purpose where active = 'T'");
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

        Call<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Example> call4 = apiInterfacevisit.getResult(jsonObject7);
        call4.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Example>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Example> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Example> response) {
                loading.dismiss();

                listvisit = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {

                        VisitpurrposeName = listvisit.get(0).getResult().getRow().get(i).getVisitpurpose();
                        //  ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameVisitpurpose.add(VisitpurrposeName);
                        //  PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckoutActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameVisitpurpose);
                PartSpinnerVisitpurpose.setAdapter(adapter);

                PartSpinnerVisitpurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        VisitpurrposeName = PartNameVisitpurpose.get(position);

                        //   Toast.makeText(getApplicationContext(),part_IdPincode,Toast.LENGTH_SHORT);//
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.planned.retrievevisit.Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UpdateCout() throws JSONException {
        final ProgressDialog loading = new ProgressDialog(CheckoutActivity.this);
        loading.setMessage("Loading checkout...");
        loading.setCancelable(false);
        loading.show();
        apiInterfaceplan = APIClientPlan.getClient().create(APIInterfacePlan.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);

        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "update unplanvisit SET  checkouttime='" + currentDateTimeString + "',c_latitude='"+latitude+"',c_longitude='"+longitude+"' where unplanvisitid='" + Getunplanvisitid + "'");

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

        Call<Getcheckout> call4 = apiInterfaceplan.getResult(jsonObject7);
        call4.enqueue(new Callback<Getcheckout>() {
            @Override
            public void onResponse(Call<Getcheckout> call, Response<Getcheckout> response) {
                loading.dismiss();

                listsaves = response.body().getResult();
                //numbers = response.body().getResult().get(j).getResult().getRow().size();//

                try {
                    if (response.body().getResult().get(0).getResult().getStatus() != null) {
                        message = listsaves.get(0).getResult().getStatus();
                        Toast.makeText(CheckoutActivity.this, "Checkout" + message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckoutActivity.this, TabbedActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Getcheckout> call, Throwable t) {
            loading.dismiss();
                Toast.makeText(CheckoutActivity.this, "server problem!!!", Toast.LENGTH_SHORT).show();
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
        Log.e("MainActivity", "Status: " + status);
        if (status == false)
            Toast.makeText(CheckoutActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
    }
    public void withEditText(View v) {

        AlertDialog.Builder builders = new AlertDialog.Builder(CheckoutActivity.this);
        builders.setTitle("Reason for Early checkout");
        input = new EditText(CheckoutActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builders.setView(input);
        builders.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputval=input.getText().toString();
                if (!inputval.isEmpty()) {

                    try {
                        UpdateEarlycheckout();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CheckoutActivity.this, "Reason for Earlycheckout cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builders.show();
    }

    private  void UpdateEarlycheckout()throws JSONException{
        final ProgressDialog loading = new ProgressDialog(CheckoutActivity.this);
        loading.setMessage("Loading checkout...");
        loading.setCancelable(false);
        loading.show();
        apiInterfaceplan = APIClientPlan.getClient().create(APIInterfacePlan.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s", "");
        jsonObject5.addProperty("sql", "update unplanvisit SET  remarks='"+inputval+"',checkouttime='" + currentDateTimeString + "',c_latitude='"+latitude+"',c_longitude='"+longitude+"' where employee='" + GetEmployeename + "' and unplanvisitid='" + Getunplanvisitid + "'");

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

        Call<Getcheckout> call4 = apiInterfaceplan.getResult(jsonObject7);
        call4.enqueue(new Callback<Getcheckout>() {
            @Override
            public void onResponse(Call<Getcheckout> call, Response<Getcheckout> response) {
                loading.dismiss();

                listsupdate = response.body().getResult();
                //numbers = response.body().getResult().get(j).getResult().getRow().size();//

                try {
                    if (response.body().getResult().get(0).getResult().getStatus() != null) {
                        message = listsupdate.get(0).getResult().getStatus();
                        Toast.makeText(CheckoutActivity.this, "Early Checkout" + message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckoutActivity.this, TabbedActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Getcheckout> call, Throwable t) {
             loading.dismiss();
                Toast.makeText(CheckoutActivity.this, "Server problem!!!", Toast.LENGTH_SHORT).show();

            }
        });

    }

}