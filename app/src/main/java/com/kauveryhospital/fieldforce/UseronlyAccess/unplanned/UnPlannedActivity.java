package com.kauveryhospital.fieldforce.UseronlyAccess.unplanned;

import androidx.appcompat.app.AppCompatActivity;

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
import com.kauveryhospital.fieldforce.Globaldeclare.APIClient;
import com.kauveryhospital.fieldforce.Globaldeclare.APIInterface;
import com.kauveryhospital.fieldforce.Globaldeclare.Example;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.TabbedActivity;
import  com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.APIInterfaceSave;
import  com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ResultSave;
import  com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.APIInterfacevisit;
import  com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.APIClientvisit;
import  com.kauveryhospital.fieldforce.Globaldeclare.Result;
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

public class UnPlannedActivity extends AppCompatActivity implements NetworkChangeCallback {
    ImageView backarrow;
    Spinner customer, PartSpinnerContacttype, PartSpinnerVisitpurpose;
    List<String> PartNameContacttype;
    List<String> PartIdContacttype;
    List<String> PartIdAddressType;
    APIInterfaceSave apiInterfaceSave;
    Button btnlogin;

    List<String> PartNameVisitpurpose;
    List<String> PartIdVisitpurpose;
    EditText name, jointcall, chevkintime, edlatitude, edlongitude, portfolio, specialization, addresss, Remarks;
    List<String> PartNameContactName;
    List<String> PartIdContactName;
    List<ResultSave> listsave = new ArrayList<>();
    public static final String PREFS_NAME = "loginpref";
    int j = 0;
    static final String PREFS_NAMES = "preferenceName";
    List<Result> listaddress = new ArrayList<>();
    List<Result> listcontname = new ArrayList<>();
    List<Result> list = new ArrayList<>();
    List<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Result> listvisit = new ArrayList<>();
    APIInterface apiInterface;
    APIInterfacevisit apiInterfacevisit;
    private NetworkChangeReceiver networkChangeReceiver;
    String statuscheckbox, settingaddress, settingcreatedon, currentDateTimeString, latitude, pswd, longitude, message, uname, uaddress, Statename, stateId, part_IdState, Cityname, cityId, part_IdCity, Areaname, areaId, part_IdArea, Pincodename, pincodeId, part_IdPincode, ContacttypeName, getaddressId, ContacttypeId, part_IdContacttype, SalutationName, SalutationId, VisitpurrposeName, createdon, ContactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_planned);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTimeString = formatter.format(todayDate);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        SharedPreferences settings = getSharedPreferences(PREFS_NAMES, 0);
        latitude = settings.getString("curlatitude", "");
        longitude = settings.getString("curlongitude", "");
        networkChangeReceiver = new NetworkChangeReceiver(this);
        btnlogin = findViewById(R.id.btnLogin);
        //  name = findViewById(R.id.name);
        jointcall = findViewById(R.id.jointcall);
        chevkintime = findViewById(R.id.chevkintime);
        edlatitude = findViewById(R.id.edlatitude);
        edlongitude = findViewById(R.id.edlongitude);
        portfolio = findViewById(R.id.portfolio);
        specialization = findViewById(R.id.specialization);
        addresss = findViewById(R.id.addresss);
        Remarks = findViewById(R.id.Remarks);
        customer = findViewById(R.id.customer);
        PartNameContactName = new ArrayList<>();
        PartIdContactName = new ArrayList<>();
        PartSpinnerContacttype = findViewById(R.id.contacttype);
        PartNameContacttype = new ArrayList<>();
        PartIdContacttype = new ArrayList<>();
        PartSpinnerVisitpurpose = findViewById(R.id.visitpurpose);
        PartNameVisitpurpose = new ArrayList<>();
        PartIdVisitpurpose = new ArrayList<>();
        //name.setText(uname);

        edlatitude.setText(latitude);
        edlongitude.setText(longitude);
        postdataContacttype();



        backarrow = findViewById(R.id.backarrow);
        //toolbar.setSubtitle("Sub");


        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnPlannedActivity.this, TabbedActivity.class);
                startActivity(intent);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    validation();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check the Internet Connection!!", Toast.LENGTH_SHORT).show();
                }
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
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity", "Status: " + status);
        if (status == false)
            Toast.makeText(UnPlannedActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
    }

    private void postdataContacttype() {
        list.clear();
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading ContactType...");
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
        jsonObject5.addProperty("sql", "select 'Doctor' dt from dual union all select 'Ambulance Driver' dt from dual union all select 'Others' dt from dual union all select 'Corporate Name' dt from dual order by dt");
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
                //    String jsonString = response.body().toString();
                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {

                        ContacttypeName = list.get(0).getResult().getRow().get(i).getDt();
                        //  ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameContacttype.add(ContacttypeName);
                        //  PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UnPlannedActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameContacttype);
                PartSpinnerContacttype.setAdapter(adapter);
                PartSpinnerContacttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ContacttypeName = PartNameContacttype.get(position);
                        listcontname.clear();
                        PartNameContactName.clear();
                        if (ContacttypeName.equals("Corporate Name")) {
                            getAddresses();
                        } else {

                            postdatacontactname();
                        }

                        // Toast.makeText(getApplicationContext(),part_IdPincode,Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void postdataVisitpurpose() {
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading Visit Purpose...");
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
        jsonObject5.addProperty("sql", "select visitpurpose from visit_purpose where active = 'T'");
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

        Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Example> call4 = apiInterfacevisit.getResult(jsonObject7);
        call4.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Example>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Example> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Example> response) {
                loading.dismiss();
//
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UnPlannedActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameVisitpurpose);
                PartSpinnerVisitpurpose.setAdapter(adapter);

                PartSpinnerVisitpurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        VisitpurrposeName = PartNameVisitpurpose.get(position);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(UnPlannedActivity.this, "Nothing is selected", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.retrievevisit.Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void postdatacontactname() {
        listcontname.clear();
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading ContactName...");
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
        jsonObject5.addProperty("sql", "select contactname,address,createdon from cont_corp where contype='" + ContacttypeName + "'");
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
                //   String jsonString = response.body().toString();
                listcontname = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {

                    for (int i = 0; i <= numbers; i++) {

                        ContactName = listcontname.get(0).getResult().getRow().get(i).getContactname();
                        //Log.d( "onResponsess: ",ContactName);
                        // getaddressId=listcontname.get(0).getResult().getRow().get(i).getAddress();
                        // createdon=list.get(0).getResult().getRow().get(i).getCreatedon();
                        // ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameContactName.add(ContactName);
                        // chevkintime.setText(createdon);
                        //  PartIdAddressType.add(getaddressId);
                        //  PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UnPlannedActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameContactName);
                customer.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ContactName = PartNameContactName.get(position);
                        gettingcontactname();
                        //   getaddressId=PartIdAddressType.get(position); //
                        //   Toast.makeText(getApplicationContext(),ContactName,Toast.LENGTH_SHORT); //
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // input=ContactName
    private void getAddresses() {
        listaddress.clear();
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading ContactName...");
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
        jsonObject5.addProperty("sql", "select address,createdon,name from corporate");
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
                //   String jsonString = response.body().toString();
                listaddress = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {

                    for (int i = 0; i <= numbers; i++) {

                        ContactName = listaddress.get(0).getResult().getRow().get(i).getName();
                        // getaddressId=list.get(0).getResult().getRow().get(i).getAddress();
                        //  createdon=list.get(0).getResult().getRow().get(i).getCreatedon();
                        //  ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameContactName.add(ContactName);
                        //   PartIdAddressType.add(getaddressId);
                        // chevkintime.setText(createdon);
                        //   PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UnPlannedActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameContactName);
                customer.setAdapter(adapter);
                customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ContactName = PartNameContactName.get(position);
                        gettingaddress();
                        // getaddressId=PartIdAddressType.get(position);
                        //  Toast.makeText(getApplicationContext(),getaddressId,Toast.LENGTH_SHORT);//
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gettingaddress() {
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading Address...");
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
        jsonObject5.addProperty("sql", "select address,createdon from corporate where name='" + ContactName + "'");
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
                //String jsonString = response.body().toString();
                listcontname = response.body().getResult();
                //  int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    settingaddress = listcontname.get(0).getResult().getRow().get(0).getAddress();
                    settingcreatedon = listcontname.get(0).getResult().getRow().get(0).getCreatedon();
                    addresss.setText(settingaddress);
                    chevkintime.setText(settingcreatedon);
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void gettingcontactname() {
        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Loading Address...");
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
        jsonObject5.addProperty("sql", "select address,createdon from cont_corp where contactname='" + ContactName + "'");
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
                //String jsonString = response.body().toString();
                listcontname = response.body().getResult();

                //  int numbers = response.body().getResult().get(j).getResult().getRow().size();//
                try {
                    settingaddress = listcontname.get(0).getResult().getRow().get(0).getAddress();
                    settingcreatedon = listcontname.get(0).getResult().getRow().get(0).getCreatedon();
                    addresss.setText(settingaddress);
                    chevkintime.setText(settingcreatedon);
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validation() {


//        if(VisitpurrposeName.equals("Select Visit purpose")){
//            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
//        }
        postdataVisitpurpose();
        statuscheckbox = "active";

        // savecorpaddress=etcorpaddress.getText().toString();//

        try {

            postData();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void postData() throws JSONException {
//      int selectedItemofmyspinner=PartSpinnerVisitpurpose.getSelectedItemPosition();
//      String actualposition=(String)PartSpinnerVisitpurpose.getItemAtPosition(selectedItemofmyspinner);
//      if(actualposition.isEmpty()){
//          setSpinnerError(PartSpinnerVisitpurpose,"error");
//      }

        final ProgressDialog loading = new ProgressDialog(UnPlannedActivity.this);
        loading.setMessage("Saving...");
        loading.show();
        // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiInterfaceSave = (APIInterfaceSave) com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.APIClientSave.getClient().create(com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.APIInterfaceSave.class);
        JsonObject object = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonObject2 = new JsonObject();
        JsonArray array2 = new JsonArray();
        JsonArray array5 = new JsonArray();
        JsonArray array = new JsonArray();
        JsonObject jsonParams1 = new JsonObject();
        JsonObject jsonParams2 = new JsonObject();
        JsonObject jsonParams3 = new JsonObject();
        jsonParams1.addProperty("contype", ContacttypeName);
        jsonParams1.addProperty("employee", uname);
        jsonParams1.addProperty("customer_name", ContactName);
        jsonParams1.addProperty("purpose", VisitpurrposeName);
        jsonParams1.addProperty("address", settingaddress);
        jsonParams1.addProperty("checkin", settingcreatedon);
        jsonParams1.addProperty("latitude",latitude);
        jsonParams1.addProperty("longitude",longitude);
        object.addProperty("rowno", "001");
        object.addProperty("text", "0");
        object.add("columns", jsonParams1);
        jsonParams2.addProperty("axpapp", "fieldforce");
        jsonParams2.addProperty("s", "");
        jsonParams2.addProperty("username", uname);
        jsonParams2.addProperty("password", pswd);
        jsonParams2.addProperty("transid", "uplan");
        jsonParams2.addProperty("recordid", "0");
        array5.add(object);
        jsonParams3.add("axp_recid1", array5);
        array.add(jsonParams3);
        jsonParams2.add("recdata", array);
        jsonObject.add("savedata", jsonParams2);
        array2.add(jsonObject);
        jsonObject2.add("_parameters", array2);
        // Enter the correct url for your api service site//

        apiInterfaceSave = (APIInterfaceSave) com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.APIClientSave.getClient().create(com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.APIInterfaceSave.class);
        Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ExampleSave> call2 = apiInterfaceSave.getResult(jsonObject2);
        call2.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ExampleSave>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ExampleSave> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ExampleSave> response) {
                loading.dismiss();
                listsave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getError() != null) {
                        message = listsave.get(0).getError().getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResult().get(0).getMessage() != null)
                    {
                        message = listsave.get(0).getMessage().get(0).getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UnPlannedActivity.this, TabbedActivity.class));
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.savedata.ExampleSave> call, Throwable t)
            {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "OOPS server problem... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerError(Spinner partSpinnerVisitpurpose, String s) {
        View selectedView=PartSpinnerVisitpurpose.getSelectedView();
        if(selectedView!=null && selectedView instanceof TextView){
            PartSpinnerVisitpurpose.requestFocus();
            TextView selectedTextView=(TextView)selectedView;
            selectedTextView.setError("error");
            PartSpinnerVisitpurpose.performClick();
        }
    }
}

