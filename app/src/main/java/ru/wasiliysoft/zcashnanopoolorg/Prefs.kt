package ru.wasiliysoft.zcashnanopoolorg

import android.content.Context


// Created by WasiliySoft on 21.01.2019.
class Prefs(context: Context) {
    private val prefFileName = "prefs"
    private val PREF_PAGE_ID = "PAGE_ID"

    private val sp = context.applicationContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    var savedPageId: Int
        get() = sp.getInt(PREF_PAGE_ID, 0)
        set(i: Int) {
            sp.edit().putInt(PREF_PAGE_ID, i).apply()
        }
}