package com.example.homely;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    TextView uname,fname,email,phone,x;
    String us,ful,pass,ema,ph;
    ImageView edit,del,log;
    Dialog popup;
    Button save;
    CheckBox show;
    int backPressCount=0;
    static user curUser;
    static String filQ;
    static String minps,maxps,minbs,maxbs,minss,maxss,placep;
    static int salint,typint,sortint;
    static int state=0;
    Boolean p=true,q=true;
    List<CatObj> catList;
    EditText usname,passw,emailid,fulname,pho;
    ViewPager2 menu;
    public DialogInterface.OnClickListener dialogClickListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state=0;
        setContentView(R.layout.activity_user);
        Gson gson=new Gson();filQ="";salint=0;typint=0;
        minps="";maxps="";minbs="";maxbs="";minss="";maxss="";
        placep="";sortint=0;
        menu=findViewById(R.id.options);
        curUser=gson.fromJson(getIntent().getStringExtra("curUser"),user.class);
        catList=new ArrayList<>();
        catList.add(new CatObj(R.drawable.userhome,"Your houses"));
        catList.add(new CatObj(R.drawable.allhouse,"Search houses"));
        catList.add(new CatObj(R.drawable.fav,"Favourites"));
        catAdapter catAdap=new catAdapter(UserProfile.this,catList,menu);
        menu.setAdapter(catAdap);
        menu.setClipToPadding(false);
        menu.setClipChildren(false);
        menu.setOffscreenPageLimit(3);
        menu.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        menu.setPageTransformer(compositePageTransformer);
        uname=findViewById(R.id.username);
        fname=findViewById(R.id.fullname);
        email=findViewById(R.id.emailID);
        phone=findViewById(R.id.phone);
        uname.setText(curUser.getUname());
        fname.setText(curUser.getName());
        email.setText(curUser.getEmailID());
        phone.setText(curUser.getPhone());
        edit=findViewById(R.id.edit);
        del=findViewById(R.id.delete);
        log=findViewById(R.id.logout);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup=new Dialog(UserProfile.this);
                popup.setContentView(R.layout.activity_edituser);
                usname=(EditText)popup.findViewById(R.id.userName);
                fulname=(EditText)popup.findViewById(R.id.full);
                emailid=(EditText)popup.findViewById(R.id.email);
                passw=(EditText)popup.findViewById(R.id.password);
                pho=(EditText)popup.findViewById(R.id.phone);
                save=(Button)popup.findViewById(R.id.save);
                show=(CheckBox)popup.findViewById(R.id.show);
                x=(TextView)popup.findViewById(R.id.close);
                usname.setText(uname.getText());
                fulname.setText(fname.getText());
                emailid.setText(email.getText());
                pho.setText(phone.getText());
                passw.setText(curUser.getPassword());
                show.setChecked(false);
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(show.isChecked()){
                            passw.setTransformationMethod(null);
                        }else{
                            passw.setTransformationMethod(new PasswordTransformationMethod());
                        }
                    }
                });
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit();
                    }
                });
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();

            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setMessage("Delete account?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        //delete from house table too
                        ProgressDialog progressDialog=new ProgressDialog(UserProfile.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/delUser.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        if(response.equalsIgnoreCase("-1")){
                                            Toast.makeText(UserProfile.this,"Connection failed",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(response.equalsIgnoreCase("0")){
                                            Toast.makeText(UserProfile.this,curUser.getID(),Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(UserProfile.this,"Deleted account!",Toast.LENGTH_SHORT).show();
                                            popup.dismiss();
                                            Intent back=new Intent(UserProfile.this,RegisterActivity.class);
                                            startActivity(back);
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(UserProfile.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> toSend=new HashMap<String,String>();
                                toSend.put("id",Integer.toString(curUser.getID()));
                                return toSend;
                            }
                        };
                        RequestQueue req= Volley.newRequestQueue(UserProfile.this);
                        req.add(con);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
    }
    @Override
    public void onBackPressed(){
        backPressCount+=1;
        if(backPressCount==2){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else{
            Toast.makeText(UserProfile.this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        }
    }
    public void edit(){
        us=usname.getText().toString().trim();
        ema=emailid.getText().toString().trim();
        pass=passw.getText().toString().trim();
        ful=fulname.getText().toString().trim();
        ph=pho.getText().toString().trim();
        if(us.equalsIgnoreCase("")){
            Toast.makeText(UserProfile.this,"Empty userName",Toast.LENGTH_SHORT).show();
        }
        else if(ful.equalsIgnoreCase("")){
            Toast.makeText(UserProfile.this,"Empty Full name",Toast.LENGTH_SHORT).show();
        }
        else if(ph.equalsIgnoreCase("")||ph.length()!=10 || !onlyDigits(ph)){
            Toast.makeText(UserProfile.this,"Invalid phone number",Toast.LENGTH_SHORT).show();
        }
        else if(ema.equalsIgnoreCase("")){
            Toast.makeText(UserProfile.this,"Empty email ID",Toast.LENGTH_SHORT).show();
        }
        else if(pass.equalsIgnoreCase("") || pass.length()<8){
            Toast.makeText(UserProfile.this,"Invalid password",Toast.LENGTH_SHORT).show();
        }
        else{
            ProgressDialog progressDialog=new ProgressDialog(UserProfile.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/updateUser.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if(response.equalsIgnoreCase("0")){
                                Toast.makeText(UserProfile.this,"Username / EmailID already exists",Toast.LENGTH_SHORT).show();
                            }
                            else if(response.equalsIgnoreCase("-1")){
                                Toast.makeText(UserProfile.this,"Connection failed",Toast.LENGTH_SHORT).show();
                            }
                            else{
                               // Toast.makeText(UserProfile.this,response,Toast.LENGTH_SHORT).show();
                                curUser.setPassword(pass);
                                uname.setText(us);
                                fname.setText(ful);
                                email.setText(ema);
                                phone.setText(ph);
                                popup.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfile.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> toSend=new HashMap<String,String>();
                    toSend.put("id",Integer.toString(curUser.getID()));
                    toSend.put("name",ful);
                    toSend.put("username",us);
                    toSend.put("phone",ph);
                    toSend.put("password",pass);
                    toSend.put("emailId",ema);
                    return toSend;
                }
            };
            RequestQueue req= Volley.newRequestQueue(UserProfile.this);
            req.add(con);
        }
    }
    public void logout(){
        Intent back=new Intent(UserProfile.this,LoginActivity.class);
        startActivity(back);
    }
    public boolean onlyDigits (String s){
        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i)) < '0' || (s.charAt(i)) > '9' || s.charAt(0) == '0') {
                return false;
            }
        }
        return true;
    }
}
