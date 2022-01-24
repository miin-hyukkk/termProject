package com.example.oatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class loginsuccessMain extends AppCompatActivity {
    private String strNick,strProfileImage,strNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsuccess_main);

        Intent intent = getIntent();
        strNick = intent.getStringExtra("name");
        strProfileImage = intent.getStringExtra("profileImg");
        strNum = intent.getStringExtra("phoneNumber");

        TextView tv_nick=findViewById(R.id.PN);
        TextView tv_num=findViewById(R.id.Num);
        ImageView iv_profile = findViewById(R.id.Pimage);

        tv_nick.setText(strNick);
        tv_num.setText(strNum);

        Glide.with(this).load(strProfileImage).into(iv_profile);



    }
}