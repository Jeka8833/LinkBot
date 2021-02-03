package com.Jeka8833.LinkBot.kpi;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LessonCreator {

    public static void main(String[] args) {
        final Gson gson = new Gson();
        final List<Lesson> lessonList = new ArrayList<>();
        try {
            String[] text = Files.readString(Paths.get("D:\\download\\new 3.txt")).split("\r\n");
            for (int i = 0; i < 31; i++) {
                final Lesson lesson = new Lesson();
                lesson.lesson_id = Integer.parseInt(text[i * 9 + 0]);
                lesson.day_number = Integer.parseInt(text[i * 9 + 1]);
                lesson.lesson_name = (text[i * 9 + 2]);
                lesson.lesson_number = Integer.parseInt(text[i * 9 + 3]);
                lesson.lesson_type = (text[i * 9 + 4]);
                lesson.teacher_name = (text[i * 9 + 5]);
                lesson.lesson_week = Integer.parseInt(text[i * 9 + 6]);
                lesson.time_start = (text[i * 9 + 7]);
                lesson.time_end = (text[i * 9 + 8]);

                lessonList.add(lesson);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.writeString(Paths.get("D:\\download\\out 3.txt"), gson.toJson(lessonList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
