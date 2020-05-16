package com.codecool.jpaintro;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import com.codecool.jpaintro.repository.AddressRepository;
import com.codecool.jpaintro.repository.SchoolRepository;
import com.codecool.jpaintro.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class JpaIntroApplication {
//    @Autowired
//    StudentRepository studentRepository;
//
//    @Autowired
//    private AddressRepository addressRepository;

    @Autowired
    SchoolRepository schoolRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaIntroApplication.class, args);
    }

    @Bean
    @Profile("production")
//csak a sima futtatásnál(production, ezt adtuk be alapméretezettnek az applicatoon.propertiesnél) fut le a CommandLineRunner, a teszteknél nem így nem vágja haza a tesztet.
    public CommandLineRunner init() {
        //a CommandLineRunner egy functional interface(java8 óta)
        // ami felírható egy lambda kifejezés formájában
        return args -> {
//            Student john = Student.builder()
//                    .name("john")
//                    .email("joska@gmail.com")
//                    .birthDate(LocalDate.of(1990, 1, 1))
//                    .address(Address.builder().city("Pest").country("Magyarország").build())
//                    .phoneNumber("666-666")
//                    .phoneNumber("555-555")
//                    .phoneNumber("777-777")
//
//                    .build();
//
//            john.calculateAge();
//
//            studentRepository.save(john);

            Address address = Address.builder()
                    .address("a streeet")
                    .city("Budapest")
                    .country("Agyarország")
                    .build();
            Address address1 = Address.builder()
                    .address("b streeet")
                    .city("Budapest")
                    .country("Agyarország")
                    .build();
            Student jani = Student.builder()
                    .name("jani")
                    .email("a@a.com")
                    .birthDate(LocalDate.of(1111, 1, 1))
                    .address(address)
                    .phoneNumbers(Arrays.asList("555-6666", "666-5555"))
                    .build();
            Student barbaraEgyBarbaraVilágból = Student.builder()
                    .name("barbaraEgyBarbaraVilágból")
                    .email("műanyagÉlet@ezFantasztikus.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .address(address1)
                    .phoneNumbers(Arrays.asList("777-6666", "777-5555"))
                    .build();
            School school = School.builder()
                    .location(Location.BUDEPEST)
                    .name("CC BP")
                    .student(jani)
                    .student(barbaraEgyBarbaraVilágból)
                    .build();

            barbaraEgyBarbaraVilágból.setSchool(school);
            jani.setSchool(school);
            //ez a 2 sor azért kell,
            // mert a one to many és many to one kapcsolatnál mind2 félnek tudnia kell a kapcsolatról,
            // janinak is h a schoolba jár és a schoolnak is h jani jár bele
            schoolRepository.save(school);
        };

    }
}
