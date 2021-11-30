package com.example.homely;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.List;
import java.util.Map;

public class UserHouses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button add;
    Spinner sale,type;
    EditText price,addr,desc,bedr,sqft;
    TextView close;
    Button save;
    String sales1,types1,prices,addrs,descs,bedrs,places,sqfts;
    Dialog popup;
    public DialogInterface.OnClickListener dialogClickListener;
    ArrayList<String> saleTypes;
    ArrayList<String> types;
    PlacesClient placesClient;
    RecyclerView recyclerView;
    ArrayList<OwnHouse> dataRec;
    ownHouseAdapter adapterRec;
    View frag;

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhouse);
        add=findViewById(R.id.add);
        dataRec=new ArrayList<OwnHouse>();
        recyclerView=findViewById(R.id.userhouse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterRec=new ownHouseAdapter(this,dataRec);
        recyclerView.setAdapter(adapterRec);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup=new Dialog(UserHouses.this);
                popup.setContentView(R.layout.activity_addhouse);
                sale=(Spinner) popup.findViewById(R.id.saleType);
                type=(Spinner) popup.findViewById(R.id.type);
                save=(Button)popup.findViewById(R.id.save);
                sqft=(EditText)popup.findViewById(R.id.sqft);
                close=(TextView)popup.findViewById(R.id.close);
                String apiKey = getString(R.string.api_key);
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), apiKey);
                }

                // Create a new Places client instance.
                placesClient = Places.createClient(popup.getContext());
                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME));
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        // TODO: Get info about the selected place.
                        places=place.getName().toString().trim();
                       // Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                        // TODO: Handle the error.
                        Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                saleTypes=new ArrayList<>();
                saleTypes.add("SALE".toUpperCase());
                saleTypes.add("RENT");
                saleTypes.add("INACTIVE");
                sales1="SALE";
                types1="FLAT";
                types=new ArrayList<>();
                types.add("FLAT");
                types.add("VILLA");
                types.add("BUNGALOW");
                types.add("PENTHOUSE");
                ArrayAdapter<String> adap1=new ArrayAdapter<String>(UserHouses.this, android.R.layout.simple_spinner_item,saleTypes);
                ArrayAdapter<String> adap2=new ArrayAdapter<String>(UserHouses.this, android.R.layout.simple_spinner_item,types);
                sale.setAdapter(adap1);
                type.setAdapter(adap2);
                sale.setOnItemSelectedListener(UserHouses.this);
                type.setOnItemSelectedListener(UserHouses.this);
                price=(EditText)popup.findViewById(R.id.price);
                addr=(EditText)popup.findViewById(R.id.address);
                desc=(EditText)popup.findViewById(R.id.descr);
                bedr=(EditText)popup.findViewById(R.id.bhk);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addrs=addr.getText().toString().trim();
                        descs=desc.getText().toString().trim();
                        prices=price.getText().toString().trim();
                        bedrs=bedr.getText().toString().trim();
                        sqfts=sqft.getText().toString().trim();
                        edit();
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FragmentTransaction ft2 = getSupportFragmentManager()
                                .beginTransaction();
                        ft2.remove(getSupportFragmentManager()
                                .findFragmentById(R.id.place_autocomplete_fragment));
                        ft2.commit();
                        popup.dismiss();
                    }
                });
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sales1=sale.getSelectedItem().toString();
        types1=type.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void  edit(){
        if(bedrs.equalsIgnoreCase("")){
            Toast.makeText(UserHouses.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(prices.equalsIgnoreCase("")){
            Toast.makeText(UserHouses.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }else{
            ProgressDialog progressDialog=new ProgressDialog(UserHouses.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/addHouse.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String res) {
                            progressDialog.dismiss();
                            try
                            {
                                JSONObject js=new JSONObject(res);
                                String response;
                                response = js.getString("message");
                                if(response.equalsIgnoreCase("0")){
                                    Toast.makeText(UserHouses.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(UserHouses.this,"Success",Toast.LENGTH_SHORT).show();
                                    dataRec.add(new OwnHouse(js.getInt("hid"),UserProfile.curUser.getID(),addrs,descs,sales1,types1,places,Double.parseDouble(sqfts),Integer.parseInt(bedrs),Integer.parseInt(prices)));
                                    adapterRec.notifyDataSetChanged();
                                    FragmentTransaction ft2 = getSupportFragmentManager()
                                            .beginTransaction();
                                    ft2.remove(getSupportFragmentManager()
                                            .findFragmentById(R.id.place_autocomplete_fragment));
                                    ft2.commit();
                                    popup.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(UserHouses.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> toSend=new HashMap<String,String>();
                    toSend.put("place",places);
                    toSend.put("sale",sales1);
                    toSend.put("type",types1);
                    toSend.put("addr",addrs);
                    toSend.put("descr",descs);
                    toSend.put("price",prices);
                    toSend.put("bhk",bedrs);
                    toSend.put("oid",Integer.toString(UserProfile.curUser.getID()));
                    toSend.put("sqft",sqfts);
                    return toSend;
                }
            };
            RequestQueue req= Volley.newRequestQueue(UserHouses.this);
            req.add(con);
        }}
    void getData(){
        StringRequest con1=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/showuserHomes.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {

                            JSONObject jsonObject=new JSONObject(res);
                            String response=jsonObject.getString("message");
                            if(response.equalsIgnoreCase("-1")){
                                Toast.makeText(UserHouses.this,"0",Toast.LENGTH_SHORT).show();
                            }
                            else if(response.equalsIgnoreCase("0")){
                                Toast.makeText(UserHouses.this,"No houses found",Toast.LENGTH_SHORT).show();

                            }
                            else{
                                JSONArray arr= jsonObject.getJSONArray("data");
                                Toast.makeText(UserHouses.this,"Success",Toast.LENGTH_LONG).show();
                                for(int i=0;i< arr.length();i++){
                                    JSONObject obj=arr.getJSONObject(i);
                                    dataRec.add(new OwnHouse(Integer.parseInt(obj.getString("hid")),Integer.parseInt(obj.getString("oid")),obj.getString("addr"),
                                            obj.getString("descr"),obj.getString("saleType"),obj.getString("type"),
                                            obj.getString("place"),Double.parseDouble(obj.getString("sqft")),Integer.parseInt(obj.getString("bhk")),Integer.parseInt(obj.getString("price"))));

                                }
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        adapterRec.notifyDataSetChanged();
                                    }
                                }, 10);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserHouses.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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
        RequestQueue req= Volley.newRequestQueue(UserHouses.this);
        req.add(con1);
    }

    @Override
    public void onBackPressed() {
        Intent touserProf=new Intent(UserHouses.this,UserProfile.class);
        Gson gson1=new Gson();
        touserProf.putExtra("curUser",gson1.toJson(UserProfile.curUser));
        startActivity(touserProf);
    }
}

