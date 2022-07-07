package com.protocol.student;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("student")
public class StudentController {
    @Value("${server.port:0000}")
    private int port;

    @GetMapping("list")
    public ResponseEntity<List<Student>> getAllStudent() {
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student("Hung", "10A-1", port));
        studentList.add(new Student("Thu", "12A-1", port));
        studentList.add(new Student("Sau", "11A-2", port));
        return ResponseEntity.ok().body(studentList);
    }

}
