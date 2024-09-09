package com.kauveryhospital.fieldforce.UserAdmin.corporateadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kauveryhospital.fieldforce.R;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> mExampleList;
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView1;
        public TextView mTextView2,mTextView3,mTextView4,mTextView5;
        public ExampleViewHolder(View itemView) {
            super(itemView);
          //  mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.name);
            mTextView2 = itemView.findViewById(R.id.address);
            mTextView3 =itemView.findViewById(R.id.city);
            mTextView4=itemView.findViewById(R.id.state);
            mTextView5=itemView.findViewById(R.id.pincode);
        }
    }
    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);
        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getmName());
        holder.mTextView2.setText(currentItem.getmAddress());
        holder.mTextView3.setText(currentItem.getmCity());
        holder.mTextView4.setText(currentItem.getmState());
        holder.mTextView5.setText(currentItem.getmPincode());
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
    public void filterList(ArrayList<ExampleItem> filteredList) {
        mExampleList = filteredList;
        notifyDataSetChanged();
    }
}