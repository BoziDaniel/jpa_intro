package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>
//a jpaRepohoz meg kell addni a managelni kívánt Entitást és az id-ja típusát
{
    //querymethod
    List<Student> findByNameStartingWithOrBirthDateBetween(String name, LocalDate from, LocalDate to);

    //jpl query, s: egy student, distinct: csak a különbözőket kérem (minden studentből)
    @Query("SELECT distinct s.address.country from Student s")
    List<String> findAllCountry();

}
