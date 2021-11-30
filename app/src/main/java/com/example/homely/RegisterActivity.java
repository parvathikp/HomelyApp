package com.example.homely;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText nameField;
    EditText emailField;
    EditText passwordField ;
    Button submit;
    EditText fullfield ;
    EditText phoneno ;
    String name, email, password, fullname, phone;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameField = findViewById(R.id.userName);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        fullfield = findViewById(R.id.full);
        phoneno = findViewById(R.id.phone);
        login=findViewById(R.id.login);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=nameField.getText().toString().trim();
                email=emailField.getText().toString().trim();
                password=passwordField.getText().toString().trim();
                fullname=fullfield.getText().toString().trim();
                phone=phoneno.getText().toString().trim();
                if(name.equalsIgnoreCase("")){
                    Toast.makeText(RegisterActivity.this,"Empty userName",Toast.LENGTH_SHORT).show();
                }
                else if(fullname.equalsIgnoreCase("")){
                    Toast.makeText(RegisterActivity.this,"Empty Full name",Toast.LENGTH_SHORT).show();
                }
                else if(phone.equalsIgnoreCase("")||phone.length()!=10 || !onlyDigits(phone)){
                    Toast.makeText(RegisterActivity.this,"Invalid phone number",Toast.LENGTH_SHORT).show();
                }
                else if(email.equalsIgnoreCase("") || !(email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))){
                    Toast.makeText(RegisterActivity.this,"Invalid email ID",Toast.LENGTH_SHORT).show();
                }
                else if(password.equalsIgnoreCase("") || password.length()<8){
                    Toast.makeText(RegisterActivity.this,"Invalid password",Toast.LENGTH_SHORT).show();
                }
                else{
                    ProgressDialog progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/registerUser.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String res) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject=new JSONObject(res);
                                        String response=jsonObject.getString("message");
                                        if(response.equalsIgnoreCase("0")){
                                            Toast.makeText(RegisterActivity.this,"Username already exists",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(response.equalsIgnoreCase("1")){
                                            Toast.makeText(RegisterActivity.this,"Email ID already exists",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(response.equalsIgnoreCase("-1")){
                                            Toast.makeText(RegisterActivity.this,"Connection failed",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(RegisterActivity.this,"Successfully registered",Toast.LENGTH_SHORT).show();
                                            int id=jsonObject.getInt("id");
                                            user curUser=new user(id,name,fullname,phone,email,password);
                                            Gson gson=new Gson();
                                            String tosend=gson.toJson(curUser);
                                            Intent toUser=new Intent(RegisterActivity.this,UserProfile.class);
                                            toUser.putExtra("curUser",tosend);
                                            startActivity(toUser);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> toSend=new HashMap<String,String>();
                            toSend.put("name",fullname);
                            toSend.put("username",name);
                            toSend.put("phone",phone);
                            toSend.put("password",password);
                            toSend.put("emailId",email);
                            return toSend;
                        }
                    };
                    RequestQueue req= Volley.newRequestQueue(RegisterActivity.this);
                    req.add(con);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginStart=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginStart);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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