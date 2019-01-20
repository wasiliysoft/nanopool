package ru.wasiliysoft.zcashnanopoolorg.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.TreeMap;

/**
 * Created by WasiliySoft on 02.12.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Miners {
    private SharedPreferences sp;
    private static final String PREF_MINERS = "PREF_MINERS";
    private static TreeMap<String, Miner> sMiners;


    public Miners(Context c) {
        sp = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);
//        sp.edit().clear().apply();
        String s = sp.getString(PREF_MINERS, "");
        if (s.isEmpty()) {
            sMiners = new TreeMap<>();
        } else {
            sMiners = new Gson().fromJson(s, (new TypeToken<TreeMap<String, Miner>>() {
            }.getType()));
        }
    }


    public void add(Miner miner) {
        sMiners.put(miner.getName(), miner);
        save();
    }

    public void delete(String name) {
        sMiners.remove(name);
        save();
    }

    private void save() {
        sp.edit().putString("PREF_MINERS", new Gson().toJson(sMiners)).apply();
    }

    public TreeMap<String, Miner> read() {
        return sMiners;
    }

}

