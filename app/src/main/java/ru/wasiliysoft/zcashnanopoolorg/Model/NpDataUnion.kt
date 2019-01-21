package ru.wasiliysoft.zcashnanopoolorg.Model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


// Created by WasiliySoft on 21.01.2019.
class NpDataUnion(val general: NpGeneral.Data, var calc: NpCalc.Data) {
    fun toJson(): String {
        return Gson().toJson(this, object : TypeToken<NpDataUnion>() {}.type)
    }

    companion object {
        fun fromJson(json: String): NpDataUnion {
            return Gson().fromJson(json, object : TypeToken<NpDataUnion>() {}.type)
        }
    }

}