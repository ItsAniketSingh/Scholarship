package com.example.scholarship.repository;

import com.example.scholarship.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByRollNumber(String rollNumber);
}
