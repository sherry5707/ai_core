package kinstalk.com.qloveaicore.qlovenlp.utils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kinstalk.com.common.utils.QAILog;

public class ReflectionUtils {

    public static final String TAG = ReflectionUtils.class.getSimpleName();

    public static class ClassInfo {
        public List<Field> fields;
    }

    private static Map<String, ClassInfo> mClassInfoMap = new HashMap<String, ClassInfo>();

    public static ClassInfo getClassInfo(Class<?> clazz) {
        ClassInfo classInfo = mClassInfoMap.get(clazz.getName());
        if (classInfo == null) {
            classInfo = new ClassInfo();
            classInfo.fields = getFields(clazz);
            mClassInfoMap.put(clazz.getName(), classInfo);
        }
        return classInfo;
    }

    public static JSONObject convObjectToJSON(Object object) {
        JSONObject result = new JSONObject();

        for (Field field : getClassInfo(object.getClass()).fields) {
            Serializable annotation = field.getAnnotation(Serializable.class);
            if (annotation != null && !TextUtils.isEmpty(annotation.name())) {
                String paramName = annotation.name();
                field.setAccessible(true);
                try {
                    Object value = field.get(object);
                    if (value != null) {
                        if (value instanceof Iterable<?>) {
                            Iterable<?> iterable = (Iterable<?>) value;
                            JSONArray array = new JSONArray();
                            for (Object obj : iterable) {
                                annotation = obj.getClass().getAnnotation(Serializable.class);
                                if (annotation != null) {
                                    array.put(convObjectToJSON(obj));
                                } else {
                                    array.put(obj);
                                }
                            }
                            result.put(paramName, array);
                        } else {
                            annotation = field.getType().getAnnotation(Serializable.class);
                            if (annotation != null) {
                                result.put(paramName, convObjectToJSON(value));
                            } else {
                                result.put(paramName, value);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

        QAILog.d(TAG, "convObjectToJSON result: " + result);
        return result;
    }

    private final static List<Field> getFields(Class<?> cls) {
        List<Field> list = new ArrayList<Field>();

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            list.add(field);
        }

        Class<?> superClass = (Class<?>) cls.getGenericSuperclass();
        if (superClass != null)
            list.addAll(getFields(superClass));

        return list;
    }
}