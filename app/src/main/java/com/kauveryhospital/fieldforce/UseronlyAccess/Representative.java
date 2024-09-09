package com.kauveryhospital.fieldforce.UseronlyAccess;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kauveryhospital.fieldforce.NetworkChangeReceiver;
import com.kauveryhospital.fieldforce.R;
import com.kauveryhospital.fieldforce.UseronlyAccess.contact.ContactActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.corporate.CorporateActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.leaverequest.LeaveRequestActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.planned.CheckoutActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.travelExpenses.ListTravelExpensesActivity;
import com.kauveryhospital.fieldforce.UseronlyAccess.unplanned.UnPlannedActivity;
import com.kauveryhospital.fieldforce.workstartserviceuseronly.WorkStartActivity;

import java.util.Date;

public class Representative extends Fragment {
    private NetworkChangeReceiver networkChangeReceiver;

    Button btn_checkout,btn_start_location_updates;

    static Representative instance;
    TextView empname;
    ImageView refresh;
    String latitude,longitude,currentDateTimeString,pswd,employeename,dateshrdcurrentdate;


    private static final String PREFS_NAME = "preferenceName";
    private static final String PREFS_NAMES = "loginpref";
    private static final String PREFS_NAMESS="punch";
    CardView corporate,chkoutact,contact,leaverequest,unplannedvisit,travelexp;


    public Representative() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_representative, container, false);
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        latitude= settings.getString("curlatitude","curlatitude");
        longitude=settings.getString("curlongitude","curlongitude");
        SharedPreferences settings1 = getActivity().getSharedPreferences(PREFS_NAMES, 0);
        employeename=settings1.getString("username","");
        pswd=settings1.getString("password","");
        SharedPreferences settings2 = getActivity().getSharedPreferences(PREFS_NAMESS, 0);
        dateshrdcurrentdate=settings2.getString("currentdate","");
        currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        empname=root.findViewById(R.id.empname);
        empname.setText(employeename);
        btn_checkout=root.findViewById(R.id.btn_checkout);
        corporate=root.findViewById(R.id.corporate);
        chkoutact=root.findViewById(R.id.chkoutact);
        contact=root.findViewById(R.id.contact);
        leaverequest=root.findViewById(R.id.leaverequest);
        unplannedvisit=root.findViewById(R.id.unplanvisits);
        refresh=root.findViewById(R.id.refresh);
        travelexp=root.findViewById(R.id.travelexp);
        //   employeemapping=root.findViewById(R.id.employeemapping);
//        unplannedcheckin=root.findViewById(R.id.unplannedcheckin);
        //     closedshop=root.findViewById(R.id.closedshop);
        btn_start_location_updates=root.findViewById(R.id.btn_start_location_updates);

        btn_start_location_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkStartActivity.class);
                startActivity(intent);
            }
        });
//        btn_checkout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                withEditText(v);
//            }
//        });
        corporate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CorporateActivity.class);
                startActivity(intent);

            }
        });
        chkoutact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
               startActivity(intent);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
               startActivity(intent);
            }
        });
        leaverequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaveRequestActivity.class);
                startActivity(intent);
            }
        });
        unplannedvisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UnPlannedActivity.class);
                startActivity(intent);
            }
        });
        travelexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListTravelExpensesActivity.class);
                startActivity(intent);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkStartActivity.class);
                startActivity(intent);

            }
        });
        return root;

    }
    public static Representative getInstance() {
        return instance;
    }
    public void withEditText(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reason for early checkout");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "checkout successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}