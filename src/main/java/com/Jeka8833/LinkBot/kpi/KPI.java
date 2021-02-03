package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.google.gson.Gson;

import java.util.*;

public class KPI {

    private static final boolean localBD = true;

    public List<Lesson> data;

    private static final String url = "https://api.rozklad.org.ua/v2/groups/%D0%B4%D0%BF-01/lessons";
    private static final Gson gson = new Gson();
    public static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Kiev"));

    public static List<Lesson> lessons;

    public static void init() {
        if (localBD)
            lessons = Arrays.asList(gson.fromJson(SavedBD.data, Lesson[].class));
        else
            lessons = gson.fromJson(Util.readSite(url), KPI.class).data;
    }

    public static int getTimeInSecond() {
        updateTime();
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }

    private static void updateTime() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Kiev"));
    }

    public static int getWeek() {
        updateTime();
        return (calendar.get(Calendar.WEEK_OF_YEAR) + MySQL.shiftWeek) % 2;
    }

    public static int getDay() {
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static List<Lesson> getDayLessons(final int week, final int day) {
        final List<Lesson> lessons = new ArrayList<>();
        for (Lesson lesson : KPI.lessons)
            if (lesson.lesson_week == week + 1 && lesson.day_number == day)
                lessons.add(lesson);
        lessons.sort(null);
        return lessons;
    }

    public static List<Lesson> getDayLessons(final int week) {
        updateTime();
        final int dayNumber = getDay();
        // Sunday
        if (dayNumber < 0)
            return null;
        return getDayLessons(week, dayNumber);
    }

    public static Lesson getCurrentLesson(final int week) {
        final List<Lesson> lessons = getDayLessons(week);
        if (lessons == null)
            return null;
        final int currentTime = getTimeInSecond();
        for (Lesson lesson : lessons)
            if (lesson.timeToEnd() > currentTime)
                return lesson;
        return null;
    }

    public static Lesson getNextLesson(final int week) {
        final List<Lesson> lessons = getDayLessons(week);
        if (lessons == null)
            return null;
        final int currentTime = getTimeInSecond();
        for (int i = 0; i < lessons.size() - 1; i++)
            if (lessons.get(i).timeToEnd() > currentTime)
                return lessons.get(i + 1);
        return null;
    }
}
