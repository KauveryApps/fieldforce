package com.kauveryhospital.fieldforce.UserAdmin.LeaveApprovedAdmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.TabbedActivity;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIClientadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.APIInterfaceadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Exampleadmin;
import com.kauveryhospital.fieldforce.UserAdmin.Globaldeclareadmin.Resultadmin;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CancelRequestAdapterAdmin extends RecyclerView.Adapter<CancelRequestAdapterAdmin.MyPendingHolder> implements View.OnClickListener {
    Context context;
    EditText input;
    APIInterfaceadmin apiInterface;
    String message,inputval,statusresult;

    int position;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<>();
    String uname, pswd,name,address,city,fromdate,la_leaveids,pincode,cancelremarks;
    List<Resultadmin> list = new ArrayList<Resultadmin>();
    ArrayAdapter<String> dataAdapter;

    public CancelRequestAdapterAdmin(LeaveApprovingActivityAdmin context, ArrayList<HashMap<String, String>> arraylist, String uname, String pswd) {
        this.context = context;
        data = arraylist;
        this.uname = uname;
        this.pswd = pswd;

    }


    //    private final OnClickListener mOnClickListener = new MyOnClickListener();
    @NonNull
    @Override
    public MyPendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cancel_activity_tasksadmin, parent, false);
        v.setOnClickListener(this);
        MyPendingHolder myHolder = new MyPendingHolder(v);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPendingHolder holder, final int position) {
        resultp = data.get(position);
        holder.name.setText(resultp.get("name"));
        holder.fromdate.setText(resultp.get("fromdate"));
        holder.address.setText(resultp.get("address"));
        holder.city.setText(resultp.get("city"));
        holder.pincode.setText(resultp.get("pincode"));
        holder.ones.setText(resultp.get("la_leaveid"));

        holder.reasoncancelbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                address=data.get(position).get("address");
                cancelremarks=data.get(position).get("cancelremarks");
                la_leaveids=data.get(position).get("la_leaveid");
              //  withEditText(v);
                if(address.equals("pending"))
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("");
                    builder.setIcon(R.mipmap.kauveryslogos);
                    builder.setMessage("Are You Approved the Leave?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    statusresult="Approved";

                                    try {
                                        postdata();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    statusresult="Cancelled";

                                    try {
                                        postdata();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });



                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
              if(address.equals("cancel")){
                  AlertDialog.Builder builder=new AlertDialog.Builder(context);
                  builder.setTitle("Reason For cancelling Leave:");
                  builder.setIcon(R.mipmap.kauveryslogos);
                  builder.setMessage(cancelremarks)
                          .setCancelable(true);
                  AlertDialog alertDialog=builder.create();
                  alertDialog.show();
              }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View view) {

    }

    public class MyPendingHolder extends RecyclerView.ViewHolder {
        TextView name, address, city, fromdate, pincode,ones,texts;
        Button reasoncancelbutt;

        public MyPendingHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            city = itemView.findViewById(R.id.city);
            fromdate = itemView.findViewById(R.id.state);
            pincode = itemView.findViewById(R.id.pincode);
            ones=itemView.findViewById(R.id.ones);
            reasoncancelbutt = itemView.findViewById(R.id.reasoncancelbutt);
        }
    }



    private Context getContext() {
        return context;
    }

    private void postdata() throws JSONException {
        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("Loading State...");
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
        jsonObject5.addProperty("sql", "update la_leave SET status='"+statusresult+"' where la_leaveid='" + la_leaveids + "'");
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


                list = response.body().getResult();
                if (response.body().getResult().get(0).getResult().getStatus() != null) {
                    message = list.get(0).getResult().getStatus();

                    Toast.makeText(context,statusresult+" "+ message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, TabbedActivity.class);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Exampleadmin> call, Throwable t) {

            }
        });
    }
}





