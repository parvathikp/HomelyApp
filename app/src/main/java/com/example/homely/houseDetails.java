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

public class houseDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView edit,delete;
    String places,styp,typ,encodedImage;
    TextView addr,desc,price,place,sqft,bedr,saleType,type;
    EditText addrp,descp,pricep,bedrp,sqftp;
    Spinner saleTypep,typep;
    TextView close;
    Dialog addimgPopup;
    RecyclerView imgRec;
    ownImgAdapter adapRecy;
    public static ArrayList<houseImg> imgList;
    Button addimg;
    PlacesClient placesClient;
    View frag;
    String addrf,descf,bedrf,pricef,sqftf;
    Button select,upload;
    ImageView img;
    Button save;
    ArrayList<String> sty;ArrayList<String> ty;
    public static OwnHouse curHouse;
    Dialog popup;
    public DialogInterface.OnClickListener dialogClickListener;
    @Override
    protected void onStart() {
        super.onStart();
        getImages();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ownhousedetails);
        Gson gson=new Gson();
        curHouse=gson.fromJson(getIntent().getStringExtra("curHouse").toString(),OwnHouse.class);
        imgList=new ArrayList<>();
        imgRec=(RecyclerView)findViewById(R.id.imglist);
        imgRec.setLayoutManager(new LinearLayoutManager(houseDetails.this));
        adapRecy=new ownImgAdapter(this,imgList);
        imgRec.setAdapter(adapRecy);
        edit=(ImageView)findViewById(R.id.edit1);
        delete=(ImageView)findViewById(R.id.delete1);
        addr=(TextView) findViewById(R.id.addr11);
        desc=(TextView) findViewById(R.id.desc11);
        place=(TextView) findViewById(R.id.place11);
        price=(TextView) findViewById(R.id.price11);
        saleType=(TextView) findViewById(R.id.saleType11);
        type=(TextView) findViewById(R.id.type11);
        sqft=(TextView) findViewById(R.id.sqft11);
        bedr=(TextView) findViewById(R.id.bedr11);
        addimg=(Button)findViewById(R.id.addimg);
        addr.setText(curHouse.getAddr());
        desc.setText(curHouse.getDesc());
        price.setText(Integer.toString(curHouse.getPrice()));
        sqft.setText(Double.toString(curHouse.getSqft()));
        saleType.setText(curHouse.getSaleType());
        type.setText(curHouse.getType());
        bedr.setText(Integer.toString(curHouse.getBedr()));
        place.setText(curHouse.getPlace().toUpperCase());
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                AlertDialog.Builder adb = new AlertDialog.Builder(houseDetails.this);
                adb.setMessage("Delete house?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ProgressDialog progressDialog=new ProgressDialog(houseDetails.this);
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();
                                StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/delHouse.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                progressDialog.dismiss();
                                                if(response.equalsIgnoreCase("-1")){
                                                    Toast.makeText(houseDetails.this,"Connection failed",Toast.LENGTH_SHORT).show();
                                                }
                                                else if(response.equalsIgnoreCase("0")){
                                                    Toast.makeText(houseDetails.this,"Failed",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(houseDetails.this,"Deleted house!",Toast.LENGTH_SHORT).show();
                                                    Intent back=new Intent(houseDetails.this,UserHouses.class);
                                                    startActivity(back);
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(houseDetails.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> toSend=new HashMap<String,String>();
                                        toSend.put("hid",Integer.toString(curHouse.getHid()));
                                        return toSend;
                                    }
                                };
                                RequestQueue req= Volley.newRequestQueue(houseDetails.this);
                                req.add(con);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup=new Dialog(houseDetails.this);
                popup.setContentView(R.layout.activity_edithouse);
                saleTypep=(Spinner) popup.findViewById(R.id.saleType);
                typep=(Spinner) popup.findViewById(R.id.type);
                save=(Button)popup.findViewById(R.id.save);
                sqftp=(EditText)popup.findViewById(R.id.sqft);
                close=(TextView)popup.findViewById(R.id.close);
                pricep=(EditText)popup.findViewById(R.id.price);
                addrp=(EditText)popup.findViewById(R.id.address);
                descp=(EditText)popup.findViewById(R.id.descr);
                bedrp=(EditText)popup.findViewById(R.id.bhk);
                bedrp.setText(bedr.getText().toString().trim());
                addrp.setText(addr.getText().toString().trim());
                pricep.setText(price.getText().toString().trim());
                descp.setText(desc.getText().toString().trim());
                sqftp.setText(sqft.getText().toString().trim());
                String apiKey = getString(R.string.api_key);
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), apiKey);
                }
                places=place.getText().toString().trim().toUpperCase();
                // Create a new Places client instance.
                placesClient = Places.createClient(popup.getContext());
                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
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
                sty=new ArrayList<>();
                sty.add("SALE");
                sty.add("RENT");
                sty.add("INVALID");
                styp="SALE";
                typ="FLAT";
                ty=new ArrayList<>();
                ty.add("FLAT");
                ty.add("VILLA");
                ty.add("BUNGALOW");
                ty.add("PENTHOUSE");
                ArrayAdapter<String> adap1=new ArrayAdapter<String>(houseDetails.this, android.R.layout.simple_spinner_item,sty);
                ArrayAdapter<String> adap2=new ArrayAdapter<String>(houseDetails.this, android.R.layout.simple_spinner_item,ty);
                saleTypep.setAdapter(adap1);
                typep.setAdapter(adap2);
                typep.setSelection(ty.indexOf(type.getText().toString().trim()));
                saleTypep.setSelection(sty.indexOf(saleType.getText().toString().trim()));
                saleTypep.setOnItemSelectedListener(houseDetails.this);
                typep.setOnItemSelectedListener(houseDetails.this);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addrf=addrp.getText().toString().trim();
                        descf=descp.getText().toString().trim();
                        pricef=pricep.getText().toString().trim();
                        bedrf=bedrp.getText().toString().trim();
                        sqftf=sqftp.getText().toString().trim();
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
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addimgPopup = new Dialog(houseDetails.this);
                addimgPopup.setContentView(R.layout.addimgpopup);
                select = (Button) addimgPopup.findViewById(R.id.select);
                upload = (Button) addimgPopup.findViewById(R.id.upload);
                img = (ImageView) addimgPopup.findViewById(R.id.imgsel);
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dexter.withContext(houseDetails.this)
                                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {

                                        Intent intent = new Intent(Intent.ACTION_PICK);
                                        intent.setType("image/*");
                                        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }
                });
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest request = new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/uploadImage.php"
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {
                                try {
                                    JSONObject jsonObject=new JSONObject(res);
                                    String response=jsonObject.getString("message");
                                    if(response.equalsIgnoreCase("Image Uploaded")){
                                        String imgeurl=jsonObject.getString("data");
                                        imgList.clear();
                                        getImages();
                                        addimgPopup.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(houseDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("image", encodedImage);
                                params.put("hid",Integer.toString(curHouse.getHid()));
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(houseDetails.this);
                        requestQueue.add(request);
                    }
                });
                addimgPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addimgPopup.show();

            }

    });}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null){

            Uri filePath = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);

                imageStore(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void imageStore(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);


    }
