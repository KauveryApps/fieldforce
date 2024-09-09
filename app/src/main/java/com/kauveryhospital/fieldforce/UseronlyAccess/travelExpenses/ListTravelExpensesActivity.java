package com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.Globaldeclare.APIClient;
import com.kauveryhospital.fieldforce.Globaldeclare.APIInterface;
import com.kauveryhospital.fieldforce.Globaldeclare.Example;
import com.kauveryhospital.fieldforce.Globaldeclare.Result;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTravelExpensesActivity extends AppCompatActivity implements NetworkChangeCallback {
    RecyclerView cusappoint;
    ListTravelAdapter adapter;
    HashMap<String, String> map;
    APIInterface apiInterface;
    Calendar upDateFroms;
    TextView frmbtnDatePicker, tobtnDatePicker;
    Calendar upDateFrom;
    int mDay;
    List<String> PartNameState;
    String mfrmDate, message, pswd, alterdates;
    private int year, month, day, days;
    List<String> PartIdState;
    public static  final String PREFS_NAME="loginpref";
    int j=0;
    List<Result> list=new ArrayList<>();
    ArrayList<HashMap<String, String>> arraylist;
    Button btn_fromdate, btn_todate, savebtn, cancelbtn;
    long mfrmDate1, tomindate;
    ImageView backarrow;
    DatePickerDialog mDatePicker;
    ImageView fromdate;
    String empname,address,customer,area,pincode,visitdate,jsonString,checkin,dayOfWeek,uname;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_travel_expenses);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set=getSharedPreferences(PREFS_NAME,0);
        uname=set.getString("username","");
        pswd=set.getString("password","");
        backarrow=findViewById(R.id.backarrow);
        cusappoint=(RecyclerView) findViewById(R.id.recyclerView);
        frmbtnDatePicker = findViewById(R.id.frmbtnDatePicker);
        fromdate = findViewById(R.id.fromdate);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListTravelExpensesActivity.this, TabbedActivity.class));
            }
        });
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDates = Calendar.getInstance();
                year = mcurrentDates.get(Calendar.YEAR);
                month = mcurrentDates.get(Calendar.MONTH);
                day = mcurrentDates.get(Calendar.DAY_OF_MONTH);
                mcurrentDates.set(Calendar.DAY_OF_MONTH, mDay);
                mDatePicker = new DatePickerDialog(ListTravelExpensesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        upDateFrom = Calendar.getInstance();
                        upDateFroms = Calendar.getInstance();
                        year = selectedyear;
                        month = selectedmonth;
                        day = selectedday;
                        days = selectedday + 3;
                        upDateFrom.set(year, month, day);
                        upDateFroms.set(year, month, days);
                        mfrmDate = convertDate(convertToMillis(day, month, year));
                        alterdates = convertDate1(convertToMillis(day, month, year));
                        tomindate = convertToMillis(day, month, year);
                        mfrmDate1 = convertToMillis(days, month, year);
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(selectedyear, selectedmonth, selectedday - 1);
                        dayOfWeek = simpledateformat.format(date);
                        frmbtnDatePicker.setText(mfrmDate);
                        int month_k = selectedmonth + 1;
                        Log.d("TAG", alterdates);
                        if(isConnected()){
                            postdatastate();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
                        }


                        // Retrievingdata();
                    }

                }, year, month, day);

                mDatePicker.setTitle("Please select date");
                // TODO Hide Future Date Here
                //  mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

                // TODO Hide Past Date Here
                //     mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                mDatePicker.show();
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
    public long convertToMillis(int day, int month, int year) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Calendar.YEAR, year);
        calendarStart.set(Calendar.MONTH, month);
        calendarStart.set(Calendar.DAY_OF_MONTH, day);
        return calendarStart.getTimeInMillis();
    }

    public String convertDate(long mTime) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        String formattedDate = df.format(mTime);
        return formattedDate;

    }

    public String convertDate1(long mTime) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-YYYY");
        String formattedDate = df.format(mTime);
        return formattedDate;

    }
    private void postdatastate() {
        final ProgressDialog loading = new ProgressDialog(ListTravelExpensesActivity.this);
        loading.setMessage("Loading Corporate...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject jsonObject5=new JsonObject();
        JsonObject jsonObject6=new JsonObject();
        JsonObject jsonObject7=new JsonObject();
        JSONArray array=new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);
        jsonObject5.addProperty("s"," ");
        jsonObject5.addProperty("sql","select employee,address,purpose,visitdate,customer,checkin from unplanvisit where visitdate='"+alterdates+"' and employee='"+uname+"' and expense_status is NULL ");
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

        Call<Example> call4=apiInterface.getResult(jsonObject7);
        call4.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {


                list = response.body().getResult();
                arraylist = new ArrayList<HashMap<String, String>>();
                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <numbers; i++) {
                        empname = list.get(0).getResult().getRow().get(i).getEmployee();
                        visitdate=  list.get(0).getResult().getRow().get(i).getVisitdate();
                        address = list.get(0).getResult().getRow().get(i).getAddress();
                        customer= list.get(0).getResult().getRow().get(i).getCustomer();
                        checkin=list.get(0).getResult().getRow().get(i).getCheckin();
                        map = new HashMap<String, String>();
                        map.put("empname",empname);
                        map.put("visitdate",visitdate);
                        map.put("address",address);
                        map.put("purpose",customer);
                        map.put("checkin",checkin);
                        arraylist.add(map);
                    }
                    adapter = new ListTravelAdapter(ListTravelExpensesActivity.this, arraylist);
                    cusappoint.setLayoutManager(new LinearLayoutManager(ListTravelExpensesActivity.this));
                    cusappoint.setAdapter(adapter);
                    loading.dismiss();
                } catch (Exception e)
                { e.printStackTrace(); }

            }
            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ListTravelExpensesActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity","Status: " + status);
        if(status==false)
            Toast.makeText(ListTravelExpensesActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
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