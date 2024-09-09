package com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.getdata.APIClientPlanleave;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.getdata.Getcheckoutleave;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ResultSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.APIInterfaceSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.getdata.APIInterfacePlansleav;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.getdata.Result;
import com.kauveryhospital.fieldforce.R;

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

public class LeaveRequestActivity extends AppCompatActivity implements NetworkChangeCallback {
    DatePickerDialog mDatePicker;

    ImageView backarrow;
    TextView frmbtnDatePicker, tobtnDatePicker;
    Calendar upDateFroms;
    Calendar upDateFrom;
    String dayOfWeek, uname;
    private NetworkChangeReceiver networkChangeReceiver;
    ImageView fromdate, todate, imageNotification;
    EditText eaddress;
    String mtoDate, reason;
    List<ResultSave> listsave = new ArrayList<>();
    String mfrmDate, message, pswd, alterdates;
    APIInterfaceSave apiInterfaceSave;
    APIInterfacePlansleav apiInterfaceplanleav;
    int mDay;
    List<Result> listleave = new ArrayList<Result>();
    long mfrmDate1, tomindate;
    private int year, month, day, days;
    Button btn_fromdate, btn_todate, savebtn, cancelbtn;
    DatePickerDialog datePickerDialog;
    public static final String PREFS_NAME = "loginpref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);
        backarrow = findViewById(R.id.backarrow);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        frmbtnDatePicker = findViewById(R.id.frmbtnDatePicker);
        tobtnDatePicker = findViewById(R.id.tobtnDatePicker);
        eaddress = findViewById(R.id.eaddress);
        savebtn = findViewById(R.id.savebtn);
        cancelbtn = findViewById(R.id.cancelbtn);

