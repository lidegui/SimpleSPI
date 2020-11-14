package com.ldg.testone;

import com.ldg.api.ApiOne;
import com.ldg.serviceprovider.ServiceDeclare;

/**
 * created by gui 2020/11/13
 */
@ServiceDeclare(provider = ApiOne.class)
public class ApiOneImpl implements ApiOne {
    @Override
    public String getMsg() {
        return "消息One（我不是单例）";
    }
}
