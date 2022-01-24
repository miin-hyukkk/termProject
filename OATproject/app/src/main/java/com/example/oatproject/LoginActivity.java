package com.example.oatproject;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.facebook.appevents.AppEventsLogger;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private View login_K, logout_K;
    private ImageView profileImage;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AppEventsLogger.activateApp(getApplication());
        login_K = findViewById(R.id.loginK);//카카오로그인
        logout_K = findViewById(R.id.logoutK);
        profileImage = findViewById(R.id.profile);


        mAuth = FirebaseAuth.getInstance();//파이어베이스어스객체의 공유 인스턴스를 가져옴
        findViewById(R.id.loginB).setOnClickListener(onClickListener);
        findViewById(R.id.sb).setOnClickListener(onClickListener);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.loginF);
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        Function2<OAuthToken, Throwable, Unit>callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //unit은 반환값이 없음을 나타내는 타입
                if (oAuthToken != null) {
                    //로그인이 성공한거임, 로그인이 되었을때 처리해야될 일들을 여기다가 이제 짜는거임
                }
                if (throwable != null) {
                    //실패
                }
                //콜백함수는 이렇게 로그인결과에따라 필요한 동작과 예외처리를 정의해두어야함
                LoginActivity.this.updateKakaoLoginUI();
                return null;
            }
            //interface의 이름은 FunctionN<N1,N2...N, R> 이며 invoke 함수 하나만을 abstract로 갖습니다
        };

        login_K.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    //설치되어있을때,카카오톡앱으로 로그인
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                    //로그인 결과를 처리하기위한 콜백을 function형태로 받는거임
                    //이 api가 호출되면 Android SDK가 카카오톡을 실행하고 사용자에게 앱 이용 관련 동의를 구하는 동의 화면을 출력합니다.
                    // API 호출 시 context와 결과 처리를 위한 콜백(Callback) 함수를 전달하여야 합니다.
                }
                else{
                    //카카오사이트로 로그인
                    Toast.makeText(LoginActivity.this, "?????????", Toast.LENGTH_SHORT).show();
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,callback);

                }
            }
        });

        logout_K.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUI();
                        return null;
                    }
                });
            }
        }));

        updateKakaoLoginUI();


    }

    @Override
    public void onStart() {
        super.onStart();
        // 사용자가 현재 로그인 되어있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
            currentUser.reload();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 페이스북 콜백 등록
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }






    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sb:
                    startSignupActivity();
                case R.id.loginB:
                    login();
                    break;
            }
        }
    };
    private void login() {
        String email = ((EditText) findViewById(R.id.ID2)).getText().toString();
        String password = ((EditText) findViewById(R.id.pw2)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공");
                                updateUI(user);
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                    updateUI(null);
                                    startToast("로그인 실패");

                                }

                            }
                        }


                    });

        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요");

        }


    }//파베관련
    private void startSignupActivity() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }//파베관련
    //파베관련
    private void updateUI(FirebaseUser user) { }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void updateKakaoLoginUI(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user != null){
                    //로그인상태
                    Log.i(TAG, "invoke: id" + user.getId());
                    Log.i(TAG, "invoke: email" + user.getKakaoAccount().getEmail());
                    Log.i(TAG, "invoke: nickname" + user.getKakaoAccount().getProfile().getNickname());
                    Log.i(TAG, "invoke: gender" + user.getKakaoAccount().getGender());
                    Log.i(TAG, "invoke: age" + user.getKakaoAccount().getAgeRange());
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage);
                    login_K.setVisibility(View.GONE);
                    logout_K.setVisibility(View.VISIBLE);

                }else{
                    //로그아웃상태
                    profileImage.setImageBitmap(null);
                    login_K.setVisibility(View.VISIBLE);
                    logout_K.setVisibility(View.GONE);
                }


                if (throwable != null) {
                    Log.w(TAG, "invoke: " + throwable.getLocalizedMessage());
                }

                return null;
            }
        });



    }


}

