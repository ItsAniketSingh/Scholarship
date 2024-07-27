package com.example.scholarship.controller;

import com.example.scholarship.model.Student;
import com.example.scholarship.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "Student API")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload a CSV file to process student data")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            studentService.processCsv(file);
            return ResponseEntity.ok("File processing started");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File processing failed: " + e.getMessage());
        }
    }

    @GetMapping("/{rollNumber}")
    @ApiOperation(value = "Get student eligibility by roll number")
    public ResponseEntity<Student> getStudentByRollNumber(@PathVariable String rollNumber) {
        Student student = studentService.getStudentByRollNumber(rollNumber);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.status(404).body(null);
    }
}
