package com.kauveryhospital.fieldforce.workstartserviceuseronly;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.workstartserviceuseronly.getdata.APIClientPlans;
import com.kauveryhospital.fieldforce.workstartserviceuseronly.getdata.APIInterfacePlans;
import com.kauveryhospital.fieldforce.workstartserviceuseronly.getdata.Getcheckout;
import com.kauveryhospital.fieldforce.workstartserviceuseronly.getdata.Result;
import com.robertohuertas.endless.Actions;
import com.robertohuertas.endless.EndlessService;
import com.robertohuertas.endless.MainActivitys;
import com.robertohuertas.endless.ServiceState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.robertohuertas.endless.ServiceTrackerKt.getServiceState;
import static com.robertohuertas.endless.UtilsKt.log;

public class WorkStartActivity extends FragmentActivity implements OnMapReadyCallback , SharedPreferences.OnSharedPreferenceChangeListener{

    private GoogleMap mMap;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000;
    static WorkStartActivity instance;
    TextView txtLocationResult, txtUpdatedOn, txtview;
    APIInterface apiInterface;

    private String mLastUpdateTime;
    List<Result> listss=new ArrayList<com.kauveryhospital.fieldforce.workstartserviceuseronly.getdata.Result>();
    APIInterfacePlans apiInterfaceplan;
    private static final String PREFS_NAMES = "loginpref";
    String employeename, message, currentDateTimeString,uname,pswd;
    double lat, lng;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String TAG = WorkStartActivity.class.getSimpleName();

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    Button workstart, workstop, homebutton;
    private Boolean mRequestingLocationUpdates;
    MyBackgroundService mService = null;
    boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_start);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SharedPreferences settings1 = getSharedPreferences(PREFS_NAMES, 0);

        uname = settings1.getString("username", "");

        pswd=settings1.getString("password","");
        txtLocationResult = findViewById(R.id.location_result);
        txtview = findViewById(R.id.txtview);
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        currentDateTimeString = formatter.format(todayDate);
        Log.d(TAG, "onCreate: "+currentDateTimeString);
        homebutton = findViewById(R.id.homebutton);
        txtUpdatedOn = findViewById(R.id.updated_on);
        workstart = findViewById(R.id.workstarted);
        Checkingdata();
        // init();
        // restoreValuesFromBundle(savedInstanceState);
        // startLocationButtonClick();
        // showLastKnownLocation();
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        workstop = (Button) findViewById(R.id.workstop);
                        workstart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mService.requestLocationUpdates();
//                                workstart.setBackgroundColor(Color.GRAY);
//                                Utils.preventTwoClick(view);
                               // context.startForegroundService(it)

                            }
                        });
                        workstop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mService.removeLocationUpdates();
                            }
                        });
                        setButtonState(Common.requestingLocationUpdates(WorkStartActivity.this));
                        bindService(new Intent(WorkStartActivity.this, MyBackgroundService.class), mServiceConnection, Context.BIND_AUTO_CREATE); }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
        homebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkStartActivity.this, TabbedActivity.class));
            }
        });
    }

    private void Checkingdata()
    {
        final ProgressDialog loading = new ProgressDialog(WorkStartActivity.this);
        loading.setMessage("Loading Map...");
        loading.show();
        apiInterfaceplan = APIClientPlans.getClient().create(APIInterfacePlans.class);
        JsonObject jsonObject5=new JsonObject();
        JsonObject jsonObject6=new JsonObject();
        JsonObject jsonObject7=new JsonObject();
        JSONArray array=new JSONArray();
        jsonObject5.addProperty("axpapp", "fieldforce");
        jsonObject5.addProperty("username", uname);
        jsonObject5.addProperty("password", pswd);

        jsonObject5.addProperty("s"," ");
        jsonObject5.addProperty("sql","select count(*)cnt from location_track where location_date='"+currentDateTimeString+"' and userid='"+uname+"'");
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

        Call<Getcheckout> call4=apiInterfaceplan.getResult(jsonObject7);
        call4.enqueue(new Callback<Getcheckout>() {
            @Override
            public void onResponse(Call<Getcheckout> call, Response<Getcheckout> response) {
                loading.dismiss();

                listss = response.body().getResult();
                try {
                    if(response.body().getResult().get(0).getResult().getRow().get(0).getCnt().equals("0"))
                    {
                        workstart.setVisibility(View.VISIBLE);
                        workstart.setText("To Start Work");
                    }
                    else
                    {
                        workstart.setVisibility(View.VISIBLE);
                        workstart.setBackgroundColor(Color.GRAY);
                        workstart.setEnabled(false);
                        workstart.setText("Work Started Today");

                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Getcheckout> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server Problem ,Please Wait...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;

        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES, false));
        }
    }


    @SuppressLint("ResourceAsColor")
    private void setButtonState(boolean isRequestEnable) {
        if (isRequestEnable) {
            workstart.setEnabled(true);
            workstart.setBackgroundColor(Color.RED);
            //  workstop.setEnabled(true);
            //workstop.setBackgroundColor(Color.GREEN);
        } else {
            workstart.setEnabled(false);
            workstart.setBackgroundColor(Color.GRAY);
            // workstop.setEnabled(false);
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) {
        if (event != null) {
            String data = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            Toast.makeText(mService, data, Toast.LENGTH_SHORT).show();
            lat = event.getLocation().getLatitude();
            lng = event.getLocation().getLongitude();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera//
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Your location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));

    }
}