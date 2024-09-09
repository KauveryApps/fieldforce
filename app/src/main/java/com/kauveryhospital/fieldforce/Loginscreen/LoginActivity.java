package com.kauveryhospital.fieldforce.Loginscreen;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kauveryhospital.fieldforce.NetworkChangeCallback;
import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.SharedPrefManager;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.User;
import com.robertohuertas.endless.Actions;
import com.robertohuertas.endless.EndlessService;
import com.robertohuertas.endless.MainActivitys;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements NetworkChangeCallback {
    Button btnLogin;
    APIInterface apiInterface;
    TextView frgtpswd;
    boolean status;
    EditText etUserName,etpassword;
    List<Result> list=new ArrayList<Result>();
    String username, usrname,password,MD5_Hash_String,message,is_status;
    public static  final String PREFS_NAME="loginpref";

    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        frgtpswd=findViewById(R.id.frgtpswd);
       if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
          startActivity(new Intent(this, TabbedActivity.class));
        }


        btnLogin=findViewById(R.id.btnLogin);
        etUserName=findViewById(R.id.etUserName);
        etpassword=findViewById(R.id.etpassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                if(isConnected()) {
                    validation();
                }
                else {
                    Toast.makeText(getApplicationContext(),"check Internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        frgtpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Contact To Admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validation() {
        username=etUserName.getText().toString();
        password=etpassword.getText().toString();
        MD5_Hash_String = md5(password);
        if(etUserName.getText().toString().trim().length()==0){
            etUserName.setError("Please enter username");
            etUserName.requestFocus();
            return;
        }
        if(etpassword.getText().toString().trim().length()==0){
            etpassword.setError("Please enter password");
            etpassword.requestFocus();
            return;
        }

        else

            user();


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
            Toast.makeText(LoginActivity.this, "check Internet connection", Toast.LENGTH_LONG).show();
    }
    public void user(){
        //for storing encrypt char//
        final ProgressDialog loading = new ProgressDialog(LoginActivity.this);
        loading.setMessage("Loading Login...");
        loading.setCancelable(false);
        loading.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        OkHttpClient httpClient = new OkHttpClient();
        JsonObject jsonObject6=new JsonObject();
        JsonObject jsonObject7=new JsonObject();
        JSONArray array=new JSONArray();

        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("axpapp", "fieldforce");
        jsonParams.addProperty("username", username);
        jsonParams.addProperty("password", MD5_Hash_String);
        jsonParams.addProperty("other", "Chrome");
        array.put(jsonParams);
        try {//        jsonParams.put("parameters",array.put(jsonParams).toString());
            jsonObject6.add("login", jsonParams);

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
                loading.dismiss();
                String jsonString=response.body().toString();
                Log.d("response",jsonString);
                list = response.body().getResult();
                if(response.body().getResult().get(0).getError()!=null)
                {
                    message=list.get(0).getError().getMsg();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else {
                    usrname = list.get(0).getResult().getUSERNAME();
                    message=list.get(0).getResult().getStatus();
                    is_status=list.get(0).getResult().getIs_admin();
                    User user = new User(usrname,MD5_Hash_String,is_status);

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", usrname);
                        editor.putString("password",MD5_Hash_String);
                        editor.putString("uaa",is_status);
                        editor.apply();
                        //starting the profile activity
                        finish();
                        Toast.makeText(getApplicationContext(),"Login "+ message, Toast.LENGTH_SHORT).show();
                    MainActivitys.instance.actionOnService(Actions.START);
                        startActivity(new Intent(LoginActivity.this, TabbedActivity.class));


                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("====" , "Something gone wrong");
            }
        });
    }

    public String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException ex){
        }
        return null;
    }

    public boolean isConnected()
    {
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

}