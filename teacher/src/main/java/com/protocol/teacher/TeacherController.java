package com.protocol.teacher;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("teacher")
public class TeacherController {
    @GetMapping("list")
    public ResponseEntity<List<Teacher>> getAllTeacher() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher("Haha", 30));
        teachers.add(new Teacher("Alaska", 35));
        teachers.add(new Teacher("Voldermon", 40));
        return ResponseEntity.ok().body(teachers);
    }
}
