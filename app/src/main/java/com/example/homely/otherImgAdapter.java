package com.example.homely;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class otherImgAdapter extends RecyclerView.Adapter<otherImgAdapter.dif2ViewHolder>{
    ArrayList<houseImg> data;
    Context context;
    otherImgAdapter(Context context, ArrayList<houseImg> info){
        this.data=info;this.context=context;
    }
    @NonNull
    @Override
    public dif2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_otherimage,parent,false);
        return new dif2ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull dif2ViewHolder holder,int position){
        houseImg houseObj=data.get(position);
        int t=position;
        Glide.with(context).load(houseObj.getImgurl()).into(holder.img);
        return;
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    class dif2ViewHolder extends RecyclerView.ViewHolder{
        View totalview;
        ImageView img;
        public dif2ViewHolder(View v){
            super(v);
            totalview=v;
            img=(ImageView)v.findViewById(R.id.imghome);
        }

    }

}