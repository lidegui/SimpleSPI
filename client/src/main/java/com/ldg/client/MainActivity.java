package com.ldg.client;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ldg.api.ApiOne;
import com.ldg.api.ApiTwo;
import com.ldg.serviceprovider.ServiceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void msg(View view) {
        Log.d("ldg", "第一次：msg1: " + ServiceManager.get(ApiOne.class).getMsg()
                + "\tmsg2:" + ServiceManager.get(ApiTwo.class).getMsg());

        Log.d("ldg", "第二次：msg1: " + ServiceManager.get(ApiOne.class).getMsg()
                + "\tmsg2:" + ServiceManager.get(ApiTwo.class).getMsg());
    }
}