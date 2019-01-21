package ru.wasiliysoft.zcashnanopoolorg.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by WasiliySoft on 02.12.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Miners {
    private SharedPreferences sp;
    private static final String PREF_MINERS = "PREF_MINERS";
    private static ArrayList<Miner> sMiners;

    public Miners(Context c) {
        sp = c.getSharedPreferences("miners", Context.MODE_PRIVATE);
        String s = sp.getString(PREF_MINERS, "");
        if (s.isEmpty()) {
            sMiners = new ArrayList<>();
        } else {
            sMiners = new Gson().fromJson(s, (new TypeToken<ArrayList<Miner>>() {
            }.getType()));
        }
    }


    public void add(Miner miner) {
        sMiners.add(miner);
        save();
    }

    public void delete(int i) {
        sMiners.remove(i);
        save();
    }

    private void save() {
        sp.edit().putString(PREF_MINERS, new Gson().toJson(sMiners)).apply();
    }

    public ArrayList<Miner> read() {
        return sMiners;
    }

}

