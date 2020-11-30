package com.minew.trackerfinderdemo.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minew.trackerfinderdemo.tag.BindDevice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author boyce
 * @date 2018/5/15 8:52
 */
public class Tools {

    public static final String BIND_DEVICE = "bind_device";
    public static final String MAC         = "mac";
    public static boolean isScan;

    public static List<BindDevice> getBindDevices(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = preferences.getString(BIND_DEVICE, "");
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        Type type = new TypeToken<List<BindDevice>>() {
        }.getType();
        ArrayList<BindDevice> list = gson.fromJson(jsonString, type);
        return list;
    }

    public static void saveBindDevice(Context context, BindDevice bindDevice) {
        List<BindDevice> bindDevices = getBindDevices(context);
        if (bindDevices == null) {
            bindDevices = new ArrayList<>();
        }
        boolean hasBind = false;
        for (int i = 0; i < bindDevices.size(); i++) {
            if (bindDevices.get(i).getMacAddress().equals(bindDevice.getMacAddress())) {
                hasBind = true;
                bindDevices.set(i, bindDevice);
            }
        }
        if (!hasBind) {
            bindDevices.add(bindDevice);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        String bindString = gson.toJson(bindDevices);
        preferences.edit().putString(BIND_DEVICE, bindString).commit();
    }

    public static void removeBindDevice(Context context, BindDevice bindDevice) {
        List<BindDevice> bindDevices = getBindDevices(context);
        if (bindDevices == null) {
            bindDevices = new ArrayList<>();
        }
        boolean hasBind = false;
        for (int i = 0; i < bindDevices.size(); i++) {
            if (bindDevices.get(i).getMacAddress().equals(bindDevice.getMacAddress())) {
                hasBind = true;
                bindDevices.set(i, bindDevice);
            }
        }
//        if (!hasBind) {
//            bindDevices.add(bindDevice);
//        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//                .create();
//        String bindString = gson.toJson(bindDevices);
        preferences.edit().remove(BIND_DEVICE).commit();
    }


}
