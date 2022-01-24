package com.example.oatproject;


import android.app.Application;
import android.content.Context;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 네이티브 앱 키로 초기화
        KakaoSdk.init(this, "19516a68e965371a0bd3fd30eb88a487");
    }
}