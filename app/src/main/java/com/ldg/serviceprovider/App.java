package com.ldg.serviceprovider;

import android.app.Application;
import android.content.Context;

import com.ldg.api.ApiOne;
import com.ldg.testone.ApiOneImpl;
import com.ldg.testtwo.ApiTwoImpl;

/**
 * created by gui 2020/11/13
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
