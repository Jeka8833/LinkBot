package com.Jeka8833.LinkBot.kpi;

public class Lesson implements Comparable<Lesson> {

    public int lesson_id;
    public int day_number;
    public String lesson_name;
    public int lesson_number;
    public String lesson_type;
    public String teacher_name;
    public int lesson_week;
    public String time_start;
    public String time_end;

    public int timeToStart() {
        final String[] arg = time_start.split(":");
        return Integer.parseInt(arg[0]) * 60 * 60 + Integer.parseInt(arg[1]) * 60 + Integer.parseInt(arg[2]);
    }

    public int timeToEnd() {
        final String[] arg = time_end.split(":");
        return Integer.parseInt(arg[0]) * 60 * 60 + Integer.parseInt(arg[1]) * 60 + Integer.parseInt(arg[2]);
    }

    @Override
    public int compareTo(Lesson o) {
        return lesson_number;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lesson_id=" + lesson_id +
                ", day_number=" + day_number +
                ", lesson_name='" + lesson_name + '\'' +
                ", lesson_number=" + lesson_number +
                ", lesson_type='" + lesson_type + '\'' +
                ", teacher_name='" + teacher_name + '\'' +
                ", lesson_week=" + lesson_week +
                ", time_start='" + time_start + '\'' +
                ", time_end='" + time_end + '\'' +
                '}';
    }
}
