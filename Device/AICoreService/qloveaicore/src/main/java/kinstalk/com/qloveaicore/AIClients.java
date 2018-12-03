/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.qloveaicore;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.Tuple3;

/**
 * Created by majorxia on 2017/4/10.
 * This class manages the CoreService's clients
 */

public class AIClients {
    private static final String TAG = "AI-AIClients";
    private final Map<String, ClientInfo> mTypeMap
            = Collections.synchronizedMap(new HashMap<String, ClientInfo>());
    private static AIClients sInstance;

    public synchronized static AIClients getInstance() {
        if (sInstance == null) {
            sInstance = new AIClients();
        }
        return sInstance;
    }

    public static Pair<String, ComponentName> getComponentInfoFromJson(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            String type = json.optString(AICoreDef.AI_JSON_FIELD_TYPE);
            String pkgName = json.optString(AICoreDef.AI_JSON_FIELD_PACKAGE);
            String svcName = json.optString(AICoreDef.AI_JSON_FIELD_SERVICECLASS);
            return new Pair(type, new ComponentName(pkgName, svcName));
        } catch (Exception e) {
            return null;
        }
    }

    public static Tuple3<String, ComponentName, Boolean> getInfoFromJson(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            String type = json.optString(AICoreDef.AI_JSON_FIELD_TYPE);
            String pkgName = json.optString(AICoreDef.AI_JSON_FIELD_PACKAGE);
            String svcName = json.optString(AICoreDef.AI_JSON_FIELD_SERVICECLASS);
            Boolean def = json.optBoolean(AICoreDef.AI_JSON_FIELD_DEFAULTCLIENT);
            return new Tuple3<>(type, new ComponentName(pkgName, svcName), def);
        } catch (Exception e) {
            return null;
        }
    }

    public static class ClientInfo {
        public ClientInfo(ComponentName componentName, ServiceConnection connection, IBinder b, ICmdCallback cb, boolean d) {
            this.component = componentName;
            this.connection = connection;
            this.ibinder = b;
            this.callback = cb;
            this.isdefault = d;

            if(ibinder != null) {
                mDeathRecipient = new IBinder.DeathRecipient() {

                    @Override
                    public void binderDied() {
                        QAILog.d(TAG, "binderDied:  componentName: " + component);
                        // 重新绑定远程服务
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(component);
                            QAIApplication.getInstance().getApplicationContext().startService(intent);
                            ibinder.unlinkToDeath(this, 0);
                            if (callback == null)
                                return;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {
                    QAILog.d(TAG, "linkToDeath: componentName: " + componentName);
                    ibinder.linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public ComponentName component;
        private ServiceConnection connection;
        private IBinder ibinder;
        public ICmdCallback callback;
        private boolean isdefault;
        private IBinder.DeathRecipient mDeathRecipient;

        @Override
        public String toString() {
            return "ClientInfo{" +
                    "component=" + component +
                    ", connection=" + connection +
                    ", ibinder=" + ibinder +
                    ", callback=" + callback +
                    ", isdefault=" + isdefault +
                    '}';
        }
    }

    public int addNewClient(String type, ComponentName component, ServiceConnection connection, IBinder binder, ICmdCallback cb, boolean def) {
        QAILog.d(TAG, "addNewClient() called with: type = [" + type + "], component = [" + component + "], def = [" + def + "]");
        ClientInfo ci = new ClientInfo(component, connection, binder, cb, def);
        mTypeMap.put(type, ci);
//        QAILog.d(TAG, "addNewClient: " + dump());
        return 0;
    }

    public int removeClientByType(String type) {
        QAILog.d(TAG, "removeClientByType: Enter");
        mTypeMap.remove(type);
        return 0;
    }

    public ClientInfo getClientInfoByType(String type) {
        QAILog.d(TAG, "getClientInfoByType: Enter," + type);
        ClientInfo ci = mTypeMap.get(type);
        if (ci == null) {
            for (ClientInfo i : mTypeMap.values()) {
                if (i.isdefault) {
                    ci = i;
                    break;
                }
            }
        }
        QAILog.d(TAG, "getClientInfoByType: ci:" + ci);
        return ci;
    }

    private void removeTypeMapEntryByComponent(ComponentName cn) {
        Iterator<Map.Entry<String, ClientInfo>> it =
                mTypeMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ClientInfo> entry = it.next();
            if (entry.getValue().component.equals(cn))
                it.remove();
        }
    }

    public int onClientUnRegister(String jsonParam) {
        QAILog.d(TAG, "onClientUnRegister: enter," + jsonParam);

        if (!TextUtils.isEmpty(jsonParam)) {
            try {
                JSONObject json = new JSONObject(jsonParam);

                String type = json.optString(AICoreDef.AI_JSON_FIELD_TYPE);
                String pkgName = json.optString(AICoreDef.AI_JSON_FIELD_PACKAGE);
                String svcName = json.optString(AICoreDef.AI_JSON_FIELD_SERVICECLASS);

                ComponentName cn = new ComponentName(pkgName, svcName);
                removeTypeMapEntryByComponent(cn);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        QAILog.d(TAG, "onClientUnRegister: this:" + dump());
        return 0;
    }

    private String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nClients: Components {");
        for (Map.Entry<String, ClientInfo> entry : mTypeMap.entrySet()) {
            sb.append(entry.getKey() + ", " + entry.getValue().component
                    + ", " + entry.getValue().callback + ";\n");
        }
        sb.append(" }\nBinders {");

        return sb.toString();
    }
}