//
//    public void btnShowImages(View view) {
//        startActivity(new Intent(getApplicationContext(),ShowImages.class));
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        styp=saleTypep.getSelectedItem().toString();
        typ=typep.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public  void getImages(){
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
                                    Toast.makeText(houseDetails.this,"No images found",Toast.LENGTH_SHORT).show();
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String imageurl = object.getString("image");
                                    String url = "https://wayless-editor.000webhostapp.com/Images/"+imageurl;
                                    imgList.add(new houseImg(object.getInt("id"),houseDetails.curHouse.getHid(),url));

                                    adapRecy.notifyDataSetChanged();
                                }

                            }else{
                                Toast.makeText(houseDetails.this,"Failed to fetch",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(houseDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> toSend1=new HashMap<String,String>();
                toSend1.put("hid",Integer.toString(curHouse.getHid()));
                return toSend1;
            }
        };
        RequestQueue req= Volley.newRequestQueue(houseDetails.this);
        req.add(request);
    }
    public void  edit(){
        if(bedrf.equalsIgnoreCase("")){
            Toast.makeText(houseDetails.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(pricef.equalsIgnoreCase("")){
            Toast.makeText(houseDetails.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }else{
            ProgressDialog progressDialog=new ProgressDialog(houseDetails.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/editHouse.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            if(response.equalsIgnoreCase("0")){
                                Toast.makeText(houseDetails.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(houseDetails.this,"Updated",Toast.LENGTH_SHORT).show();
                                addr.setText(addrf);
                                desc.setText(descf);
                                price.setText(pricef);
                                sqft.setText(sqftf);
                                saleType.setText(styp);
                                type.setText(typ);
                                bedr.setText(bedrf);
                                place.setText(places.toUpperCase());
                                FragmentTransaction ft2 = getSupportFragmentManager()
                                        .beginTransaction();
                                ft2.remove(getSupportFragmentManager()
                                        .findFragmentById(R.id.place_autocomplete_fragment));
                                ft2.commit();
                                popup.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(houseDetails.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> toSend=new HashMap<String,String>();
                    toSend.put("place",places.toUpperCase());
                    toSend.put("sale",styp);
                    toSend.put("type",typ);
                    toSend.put("addr",addrf);
                    toSend.put("descr",descf);
                    toSend.put("price",pricef);
                    toSend.put("bhk",bedrf);
                    toSend.put("hid",Integer.toString(houseDetails.curHouse.getHid()));
                    toSend.put("sqft",sqftf);
                    return toSend;
                }
            };
            RequestQueue req= Volley.newRequestQueue(houseDetails.this);
            req.add(con);
        }}

    @Override
    public void onBackPressed() {
        Intent tohomes=new Intent(houseDetails.this,UserHouses.class);
        tohomes.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(tohomes);
        finish();
    }
}
