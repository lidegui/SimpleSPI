package com.ldg.testtwo;

import com.ldg.api.ApiTwo;
import com.ldg.serviceprovider.ServiceDeclare;

/**
 * created by gui 2020/11/13
 */
@ServiceDeclare(provider = ApiTwo.class, singleton = true)
public class ApiTwoImpl implements ApiTwo {

    private int count = 0;

    @Override
    public String getMsg() {
        count++;
        return "消息Two（我是单例）" + count;
    }
}
