package com.ldg.client;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ldg.api.ApiOne;
import com.ldg.api.ApiTwo;
import com.ldg.serviceprovider.ServiceManager;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tvMsg);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(getMsg(count));
            }
        });
    }

    public String getMsg(int count) {
        return "第" + count + "次点击结果：" +
                "\n" +
                "moduleOne:" + ServiceManager.get(ApiOne.class).getMsg()
                + "\n"
                + "moduleTwo:" + ServiceManager.get(ApiTwo.class).getMsg();
    }
}