package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelVideoSkill {

    public String intentName;
    public ArrayList<Slot> slots;
    public HashMap<String, String> slotsMap;

    public ModelVideoSkill init() {
        slotsMap = new HashMap<>();
        if (slots != null)
            for (Slot slot : slots) {
                if (!TextUtils.isEmpty(slot.value))
                    slotsMap.put(slot.key, slot.value);
            }
        return this;
    }

    @Override
    public String toString() {
        return "ModelVideoSkill{" +
                "intentName='" + intentName + '\'' +
                ", slots=" + slots +
                ", slotsMap=" + slotsMap +
                '}';
    }

    private static class Slot {
        String key;
        String value;

        @Override
        public String toString() {
            return "Slot{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
