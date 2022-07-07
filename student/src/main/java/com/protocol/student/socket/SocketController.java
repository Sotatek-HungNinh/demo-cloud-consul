//package com.protocol.student.socket;
//
//import com.protocol.student.Student;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//public class SocketController {
//    @MessageMapping("/student")
//    @SendTo("/topic/student")
//    public ResponseEntity<List<Student>> getAllStudent() {
//        List<Student> studentList = new ArrayList<>();
//        studentList.add(new Student("Hung", "10A-1", 15));
//        studentList.add(new Student("Thu", "12A-1", 14));
//        studentList.add(new Student("Sau", "11A-2", 10));
//        return ResponseEntity.ok().body(studentList);
//    }
//}
