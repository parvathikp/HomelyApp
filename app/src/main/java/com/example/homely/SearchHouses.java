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

public class SearchHouses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView clrfil,close;
    RecyclerView allhouse;int sortQ;
    ArrayList<OwnHouse> allRec;
    ownHouseAdapter otheradapRec;
    ArrayList<String> sortList,saleList,typeList;
    ArrayAdapter<String> sortadap;
    HashMap<String,String> sortMap;
    Spinner sort;
    PlacesClient placesClient;
    EditText minp,maxp,minb,maxb,mins,maxs;
    int sales,types,sorts;String places;
    Spinner salet,typet;
    Button apply;
    Dialog filpopup;
    ImageView filt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchhouses);
        UserProfile.state=1;
        clrfil=(TextView)findViewById(R.id.clear);
        filt=(ImageView) findViewById(R.id.filter);
        allhouse=(RecyclerView) findViewById(R.id.all);
        sort=(Spinner) findViewById(R.id.sort);
        allRec=new ArrayList<>();
        sortMap=new HashMap<>();
        sortList=new ArrayList<>();
        saleList=new ArrayList<>();
        typeList=new ArrayList<>();
        saleList.add("BOTH");
        saleList.add("SALE");
        saleList.add("RENT");
        typeList.add("ALL");
        typeList.add("FLAT");
        typeList.add("VILLA");
        typeList.add("BUNGALOW");
        typeList.add("PENTHOUSE");
        sales=UserProfile.salint;types=UserProfile.typint;
        sortMap.put("PRICE (Low to High)"," ORDER BY Price");
        sortMap.put("PRICE (High to Low)"," ORDER BY Price DESC");
        sortMap.put("SQFT (Low to High)"," ORDER BY sqft");
        sortMap.put("SQFT (High to Low)"," ORDER BY sqft DESC");
        sortMap.put("BHK (Low to High)"," ORDER BY BHK");
        sortMap.put("BHK (High to Low)"," ORDER BY BHK DESC");
        sortList.add("PRICE (Low to High)");
        sortList.add("PRICE (High to Low)");
        sortList.add("SQFT (Low to High)");
        sortList.add("SQFT (High to Low)");
        sortList.add("BHK (Low to High)");
        sortList.add("BHK (High to Low)");
        ArrayAdapter<String> adap1=new ArrayAdapter<String>(SearchHouses.this, android.R.layout.simple_spinner_item,sortList);
        sort.setAdapter(adap1);
        allhouse.setLayoutManager(new LinearLayoutManager(SearchHouses.this));
        otheradapRec=new ownHouseAdapter(this,allRec);
        allhouse.setHasFixedSize(true);
        allhouse.setAdapter(otheradapRec);
        clrfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.filQ="";//call getHouses();
                UserProfile.typint=0;
                UserProfile.salint=0;
                UserProfile.minps="";
                UserProfile.maxps="";
                UserProfile.maxbs="";
                UserProfile.minbs="";
                UserProfile.minss="";
                UserProfile.maxss="";
                UserProfile.placep="";
                getHouses(" WHERE saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint)),false);
            }
        });
        sort.setSelection(UserProfile.sortint);
        sorts=UserProfile.sortint;
        sort.setOnItemSelectedListener(this);
        filt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filpopup=new Dialog(SearchHouses.this);
                filpopup.setContentView(R.layout.activity_filterhouses);
                salet=(Spinner)filpopup.findViewById(R.id.saleType);
                typet=(Spinner) filpopup.findViewById(R.id.type);
                mins=(EditText) filpopup.findViewById(R.id.minsqft);
                maxs=(EditText) filpopup.findViewById(R.id.maxsqft);
                minb=(EditText) filpopup.findViewById(R.id.minbedr);
                maxb=(EditText) filpopup.findViewById(R.id.maxbedr);
                minp=(EditText) filpopup.findViewById(R.id.minprice);
                maxp=(EditText) filpopup.findViewById(R.id.maxprice);
                apply=(Button) filpopup.findViewById(R.id.apply);
                close=(TextView)filpopup.findViewById(R.id.close);
                mins.setText(UserProfile.minss);
                maxs.setText(UserProfile.maxss);
                minb.setText(UserProfile.minbs);
                maxb.setText(UserProfile.maxbs);
                minp.setText(UserProfile.minps);
                maxp.setText(UserProfile.maxps);
                ArrayAdapter<String> adappop1=new ArrayAdapter<String>(SearchHouses.this, android.R.layout.simple_spinner_item,saleList);
                salet.setAdapter(adappop1);
                salet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        sales=position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ArrayAdapter<String> adappop2=new ArrayAdapter<String>(SearchHouses.this, android.R.layout.simple_spinner_item,typeList);
                typet.setAdapter(adappop2);
                typet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        types=position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                salet.setSelection(UserProfile.salint);
                typet.setSelection(UserProfile.typint);
                String apiKey = getString(R.string.api_key);
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), apiKey);
                }
                places=UserProfile.placep;
                // Create a new Places client instance.
                placesClient = Places.createClient(filpopup.getContext());
                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
                autocompleteFragment.setText(places);
                sales=UserProfile.salint;
                types=UserProfile.typint;
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME));
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        // TODO: Get info about the selected place.
                        places= place.getName().trim().toUpperCase();
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                        // TODO: Handle the error.
                        //Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
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
                        filpopup.dismiss();
                    }
                });
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfile.typint=types;
                        UserProfile.salint=sales;
                        UserProfile.sortint=sorts;
                        UserProfile.minps=minp.getText().toString();
                        UserProfile.maxps=maxp.getText().toString();
                        UserProfile.maxbs=maxb.getText().toString();
                        UserProfile.minbs=minb.getText().toString();
                        UserProfile.minss=mins.getText().toString();
                        UserProfile.maxss=maxs.getText().toString();
                        UserProfile.placep=places;
                        String s="";
                        ArrayList<String> expr=new ArrayList<>();expr.clear();
                        if(UserProfile.salint!=0){

                            expr.add("saleType = \""+saleList.get(UserProfile.salint)+"\"");
                        }
                        if(UserProfile.typint!=0){
                            expr.add("type = \""+typeList.get(UserProfile.typint)+"\"");
                        }
                        if(UserProfile.minps.length()>0){
                            expr.add("Price >= "+minp.getText().toString().trim());
                        }
                        if(UserProfile.maxps.length()>0){
                            expr.add("Price <= "+maxp.getText().toString().trim());
                        }
                        if(UserProfile.minss.length()>0){
                            expr.add("sqft >= "+mins.getText().toString().trim());
                        }
                        if(UserProfile.maxss.length()>0){
                            expr.add("sqft <= "+maxs.getText().toString().trim());
                        }
                        if(UserProfile.minbs.length()>0){
                            expr.add("BHK >= "+minb.getText().toString().trim());
                        }
                        if(UserProfile.maxbs.length()>0){
                            expr.add("BHK <= "+maxb.getText().toString().trim());
                        }
                        if(UserProfile.placep.length()>0){
                            expr.add("Place = \""+places+"\"");
                        }
                        if(expr.size() >0){
                            s=" WHERE "+expr.get(0);
                            for(int i=1;i<expr.size();i++){
                                s=s+" AND "+expr.get(i);
                            }
                            //join with where etc.
                        }UserProfile.filQ=s;
                        if(UserProfile.filQ==""){
                            s=" WHERE saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint));
                        }
                        else{
                            s=UserProfile.filQ+" AND saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint));
                        }
                        FragmentTransaction ft3 = getSupportFragmentManager()
                                .beginTransaction();
                        ft3.remove(getSupportFragmentManager()
                                .findFragmentById(R.id.place_autocomplete_fragment));
                        ft3.commit();
                        filpopup.dismiss();
                        //Toast.makeText(SearchHouses.this,s,Toast.LENGTH_SHORT).show();
                        getHouses(s,true);


                    }
                });
                filpopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                filpopup.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(UserProfile.filQ==""){
            getHouses(" WHERE saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint)),false);
        }
        else{
        getHouses(UserProfile.filQ+" AND saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint)),false);
    }}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        UserProfile.sortint=position;
        if(UserProfile.filQ==""){
            getHouses(" WHERE saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint)),false);
        }
        else{
            getHouses(UserProfile.filQ+" AND saleType <> \"INACTIVE\" "+sortMap.get(sortList.get(UserProfile.sortint)),false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void getHouses(String constr,boolean isFrompopup){
        allRec.clear();

        //Toast.makeText(SearchHouses.this,constr,Toast.LENGTH_SHORT).show();
        ProgressDialog progressDialog=new ProgressDialog(SearchHouses.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/showAllhouses.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        progressDialog.dismiss();
                        try
                        {   allRec.clear();
                            JSONObject jsonObject=new JSONObject(res);
                            String response=jsonObject.getString("message");
                            if(response.equalsIgnoreCase("-1")){
                                //Toast.makeText(SearchHouses.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                            else if(response.equalsIgnoreCase("0")){
                                //Toast.makeText(SearchHouses.this,"No houses found",Toast.LENGTH_SHORT).show();
                                otheradapRec.notifyDataSetChanged();

                            }
                            else{

                                JSONArray arr= jsonObject.getJSONArray("data");
                                if(arr.length()==0){
                                    //Toast.makeText(SearchHouses.this,"NULL",Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(SearchHouses.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend=new HashMap<String,String>();
                toSend.put("constr",constr);
                return toSend;
            }
        };
        RequestQueue req= Volley.newRequestQueue(SearchHouses.this);
        req.add(con);
    }

    @Override
    public void onBackPressed() {
        UserProfile.state=0;
        Intent returnto=new Intent(SearchHouses.this,UserProfile.class);
        Gson gson1=new Gson();
        returnto.putExtra("curUser",gson1.toJson(UserProfile.curUser));
        startActivity(returnto);
        //add clicking on phone  number or its icon takes to phone call
    }
}
