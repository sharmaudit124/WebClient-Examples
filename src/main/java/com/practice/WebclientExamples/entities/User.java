package com.practice.WebclientExamples.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long userId;
    private String name;
    private String gender;
    private int age;
    private String dob;
    private String nationality;
    private String verificationStatus;
    @JsonIgnore
    private LocalDate dateCreated;
    @JsonIgnore
    @UpdateTimestamp
    private LocalDate dateModified;

}
