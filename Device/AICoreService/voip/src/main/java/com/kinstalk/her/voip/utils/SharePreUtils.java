package com.kinstalk.her.voip.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreUtils {


    public static SharedPreferences getSharePreferences(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor editor(Context context, String name) {
        return getSharePreferences(context, name).edit();
    }

    public static void edit(Context context, String name, EditorOpt editOpt) {
        SharedPreferences.Editor editor = editor(context, name);
        editOpt.onEdit(editor);
        editor.commit();
    }

    interface EditorOpt {
        void onEdit(SharedPreferences.Editor editor);
    }

}
