package com.github.anniepank.hability.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.anniepank.hability.Reminder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedList;

/**
 * Created by anya on 11/6/16.
 */
public class Settings {
    public LinkedList<Habit> habits;
    public String cachedImageOfTheDayUrl;
    public String syncKey;
    public boolean webIntroClosed = false;

    private static final String NAME = "Habits";
    private static final String KEY = "Settings";
    private static boolean gotSettings = false;
    private static Settings global;

    public static Settings get(Context context) {
        if (!gotSettings) {
            global = load(context);
            gotSettings = true;
        }
        return global;
    }

    public void save(Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        SharedPreferences prefs = context.getSharedPreferences(NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY, json);
        // Log.i("habits", json);
        editor.apply();
        Reminder.scheduleNotifications(context);
    }

    private static Settings load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NAME, 0);
        String json = prefs.getString(KEY, null);
        if (json == null) {
            Settings settings = new Settings();
            settings.habits = new LinkedList<>();
            return settings;
        }

        return new Gson().fromJson(migrate(json), Settings.class);
    }

    private static String migrate(String fromJson) {
        JsonObject jo = (JsonObject) new JsonParser().parse(fromJson);

        for (JsonElement habitElement : jo.getAsJsonArray("habits")) {
            JsonObject habit = (JsonObject) habitElement;

            if (habit.has("habitName")) {
                habit.addProperty("name", habit.get("habitName").getAsString());
            }

            JsonArray days = habit.getAsJsonArray("days");

            if (days.size() > 0 && days.get(0).isJsonPrimitive()) {
                for (int i = 0; i < days.size(); i++) {
                    JsonObject habitDate = new JsonObject();
                    habitDate.addProperty("date", days.get(i).getAsLong());
                    habitDate.addProperty("updatedAt", System.currentTimeMillis());
                    days.set(i, habitDate);
                }
            }
        }

        return new Gson().toJson(jo);
    }

    public void deleteOldDates() {
        for (Habit habit : habits) {
            if (habit.deleted) habit.days = new LinkedList<HabitDate>();
        }
    }
}
