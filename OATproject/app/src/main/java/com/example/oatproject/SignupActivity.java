package com.example.oatproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private View sign_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sb).setOnClickListener(onClickListener);
        sign_up=findViewById(R.id.clear);
        findViewById(R.id.checking).setOnClickListener(onClickListener2);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // 사용자가 현재 로그인되어있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            currentUser.reload();
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sb:
                    signup();
                    break;
            }
        }
    };
    View.OnClickListener onClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checking:
                    checkings();
                    break;
            }
        }
    };
    private void sendVerifi(){
        //링크 전송
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> verifitask) {
                if(verifitask.isSuccessful()){
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(SignupActivity.this,
                            "이메일인증링크가" + user.getEmail()+"로 전송되었습니다.",
                            Toast.LENGTH_SHORT).show();

                }
                else{
                    Log.e(TAG, "sendEmailVerification", verifitask.getException());
                    Toast.makeText(SignupActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void signup(){
        //초기 회원가입처리

        String email = ((EditText)findViewById(R.id.ID)).getText().toString();

        String password = ((EditText)findViewById(R.id.pw)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.pwc)).getText().toString();
        //유효성검사후 신규사용자를 만드는걸 허락


        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0) {
            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendVerifi();//회원가입 동시에 그 이메일로 링크 전송

                                } else {
                                    if (task.getException() != null) {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        startToast(task.getException().toString());

                                    }

                                }
                            }


                        });
            } else {
                startToast("비밀번호가 일치하지않습니다");
            }
        }
        else{
            startToast("이메일 또는 비밀번호를 입력해주세요");

        }




    }

    private void checkings() {
        //인증결과 확인 후 회원가입 처리

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                if (user.isEmailVerified() == true) {
                    Toast.makeText(SignupActivity.this, "이메일인증완료", Toast.LENGTH_SHORT).show();
                    startToast("회원가입 성공");
                    sign_up.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(SignupActivity.this, "이메일인증실패", Toast.LENGTH_SHORT).show();
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            }
                            else{
                                Toast.makeText(SignupActivity.this  , "오류", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }







    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}

