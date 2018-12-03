/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.aispeech.speech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.aispeech.util.AIPermissionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity implements AdapterView.OnItemClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = "Main";

    private AIPermissionRequest mPermissionRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //实例化动态权限申请类
        mPermissionRequest = new AIPermissionRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestMulti();//所有权限一同申请
            Log.d(TAG, "request all needed　permissions");
        }
        ListView listView = (ListView) findViewById(R.id.activity_list);
        ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();

        item = new HashMap<String, Object>();
        item.put("activity_name", "授权");
        item.put("activity_class", AuthActivity.class);
        listItems.add(item);
        
       /* item = new HashMap<String, Object>();
        item.put("activity_name", "云端语音识别");
        item.put("activity_class", CloudASR.class);
        listItems.add(item);
        
        item = new HashMap<String, Object>();
        item.put("activity_name", "本地语法编译");
        item.put("activity_class", LocalGrammar.class);
        listItems.add(item);*/

        item = new HashMap<String, Object>();
        item.put("activity_name", "本地语法编译");
        item.put("activity_class", LocalGrammar.class);
        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put("activity_name", "本地合成");
        item.put("activity_class", LocalTTS.class);
        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put("activity_name", "本地唤醒DNN");
        item.put("activity_class", LocalWakeUpDnn.class);
        listItems.add(item);

        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[] { "activity_name" }, new int[] { R.id.text_item });

        listView.setAdapter(adapter);
        listView.setDividerHeight(2);

        listView.setOnItemClickListener(this);
        
    }

    /**
     * 请求多个权限
     *
     */
    public void requestMulti() {
        mPermissionRequest.requestMultiPermissions(this, mPermissionGrant);
    }

    private AIPermissionRequest.PermissionGrant mPermissionGrant = new AIPermissionRequest.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case AIPermissionRequest.CODE_READ_CONTACTS:
                    Toast.makeText(Main.this, "Result Permission Grant CODE_READ_CONTACTS", Toast.LENGTH_SHORT).show();
                    break;
                case AIPermissionRequest.CODE_RECORD_AUDIO:
                    Toast.makeText(Main.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case AIPermissionRequest.CODE_READ_PHONE_STATE:
                    Toast.makeText(Main.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case AIPermissionRequest.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(Main.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case AIPermissionRequest.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(Main.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mPermissionRequest.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Map<?, ?> map = (HashMap<?, ?>) parent.getAdapter().getItem(position);
        Class<?> clazz = (Class<?>) map.get("activity_class");
        Intent it = new Intent(this, clazz);
        this.startActivity(it);
    }

}
