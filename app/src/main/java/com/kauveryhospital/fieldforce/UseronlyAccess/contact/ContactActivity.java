package com.kauveryhospital.fieldforce.UseronlyAccess.contact;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.APIInterfaceSave;
import com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ResultSave;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity implements NetworkChangeCallback {
    ImageView backarrow;
    private NetworkChangeReceiver networkChangeReceiver;
    APIInterfaceSave apiInterfaceSave;
    Spinner PartSpinnerState, PartSpinnerCity, PartSpinnerArea, PartSpinnerPincode, PartSpinnerContacttype, PartSpinnerSalutation, PartSpinnerCorporatename;
    public static final String PREFS_NAME = "loginpref";
    static final String PREFS_NAMES = "preferenceName";
    ImageView viewlist;
    List<String> PartNameState;
    List<String> PartIdState;

    List<String> PartNameCity;
    List<String> PartIdCity;

    List<String> PartNameArea;
    List<String> PartIdArea;

    List<String> PartNamePincode;
    List<String> PartIdPincode;

    List<String> PartNameContacttype;
    List<String> PartIdContacttype;

    List<String> PartNameSalutation;
    List<String> PartIdSalutation;

    List<String> PartNameCorporateName;
    List<String> PartIdCorporateName;
    CheckBox checkBox;
    Button btnSaveContact;
    List<Result> list = new ArrayList<>();
    int j = 0;
    APIInterface apiInterface;
    List<ResultSave> listsave = new ArrayList<>();
    EditText contactname, ambulancename, specialization, portfolio, regno, address, phoneno;
    String savecontactname, saveambulancename, savespecialization, saveportfolio, saveregno, saveaddress, savephoneno;
    String statuscheckbox, latitude, longitude, message, pswd, uname, Statename, stateId, part_IdState, part_NameState, Cityname, cityId, part_IdCity, part_NameCity, Areaname, areaId, part_IdArea, part_NameArea, Pincodename, pincodeId, part_IdPincode, part_NamePincode, ContacttypeName, ContacttypeId, part_IdContacttype, SalutationName, SalutationId, CorporateName, CorporateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        SharedPreferences settings = getSharedPreferences(PREFS_NAMES, 0);
        latitude = settings.getString("curlatitude", "");
        longitude = settings.getString("curlongitude", "");
        viewlist = findViewById(R.id.viewlist);
        PartSpinnerContacttype = findViewById(R.id.contacttype);
        btnSaveContact = findViewById(R.id.btnSaveContact);
        contactname = findViewById(R.id.contactname);
        ambulancename = findViewById(R.id.ambulancename);
        specialization = findViewById(R.id.specialization);
        portfolio = findViewById(R.id.portfolio);
        regno = findViewById(R.id.regno);
        address = findViewById(R.id.address);
        phoneno = findViewById(R.id.phoneno);

        PartNameContacttype = new ArrayList<>();
        PartIdContacttype = new ArrayList<>();
        postdataContacttype();

        checkBox = findViewById(R.id.checkBox);
        backarrow = findViewById(R.id.backarrow);

        PartSpinnerState = (Spinner) findViewById(R.id.state);
        PartNameState = new ArrayList<>();
        PartIdState = new ArrayList<>();
        postdatastate();
        PartSpinnerSalutation = (Spinner) findViewById(R.id.salutation);
        PartNameSalutation = new ArrayList<>();
        PartIdSalutation = new ArrayList<>();
        postdataSalutation();
        PartSpinnerCity = (Spinner) findViewById(R.id.city);
        PartNameCity = new ArrayList<>();
        PartIdCity = new ArrayList<>();

        PartSpinnerArea = (Spinner) findViewById(R.id.area);
        PartNameArea = new ArrayList<>();
        PartIdArea = new ArrayList<>();

        PartSpinnerPincode = (Spinner) findViewById(R.id.pincode);
        PartNamePincode = new ArrayList<>();
        PartIdPincode = new ArrayList<>();

        PartSpinnerCorporatename = findViewById(R.id.corporatename);
        PartNameCorporateName = new ArrayList<>();
        PartIdCorporateName = new ArrayList<>();
        postdataCorporatename();
        //toolbar.setSubtitle("Sub");

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, TabbedActivity.class);
                startActivity(intent);
            }
        });
        btnSaveContact.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(ContactActivity.this, ListContactActivity.class);
                startActivity(intent);
            }
        });

    }
    private void validation() {

        if(address.getText().toString().trim().length()==0){
            address.setError("Address is not entered");
            address.requestFocus();
            return;
        }
        else {
            savecontactname = contactname.getText().toString();
            saveambulancename = ambulancename.getText().toString();
            savespecialization = specialization.getText().toString();
            saveportfolio = portfolio.getText().toString();
            saveregno = regno.getText().toString();
            saveaddress = address.getText().toString();
            savephoneno = phoneno.getText().toString();
            try {
                postData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
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
        jsonObject5.addProperty("s", "");
        jsonObject5.addProperty("sql", "select state_name,stateid from STATE where ACTIVE = 'T'");
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameState);
                PartSpinnerState.setAdapter(adapter);
                PartSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        part_IdState = PartIdState.get(position);
                        part_NameState = PartNameState.get(position);
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
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdatacity() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameCity);
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
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("MainActivity", "Status: " + status);
        if (status == false)
            Toast.makeText(ContactActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
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

    private void postdataArea() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameArea);
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
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postdataPincode() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
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
        jsonObject5.addProperty("s", " ");
        jsonObject5.addProperty("sql", "select pin_code from PINCODE  where AREA = '" + part_IdArea + "' and ACTIVE = 'T'");
        array.put(jsonObject5);
        try {

            jsonObject6.add("getchoices", jsonObject5);
            // array.put(jsonObject6);//
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
                        pincodeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNamePincode.add(Pincodename);
                        PartIdPincode.add(pincodeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNamePincode);
                PartSpinnerPincode.setAdapter(adapter);
                PartSpinnerPincode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        part_IdPincode = PartIdPincode.get(position);
                        part_NamePincode = PartNamePincode.get(position);
                        Toast.makeText(getApplicationContext(), part_IdPincode, Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdataContacttype() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
        loading.setMessage("Loading ContactType...");
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

        jsonObject5.addProperty("sql", "select 'Doctor' dt from dual union all select 'Ambulance Driver' dt from dual union all select 'Others' dt from dual order by dt");
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
//
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameContacttype);
                PartSpinnerContacttype.setAdapter(adapter);
                PartSpinnerContacttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ContacttypeName = PartNameContacttype.get(position);
                        //   Toast.makeText(getApplicationContext(),part_IdPincode,Toast.LENGTH_SHORT);//
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdataSalutation() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
        loading.setMessage("Loading ContactType...");
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
        jsonObject5.addProperty("sql", "select 'Mr. ' title from dual union all select 'Mrs. ' title from dual union all select 'Ms. ' title from dual union all select 'Dr. ' title from dual union all select 'Prof. ' title from dual order by title");
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

                        SalutationName = list.get(0).getResult().getRow().get(i).getTitle();
                        //  ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameSalutation.add(SalutationName);
                        //  PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameSalutation);
                PartSpinnerSalutation.setAdapter(adapter);
                PartSpinnerSalutation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SalutationName = PartNameSalutation.get(position);
                        //   Toast.makeText(getApplicationContext(),part_IdPincode,Toast.);//
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void postdataCorporatename() {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
        loading.setMessage("Loading ContactType...");
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
        jsonObject5.addProperty("sql", "select a1.corporateid, a1.name from corporate a1 order by a1.name");
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

                        CorporateName = list.get(0).getResult().getRow().get(i).getName();
                        // ContacttypeId = list.get(0).getResult().getRow().get(i).getPincodeid();
                        PartNameCorporateName.add(CorporateName);
                        //  PartIdContacttype.add(ContacttypeId);
                    }
                } catch (Exception e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, PartNameCorporateName);
                PartSpinnerCorporatename.setAdapter(adapter);
                PartSpinnerCorporatename.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        CorporateName = PartNameCorporateName.get(position);
                        //   Toast.makeText(getApplicationContext(),part_IdPincode,Toast.);//
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivity.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }
    private  void getdatas(){

    }

    public void postData() throws JSONException {
        final ProgressDialog loading = new ProgressDialog(ContactActivity.this);
        loading.setMessage("Saving Contact...");
        loading.setCancelable(false);
        loading.show();
        // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiInterfaceSave = (APIInterfaceSave) com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.APIClientSave.getClient().create(com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.APIInterfaceSave.class);
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
        jsonParams1.addProperty("salutation", SalutationName);
        jsonParams1.addProperty("contactname", savecontactname);
        jsonParams1.addProperty("corporate", CorporateName);
        jsonParams1.addProperty("ambulance", saveambulancename);
        jsonParams1.addProperty("specialization", savespecialization);
        jsonParams1.addProperty("portfolio", saveportfolio);
        jsonParams1.addProperty("regno", saveregno);
        jsonParams1.addProperty("address", saveaddress);
        jsonParams1.addProperty("state", part_NameState);
        jsonParams1.addProperty("city", part_NameCity);
        jsonParams1.addProperty("area", part_NameArea);
        jsonParams1.addProperty("pincode", part_NamePincode);
        jsonParams1.addProperty("active", statuscheckbox);
        jsonParams1.addProperty("latitude", latitude);
        jsonParams1.addProperty("longitude", longitude);
        jsonParams1.addProperty("img_contact", "");
        jsonParams1.addProperty("emp_name", "");

        object.addProperty("rowno", "001");
        object.addProperty("text", "0");
        object.add("columns", jsonParams1);


        jsonParams2.addProperty("axpapp", "fieldforce");
        jsonParams2.addProperty("s", "");
        jsonParams2.addProperty("username", uname);
        jsonParams2.addProperty("password", pswd);

        jsonParams2.addProperty("transid", "contc");
        jsonParams2.addProperty("recordid", "0");

        array5.add(object);
        jsonParams3.add("axp_recid1", array5);
        array.add(jsonParams3);
        jsonParams2.add("recdata", array);
        jsonObject.add("savedata", jsonParams2);

        array2.add(jsonObject);
        jsonObject2.add("_parameters", array2);


        // Enter the correct url for your api service site
        apiInterfaceSave = (APIInterfaceSave) com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.APIClientSave.getClient().create(com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.APIInterfaceSave.class);
        Call<com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ExampleSave> call2 = apiInterfaceSave.getResult(jsonObject2);
        call2.enqueue(new Callback<com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ExampleSave>() {
            @Override
            public void onResponse(Call<com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ExampleSave> call, Response<com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ExampleSave> response) {
                loading.dismiss();
                listsave = response.body().getResult();
                try {
                    if (response.body().getResult().get(0).getError() != null) {
                        message = listsave.get(0).getError().getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResult().get(0).getMessage() != null) {
                        message = listsave.get(0).getMessage().get(0).getMsg();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ContactActivity.this, TabbedActivity.class));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<com.kauveryhospital.fieldforce.UseronlyAccess.contact.savedata.ExampleSave> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ContactActivity.this, "Server problem !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

}