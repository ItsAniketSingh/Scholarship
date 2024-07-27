package com.example.scholarship.service;

import com.example.scholarship.model.Student;
import com.example.scholarship.repository.StudentRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final int scienceThreshold = 85;
    private final int mathsThreshold = 90;
    private final int computerThreshold = 95;
    private final int englishThreshold = 75;

    public void processCsv(MultipartFile file) throws IOException {
        logger.info("Starting CSV processing for file: {}", file.getOriginalFilename());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<CSVRecord> records = csvParser.getRecords();
            executor.submit(() -> {
                for (CSVRecord record : records) {
                    processRecord(record);
                }
            });
        }

        logger.info("CSV processing initiated for file: {}", file.getOriginalFilename());
    }

    private void processRecord(CSVRecord record) {
        String rollNumber = record.get("roll number");
        String name = record.get("student name");
        int science = Integer.parseInt(record.get("science"));
        int maths = Integer.parseInt(record.get("maths"));
        int english = Integer.parseInt(record.get("english"));
        int computer = Integer.parseInt(record.get("computer"));

        String eligibility = (science > scienceThreshold && maths > mathsThreshold &&
                computer > computerThreshold && english > englishThreshold) ? "YES" : "NO";

        Student student = studentRepository.findByRollNumber(rollNumber);
        if (student == null) {
            student = new Student();
            student.setRollNumber(rollNumber);
            student.setName(name);
            student.setScience(science);
            student.setMaths(maths);
            student.setEnglish(english);
            student.setComputer(computer);
            logger.info("Creating new student record: {}", rollNumber);
        } else {
            logger.info("Updating student record: {}", rollNumber);
        }
        student.setEligibility(eligibility);
        studentRepository.save(student);
    }

    public Student getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber);
        if (student != null) {
            logger.info("Retrieved student record: {}", rollNumber);
        } else {
            logger.info("Student record not found: {}", rollNumber);
        }
        return student;
    }
}
