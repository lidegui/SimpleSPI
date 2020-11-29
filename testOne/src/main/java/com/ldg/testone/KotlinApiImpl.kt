package com.ldg.testone

import com.ldg.api.KotlinApi
import com.ldg.serviceprovider.ServiceDeclare

/**
 *
 * created by gui 2020/11/29
 *
 */
@ServiceDeclare(provider = KotlinApi::class, singleton = true)
class KotlinApiImpl : KotlinApi {
    companion object {
        var add: Int = 0
    }

    override fun getString(): String {
        add++;
        return "" + add
    }
}