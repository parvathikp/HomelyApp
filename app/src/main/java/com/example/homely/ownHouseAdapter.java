package com.example.homely;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ownHouseAdapter extends RecyclerView.Adapter<ownHouseAdapter.modViewHolder>{
    ArrayList<OwnHouse> data;
    Context context;
    ownHouseAdapter(Context context, ArrayList<OwnHouse> info){
        this.data=info;this.context=context;
    }
    @NonNull
    @Override
    public modViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ownhome,parent,false);
        return new modViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull modViewHolder holder,int position){
        OwnHouse houseObj=data.get(position);
        holder.place.setText(houseObj.getPlace().toString().toUpperCase());
        holder.price.setText(Integer.toString(houseObj.getPrice()));
        holder.bedr.setText(Integer.toString(houseObj.getBedr())+" BHK");
        holder.sqft.setText(Double.toString(houseObj.getSqft())+" SQFT");
        holder.sale.setText(houseObj.getSaleType().toUpperCase());
        holder.type.setText(houseObj.getType().toUpperCase());
        holder.totalview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserProfile.state==0){
                Intent toDetail=new Intent(context,houseDetails.class);
                Gson gson=new Gson();
                String tosend=gson.toJson(houseObj);
                toDetail.putExtra("curHouse",tosend);
                context.startActivity(toDetail);}
                else{
                    Intent toDetail=new Intent(context,OtherHousDetails.class);
                    Gson gson=new Gson();
                    String tosend=gson.toJson(houseObj);
                    toDetail.putExtra("selHouse",tosend);
                    context.startActivity(toDetail);
                }

            }
        });

        return;
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    class modViewHolder extends RecyclerView.ViewHolder{
        View totalview;
        TextView sale,price,bedr,sqft,place,type;
        public modViewHolder(View v){
            super(v);
            totalview=v;
            sale=(TextView) v.findViewById(R.id.saleType);
            price=(TextView)v.findViewById(R.id.price);
            bedr=(TextView)v.findViewById(R.id.bedr);
            sqft=(TextView)v.findViewById(R.id.sqft);
            place=(TextView)v.findViewById(R.id.place);
            type=(TextView)v.findViewById(R.id.type);
        }

        public View getTotalview() {
            return totalview;
        }

        public TextView getSale() {
            return sale;
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getBedr() {
            return bedr;
        }

        public TextView getSqft() {
            return sqft;
        }

        public TextView getPlace() {
            return place;
        }

        public TextView getType() {
            return type;
        }

    }
}