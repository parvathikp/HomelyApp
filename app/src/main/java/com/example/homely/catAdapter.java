package com.example.homely;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.startActivity;

public class catAdapter extends RecyclerView.Adapter<catAdapter.customViewHolder>{
    List<CatObj> data;Context context;
    ViewPager2 viewpager;
    catAdapter(Context context,List<CatObj> info,ViewPager2 viewPager){
        this.data=info;this.context=context;
        this.viewpager=viewPager;
    }
    @NonNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_layout,parent,false);

        return new customViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull customViewHolder holder,int position){
        CatObj cat=data.get(position);
        int t=position;
        holder.img.setImageResource(cat.getImg());
        holder.name.setText(cat.getTitle());
        holder.totalview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t==0){
                Intent toNext=new Intent(context,UserHouses.class);
                context.startActivity(toNext);}
                else if(t==1){
                    Intent toNext=new Intent(context,SearchHouses.class);
                    context.startActivity(toNext);
                }
                else if(t==2){
                    Intent toNext=new Intent(context,favourites.class);
                    context.startActivity(toNext);}
                }
        });

        return;
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    class customViewHolder extends RecyclerView.ViewHolder{
        ImageView img;View totalview;
        TextView name;int ind;
        public customViewHolder(View v){
            super(v);
            totalview=v;
            img=(ImageView)v.findViewById(R.id.img);
            name=(TextView)v.findViewById(R.id.title);
        }

        public ImageView getCatImg() {
            return img;
        }

        public TextView getCatName() {
            return name;
        }
    }
}