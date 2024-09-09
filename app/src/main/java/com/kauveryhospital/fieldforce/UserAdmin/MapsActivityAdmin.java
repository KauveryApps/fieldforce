package com.kauveryhospital.fieldforce.UserAdmin;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIClientadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIInterfaceadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Exampleadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Result_admin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Resultadmin;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivityAdmin extends FragmentActivity implements OnMapReadyCallback {

    public static final String PREFS_NAME = "loginpref";
    private GoogleMap mMap;
    ArrayAdapter<String> adapter;
    ImageView backarrow;
    Button choosedate;
    private DatePicker datePicker;
    Button empid;
    double lat, lng;
    String currentdate, Statename, stateId;
    private Calendar calendar;
    TextView selctdate;
    String latitude, longitude, uname, pswd, part_IdState, part_NameState;
    int j = 0;
    APIInterfaceadmin apiInterface;
    List<Resultadmin> list = new ArrayList<>();
    List<Result_admin> list1 = new ArrayList<>();
    List<LatLng> sourcePoints;
    Spinner PartSpinnerState;
    TextView dateView;
    int year, month, day;
    int i = 0;
    List<String> PartNameState;
    List<String> PartIdState;
    private int mYear, mMonth, mDay, mHour, mMinute, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_admin);
        backarrow = findViewById(R.id.backarrow);
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        uname = set.getString("username", "");
        pswd = set.getString("password", "");
        PartSpinnerState = (Spinner) findViewById(R.id.empids);
        PartNameState = new ArrayList<>();
        PartIdState = new ArrayList<>();
        postdatastate();
        selctdate = findViewById(R.id.selctdate);
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        currentdate = formatter.format(todayDate);

        choosedate = findViewById(R.id.choosedate);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        choosedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MapsActivityAdmin.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        choosedate.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                        currentdate = choosedate.getText().toString();
                        mMap.clear();
                        postdata();

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivityAdmin.this, TabbedActivity.class));
            }
        });
    }

    private void postdatastate() {
        final ProgressDialog loading = new ProgressDialog(MapsActivityAdmin.this);
        loading.setMessage("Loading Employee Id...");
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
        jsonObject5.addProperty("sql", "select username from axusers");
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
                loading.dismiss();
                //  String jsonString = response.body().toString();
                list = response.body().getResult();

                int numbers = response.body().getResult().get(j).getResult().getRow().size();
                try {
                    for (int i = 0; i <= numbers; i++) {
                        Statename = list.get(0).getResult().getRow().get(i).getUsername();
                        // stateId = list.get(0).getResult().getRow().get(i).getstateid();
                        PartNameState.add(Statename);
                        //  PartIdState.add(stateId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new ArrayAdapter<String>(MapsActivityAdmin.this, android.R.layout.simple_spinner_dropdown_item, PartNameState);
                PartSpinnerState.setAdapter(adapter);
                PartSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //  part_IdState= PartIdState.get(position);
                        part_NameState = PartNameState.get(position);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(MapsActivityAdmin.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    private void postdata() {
        final ProgressDialog loading = new ProgressDialog(MapsActivityAdmin.this);
        loading.setMessage("Loading Tracking...");
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
        jsonObject5.addProperty("sql", "select latitude,longitude,userid from location_track where userid='" + part_NameState + "' and location_date='" + currentdate + "'");
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
//                sourcePoints.clear();
                // String jsonString=response.body().toString();
                loading.dismiss();
                list = response.body().getResult();
                int numbers = response.body().getResult().get(j).getResult().getRow().size();

                try {
                    for (i = 0; i <= numbers; i++) {
                        latitude = list.get(0).getResult().getRow().get(i).getLatitude();
                        longitude = list.get(0).getResult().getRow().get(i).getLongitude();
                        lat = Double.parseDouble(latitude);
                        //    Toast.makeText(getApplicationContext(), (int) lat, Toast.LENGTH_SHORT).show();
                        lng = Double.parseDouble(longitude);
//
                        LatLng sydney = new LatLng(lat, lng);


                        mMap.addMarker(new MarkerOptions().position(sydney).title(""));

                        CameraPosition googlePlex = CameraPosition.builder()
                                .target(new LatLng(lat, lng))
                                .zoom(23)
                                .bearing(0)
                                .tilt(45)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2500, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(MapsActivityAdmin.this, "No Records Found", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void addCircleMarker(LatLng latLng) {
        Drawable circleDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_blue_circle);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable, 30, 30);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(markerIcon)

        );
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, int width, int height) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}