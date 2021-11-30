package com.example.homely;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText emailField,passwordField;
    String email,pass;
    Button submit;
    TextView reg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailField=findViewById(R.id.email);
        passwordField=findViewById(R.id.password);
        submit=findViewById(R.id.submit);
        reg=findViewById(R.id.register);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailField.getText().toString().trim();
                pass=passwordField.getText().toString().trim();
                if(email.equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this,"Empty email ID",Toast.LENGTH_SHORT).show();
                }
                else if(pass.equalsIgnoreCase("") || pass.length()<8){
                    Toast.makeText(LoginActivity.this,"Invalid password",Toast.LENGTH_SHORT).show();
                }
                else{
                    ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    StringRequest con=new StringRequest(Request.Method.POST, "https://wayless-editor.000webhostapp.com/loginUser.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String res) {
                                    progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(res);
                                        String response=jsonObject.getString("message");
                                        if(response.equalsIgnoreCase("0")){
                                            Toast.makeText(LoginActivity.this,"Incorrect username/password",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this,"Successfully logged in",Toast.LENGTH_SHORT).show();
                                            int id=jsonObject.getInt("id");
                                            String uname=jsonObject.getString("username");
                                            String name=jsonObject.getString("name");
                                            String phone=jsonObject.getString("phone");
                                            user curUser=new user(id,uname,name,phone,email,pass);
                                            Gson gson=new Gson();
                                            String tosend=gson.toJson(curUser);
                                            Intent toUser=new Intent(LoginActivity.this,UserProfile.class);
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
                            Toast.makeText(LoginActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> toSend=new HashMap<String,String>();
                            toSend.put("password",pass);
                            toSend.put("emailId",email);
                            return toSend;
                        }
                    };
                    RequestQueue req= Volley.newRequestQueue(LoginActivity.this);
                    req.add(con);
                }
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regStart=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regStart);
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
}
