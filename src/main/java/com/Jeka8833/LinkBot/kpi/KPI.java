package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.Util;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class KPI {

    public List<Lesson> data;

    private static final String url = "https://api.rozklad.org.ua/v2/groups/%D0%B4%D0%BF-01/lessons";
    private static final Gson gson = new Gson();
    private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Kiev"));

    public static List<Lesson> lessons;

    public static void init() {
        lessons = gson.fromJson(Util.readSite(url), KPI.class).data;
    }

    public static int getWeek(final int maxCount, final int move) {
        return (int) (((calendar.getTimeInMillis() / (1000 * 60 * 60 * 24 * 7)) + move) % maxCount);
    }

    public static @Nullable List<Lesson> getDayLessons(final int week) {
        final List<Lesson> lessons = new ArrayList<>();
        final int dayNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        // Sunday
        if (dayNumber < 0)
            return null;
        for (Lesson lesson : KPI.lessons)
            if (lesson.lesson_week == week + 1 && lesson.day_number == dayNumber)
                lessons.add(lesson);
        lessons.sort(null);
        return lessons;
    }

    public static @Nullable Lesson getCurrentLesson(final int week) {
        final List<Lesson> lessons = getDayLessons(week);
        if (lessons == null)
            return null;
        final int currentTime = getCurrentTimeInSecond();
        for (Lesson lesson : lessons)
            if (lesson.timeToEnd() > currentTime)
                return lesson;
        return null;
    }

    public static int getCurrentTimeInSecond() {
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }
}
