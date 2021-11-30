package com.example.homely;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.appcompat.app.AlertDialog;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OtherHousDetails extends AppCompatActivity{
    TextView addr,desc,price,place,sqft,bedr,saleType,type,oname,oemail,ophone;
    RecyclerView imgRec;
    otherImgAdapter adapRecy;
    String onames,ophones,omails;
    public ArrayList<houseImg> imgListo;
    ImageView call,mail,star;int starStat;
    public static OwnHouse selHouse;
    @Override
    protected void onStart() {
        super.onStart();
        getImages();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherhousedetails);
        Gson gson=new Gson();
        starStat=0;
        selHouse=gson.fromJson(getIntent().getStringExtra("selHouse").toString(),OwnHouse.class);
        imgListo=new ArrayList<>();
        imgRec=(RecyclerView)findViewById(R.id.imglist);
        imgRec.setLayoutManager(new LinearLayoutManager(OtherHousDetails.this));
        adapRecy=new otherImgAdapter(this,imgListo);
        imgRec.setAdapter(adapRecy);
        addr=(TextView) findViewById(R.id.addr11);
        desc=(TextView) findViewById(R.id.desc11);
        place=(TextView) findViewById(R.id.place11);
        price=(TextView) findViewById(R.id.price11);
        saleType=(TextView) findViewById(R.id.saleType11);
        type=(TextView) findViewById(R.id.type11);
        sqft=(TextView) findViewById(R.id.sqft11);
        bedr=(TextView) findViewById(R.id.bedr11);
        oname=(TextView)findViewById(R.id.ownername);
        ophone=(TextView)findViewById(R.id.ownerphone);
        oemail=(TextView)findViewById(R.id.owneremail);
        call=(ImageView)findViewById(R.id.call);
        star=(ImageView)findViewById(R.id.star);
        mail=(ImageView)findViewById(R.id.mail);
        addr.setText(selHouse.getAddr());
        desc.setText(selHouse.getDesc());
        price.setText(Integer.toString(selHouse.getPrice()));
        sqft.setText(Double.toString(selHouse.getSqft()));
        saleType.setText(selHouse.getSaleType());
        type.setText(selHouse.getType());
        bedr.setText(Integer.toString(selHouse.getBedr()));
        place.setText(selHouse.getPlace().toUpperCase());
        checkFave();
        setOwnerinfo();
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toCall="tel:"+ophones;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(toCall));
                startActivity(intent);
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ omails});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(starStat==0){
                    starStat=1;
                    star.setImageResource(R.drawable.starcol);
                    setAsFav();
                }else{
                    starStat=0;
                    star.setImageResource(R.drawable.star);
                    remAsFav();
                }
            }
        });
        }

    public void setOwnerinfo(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/fetchOwnerinfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("message");

                            if(success.equals("1")){
                                onames=jsonObject.getString("oname");
                                ophones=jsonObject.getString("ophone");
                                omails=jsonObject.getString("omail");
                                oname.setText(onames);
                                ophone.setText(ophones);
                                oemail.setText(omails);
                                //Toast.makeText(OtherHousDetails.this,jsonObject.getString("ophone"),Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(OtherHousDetails.this,"Failed to fetch",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherHousDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("oid",Integer.toString(OtherHousDetails.selHouse.getOid()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(OtherHousDetails.this);
        req.add(request);
    }

    public  void getImages(){
        imgListo.clear();
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/fetchImages.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(success.equals("1")){
                                if(jsonArray.length() ==0){
                                    Toast.makeText(OtherHousDetails.this,"No images found",Toast.LENGTH_SHORT).show();
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String imageurl = object.getString("image");
                                    String url = "https://wayless-editor.000webhostapp.com/Images/"+imageurl;
                                    imgListo.add(new houseImg(object.getInt("id"),OtherHousDetails.selHouse.getHid(),url));

                                    adapRecy.notifyDataSetChanged();
                                }

                            }else{
                                Toast.makeText(OtherHousDetails.this,"Failed to fetch",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherHousDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("hid",Integer.toString(selHouse.getHid()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(OtherHousDetails.this);
        req.add(request);
    }
    void checkFave(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/getIfFav.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("1")){
                            starStat=1;
                            star.setImageResource(R.drawable.starcol);
                        }
                        else if(response.equalsIgnoreCase("0")){
                            starStat=0;
                            star.setImageResource(R.drawable.star);
                        }
                        else{
                            Toast.makeText(OtherHousDetails.this,response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherHousDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("hid",Integer.toString(selHouse.getHid()));
                toSend1.put("oid",Integer.toString(UserProfile.curUser.getID()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(OtherHousDetails.this);
        req.add(request);
    }
    void setAsFav(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/setFav.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("1")){
                            Toast.makeText(OtherHousDetails.this,"Marked",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(OtherHousDetails.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherHousDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("hid",Integer.toString(selHouse.getHid()));
                toSend1.put("oid",Integer.toString(UserProfile.curUser.getID()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(OtherHousDetails.this);
        req.add(request);
    }
    void remAsFav(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/remFav.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("1")){
                            Toast.makeText(OtherHousDetails.this,"Removed star",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(OtherHousDetails.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OtherHousDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("hid",Integer.toString(selHouse.getHid()));
                toSend1.put("oid",Integer.toString(UserProfile.curUser.getID()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(OtherHousDetails.this);
        req.add(request);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
