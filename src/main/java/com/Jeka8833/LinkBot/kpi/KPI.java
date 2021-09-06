package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.MySQL;
import com.Jeka8833.LinkBot.Util;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class KPI {

    private static final Gson gson = new Gson();
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Kiev");
    public static Calendar calendar = Calendar.getInstance(TIME_ZONE);

    public static List<Lesson> lessons;

    public static void init() {
        lessons = Arrays.asList(gson.fromJson(SavedBD.data, Lesson[].class));
    }

    private static void updateTime() {
        calendar = Calendar.getInstance(TIME_ZONE);
    }

    public static int getTimeInSecond() {
        updateTime();
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }

    public static int getWeek() {
        updateTime();
        return (calendar.get(Calendar.WEEK_OF_YEAR) + MySQL.shiftWeek) % 2;
    }

    public static int getDay() {
        updateTime();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getCurrentLessonNumber() {
        final long time = getTimeInSecond();
        if (time < Util.parseTime("10:05:00"))
            return 1;
        if (time < Util.parseTime("12:00:00"))
            return 2;
        if (time < Util.parseTime("13:55:00"))
            return 3;
        if (time < Util.parseTime("15:50:00"))
            return 4;
        if (time < Util.parseTime("17:45:00"))
            return 5;
        if (time < Util.parseTime("20:05:00"))
            return 6;
        return 7;
    }

    public static @NotNull List<Lesson> getDayLessons(final int week, final int day) {
        final List<Lesson> lessons = new ArrayList<>();
        for (Lesson lesson : KPI.lessons)
            if (lesson.lesson_week == week + 1 && lesson.day_number == day)
                lessons.add(lesson);
        lessons.sort(null);
        return lessons;
    }

    public static @NotNull List<Lesson> getDayLessons() {
        return getDayLessons(getWeek(), getDay());
    }

    public static @NotNull List<Lesson> getCurrentLessons() {
        final int currentLessonNumber = getCurrentLessonNumber();
        return getDayLessons().stream()
                .filter(lesson -> lesson.lesson_number == currentLessonNumber).collect(Collectors.toList());
    }

    public static @NotNull List<Lesson> getNextLesson() {
        final List<Lesson> dayLesson = getDayLessons();
        for (int i = getCurrentLessonNumber() + 1; i <= 7; i++) {
            int finalI = i;
            final List<Lesson> out = dayLesson.stream()
                    .filter(lesson -> lesson.lesson_number == finalI).collect(Collectors.toList());
            if (!out.isEmpty())
                return out;
        }
        return new ArrayList<>();
    }
}