        fromdate = findViewById(R.id.fromdate);
        todate = findViewById(R.id.todate);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveRequestActivity.this, TabbedActivity.class);
                startActivity(intent);
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
                mDatePicker = new DatePickerDialog(LeaveRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        upDateFrom = Calendar.getInstance();
                        upDateFroms = Calendar.getInstance();
                        year = selectedyear;
                        month = selectedmonth;
                        day = selectedday;
                        days = selectedday;
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
                        Retrievingdata();
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

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDates = Calendar.getInstance();
                year = mcurrentDates.get(Calendar.YEAR);
                month = mcurrentDates.get(Calendar.MONTH);
                day = mcurrentDates.get(Calendar.DAY_OF_MONTH);
                mcurrentDates.set(Calendar.DAY_OF_MONTH, mDay);
                mDatePicker = new DatePickerDialog(LeaveRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        upDateFrom = Calendar.getInstance();
                        upDateFroms = Calendar.getInstance();
                        year = selectedyear;
                        month = selectedmonth;
                        day = selectedday;
                        days = selectedday + 6;
                        upDateFrom.set(year, month, day);
                        upDateFroms.set(year, month, days);
                        mtoDate = convertDate(convertToMillis(day, month, year));


                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(selectedyear, selectedmonth, selectedday - 1);
                        dayOfWeek = simpledateformat.format(date);

                        tobtnDatePicker.setText(mtoDate);

                        int month_k = selectedmonth + 1;

                    }

                }, year, month, day);

                mDatePicker.setTitle("Please select date");
                // TODO Hide Future Date Here
                // mDatePicker.getDatePicker().setMaxDate(tomindate);

                // TODO Hide Past Date Here
                mDatePicker.getDatePicker().setMinDate(mfrmDate1);
                mDatePicker.show();

            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validation();

            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveRequestActivity.this, CancelRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validation() {

        if (isConnected()) {
            try {
                postData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
        }


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

    private void Retrievingdata() {
        final ProgressDialog loading = new ProgressDialog(LeaveRequestActivity.this);
        loading.setMessage("Checking Leave...");
        loading.setCancelable(false);
        loading.show();
        apiInterfaceplanleav = (APIInterfacePlansleav) APIClientPlanleave.getClient().create(APIInterfacePlansleav.class);
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JSONArray array = new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);

        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select count(*)cnt from la_leave where '" + alterdates + "' between fromdate and todate");
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

        Call<Getcheckoutleave> call4 = apiInterfaceplanleav.getResult(jsonObject7);
        call4.enqueue(new Callback<Getcheckoutleave>() {
            @Override
            public void onResponse(Call<Getcheckoutleave> call, Response<Getcheckoutleave> response) {
                loading.dismiss();

                listleave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getResult().getRow().get(0).getCnt().equals("0")) {

                        //  postData();
                        savebtn.setEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Leave Already Applied this date...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Getcheckoutleave> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Server Problem ,Please Wait...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postData() throws JSONException {
        savebtn.setEnabled(true);
        reason = eaddress.getText().toString();
        if (TextUtils.isEmpty(mfrmDate)) {
            frmbtnDatePicker.setError("Please Choose From Date");
            frmbtnDatePicker.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mtoDate)) {
            tobtnDatePicker.setError("Please Choose To Date");
            tobtnDatePicker.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(reason)) {
            eaddress.setError("Please enter reason", null);
            eaddress.requestFocus();
            return;
        }
        reason = eaddress.getText().toString();
        final ProgressDialog loading = new ProgressDialog(LeaveRequestActivity.this);
        loading.setMessage("Applying Leave...");
        loading.setCancelable(false);
        loading.show();
        // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiInterfaceSave = com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.APIClientSave.getClient().create(com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.APIInterfaceSave.class);
        JsonObject object = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonObject2 = new JsonObject();
        JsonArray array2 = new JsonArray();
        JsonArray array5 = new JsonArray();
        JsonArray array = new JsonArray();
        JsonObject jsonParams1 = new JsonObject();
        JsonObject jsonParams2 = new JsonObject();
        JsonObject jsonParams3 = new JsonObject();

        jsonParams1.addProperty("uname", uname);
        jsonParams1.addProperty("fromdate", mfrmDate);
        jsonParams1.addProperty("todate", mtoDate);
        jsonParams1.addProperty("reasonforleave", reason);
        jsonParams1.addProperty("remarks", "");
        jsonParams1.addProperty("status", "pending");
        jsonParams1.addProperty("employeeid", uname);
        // jsonParams1.addProperty("active",statuscheckbox);//

        object.addProperty("rowno", "001");
        object.addProperty("text", "0");
        object.add("columns", jsonParams1);
        jsonParams2.addProperty("axpapp", "fieldforce");
        jsonParams2.addProperty("s", "");
        jsonParams2.addProperty("username", uname);
        jsonParams2.addProperty("password", pswd);
        jsonParams2.addProperty("transid", "lve");
        jsonParams2.addProperty("recordid", "0");
        array5.add(object);
        jsonParams3.add("axp_recid1", array5);
        array.add(jsonParams3);
        jsonParams2.add("recdata", array);
        jsonObject.add("savedata", jsonParams2);
        array2.add(jsonObject);
        jsonObject2.add("_parameters", array2);
        // Enter the correct url for your api service site
        apiInterfaceSave = com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.APIClientSave.getClient().create(APIInterfaceSave.class);
        Call<com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ExampleSave> call2 = apiInterfaceSave.getResult(jsonObject2);
        call2.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ExampleSave>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ExampleSave> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ExampleSave> response) {
                loading.dismiss();
                listsave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getError() != null) {
                        message = listsave.get(0).getError().getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    else if (response.body().getResult().get(0).getMessage() != null) {
                        message = listsave.get(0).getMessage().get(0).getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LeaveRequestActivity.this, TabbedActivity.class));
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.savedata.ExampleSave> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(LeaveRequestActivity.this, "Server Problem", Toast.LENGTH_LONG).show();
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
            Toast.makeText(LeaveRequestActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
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
