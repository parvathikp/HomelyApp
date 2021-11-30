package com.example.homely;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class favourites extends AppCompatActivity {
    RecyclerView allhouse;
    ArrayList<OwnHouse> allRec;
    ownHouseAdapter otheradapRec;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        UserProfile.state=1;
        allhouse=(RecyclerView) findViewById(R.id.all);
        allRec=new ArrayList<>();
        allhouse.setLayoutManager(new LinearLayoutManager(favourites.this));
        otheradapRec=new ownHouseAdapter(this,allRec);
        allhouse.setHasFixedSize(true);
        allhouse.setAdapter(otheradapRec);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllFavs();}

    public void getAllFavs(){
        allRec.clear();
        ProgressDialog progressDialog=new ProgressDialog(favourites.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/showAllFaves.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        progressDialog.dismiss();
                        try
                        {   allRec.clear();
                            JSONObject jsonObject=new JSONObject(res);
                            String response=jsonObject.getString("message");
                            if(response.equalsIgnoreCase("-1")){
                                Toast.makeText(favourites.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                            else if(response.equalsIgnoreCase("0")){
                                //Toast.makeText(favourites.this,"No houses found",Toast.LENGTH_SHORT).show();
                                otheradapRec.notifyDataSetChanged();

                            }
                            else{

                                JSONArray arr= jsonObject.getJSONArray("data");
                                if(arr.length()==0){
                                    //Toast.makeText(favourites.this,"NULL",Toast.LENGTH_SHORT).show();
                                }
                                for(int i=0;i< arr.length();i++){
                                    JSONObject obj=arr.getJSONObject(i);
                                    allRec.add(new OwnHouse(Integer.parseInt(obj.getString("hid")),Integer.parseInt(obj.getString("oid")),obj.getString("addr"),
                                            obj.getString("descr"),obj.getString("saleType"),obj.getString("type"),
                                            obj.getString("place"),Double.parseDouble(obj.getString("sqft")),Integer.parseInt(obj.getString("bhk")),Integer.parseInt(obj.getString("price"))));

                                }
                                otheradapRec.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //Toast.makeText(favourites.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend=new HashMap<String,String>();
                toSend.put("oid",Integer.toString(UserProfile.curUser.getID()));
                return toSend;
            }
        };
        RequestQueue req= Volley.newRequestQueue(favourites.this);
        req.add(con);
    }

    @Override
    public void onBackPressed() {
        UserProfile.state=0;
        Intent returnto=new Intent(favourites.this,UserProfile.class);
        Gson gson1=new Gson();
        returnto.putExtra("curUser",gson1.toJson(UserProfile.curUser));
        startActivity(returnto);
    }
}