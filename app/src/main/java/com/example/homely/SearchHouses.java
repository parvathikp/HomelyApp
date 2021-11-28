package com.example.homely;

import android.app.Dialog;
import android.os.Bundle;
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

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        allhouse.setAdapter(otheradapRec);
        clrfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.filQ="";//call getHouses();
                UserProfile.sortint=0;
                UserProfile.typint=0;
                UserProfile.salint=0;
                UserProfile.minps="";
                UserProfile.maxps="";
                UserProfile.maxbs="";
                UserProfile.minbs="";
                UserProfile.minss="";
                UserProfile.maxss="";
                UserProfile.placep="";
            }
        });
        sort.setSelection(UserProfile.sortint);
        sorts=UserProfile.sortint;
        sort.setOnItemSelectedListener(this);
        filt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filpopup=new Dialog(SearchHouses.this);
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
                        Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
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
                        ArrayList<String> expr=new ArrayList<>();
                        if(UserProfile.salint!=0){
                            expr.add("saleType = "+saleList.get(UserProfile.salint));
                        }
                        if(UserProfile.typint!=0){
                            expr.add("type = "+saleList.get(UserProfile.typint));
                        }
                        if(UserProfile.minps!=""){
                            expr.add("Price >= "+minp.getText().toString().trim());
                        }
                        if(UserProfile.maxps!=""){
                            expr.add("Price <= "+maxp.getText().toString().trim());
                        }
                        if(UserProfile.minss!=""){
                            expr.add("sqft >= "+maxs.getText().toString().trim());
                        }
                        if(UserProfile.maxss!=""){
                            expr.add("sqft <= "+maxs.getText().toString().trim());
                        }
                        if(UserProfile.minbs!=""){
                            expr.add("sqft >= "+maxs.getText().toString().trim());
                        }
                        if(UserProfile.maxbs!=""){
                            expr.add("sqft >= "+maxs.getText().toString().trim());
                        }
                        if(expr.size() >0){
                            s=expr.get(0);
                            //join with where etc.
                        }
                        s=s+" "+sortMap.get(sortList.get(UserProfile.sortint));

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //call getHouses();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sorts=(position);
        //call getHouses()
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
