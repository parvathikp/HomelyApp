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

public class ownImgAdapter extends RecyclerView.Adapter<ownImgAdapter.difViewHolder>{
    ArrayList<houseImg> data;
    Context context;
    ownImgAdapter(Context context, ArrayList<houseImg> info){
        this.data=info;this.context=context;
    }
    @NonNull
    @Override
    public difViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ownimage,parent,false);
        return new difViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull difViewHolder holder,int position){
        houseImg houseObj=data.get(position);
        int t=position;
        Glide.with(context).load(houseObj.getImgurl()).into(holder.img);
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteimg(t);

            }
        });
        return;
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    class difViewHolder extends RecyclerView.ViewHolder{
        View totalview;
        ImageView img;
        ImageView del;
        public difViewHolder(View v){
            super(v);
            totalview=v;
            img=(ImageView)v.findViewById(R.id.imghome);
            del=(ImageView)v.findViewById(R.id.delimage);
        }

    }

    void deleteimg(int t){
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/delImg.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("1")){
                            Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();
                            data.remove(t);
                            notifyItemRemoved(t);
                            notifyItemRangeChanged(t,data.size());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("id",Integer.toString(data.get(t).getId()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(context);
        req.add(request);
    }
}