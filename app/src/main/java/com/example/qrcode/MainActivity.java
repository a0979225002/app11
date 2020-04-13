package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    123);
        }else {
            init();
        }
    }

    private void init(){
        webView = findViewById(R.id.webview);
        initWebView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void initWebView(){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new MyJS(),"brad");
        //讓javascript可以觸發new MyJS()方法
        //然後讓他觸發new MyJS 內的fromJS方法
        //javascript內的function{brad.fromJS()}即可觸發

        webView.loadUrl("file:///android_asset/brad.html");
    }

    public class MyJS{
        @JavascriptInterface
        public void fromJS(){
            gotoScane();
        }
    }

//    點擊按鈕觸發的功能
    private void gotoScane(){
        //移動到ScanActivity的拍照程式中
        Intent intent = new Intent(this,ScanActivity.class);
        //與startActivity差別在startActivity只能單方面傳輸值a傳給->b
        //startActivityForResult可以更新傳輸 a傳給->b b又能將新的資料傳回給a
        startActivityForResult(intent,321);
    }
//要回來的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //requestCode = startActivityForResult(intent,321); => 321
        //resultCode = ScanActivity.class->setResult(RESULT_OK)

        if (requestCode ==321 && resultCode == RESULT_OK){
            //拿取ScanActivity intent過來的值,傳給javascript:showCode()方法
            webView.loadUrl(
                    //給予key->"code",取得Intent().putExtra的value
                    String.format("javascript:showCode('%s')",data.getStringExtra("code")));
        }
    }
}
