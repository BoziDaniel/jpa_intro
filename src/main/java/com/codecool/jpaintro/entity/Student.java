package com.codecool.jpaintro.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity // jpa
public class Student {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(nullable = false, unique = true) //nem lehet null és nem lehet 2 u olyan email cím
    private String email;

    @OneToOne(cascade = CascadeType.PERSIST)
    //cascade: milyen művelet fusson le ha elmentek egy studentet, mit csináljon a benne lévő addressel. Jelen esetben ha mentek egy studentet akkor a hozzá tartozó address is mentődni fog.
    //CascadeType.ALL is van, ezesetben ha törlöm a studentet törlődik a cím is. vigyázni vele.
    private Address address;

    private LocalDate birthDate;

    @Transient // mivel ez változik és a birthDateből jön nem mentődik el az adatbáisba
    private long age;

    @ElementCollection //ha ilyen listákat akarok tárolni, egyszerű dolgokat mint string, lista, set, primitívek, ilyesmihez jó
    @Singular //egyesével tudunk dolgookat hozzáadni, nem kell előre összerakni a listát
    private List<String> phoneNumbers;

    @ManyToOne
    private School school;
    public void calculateAge() {
        if (birthDate != null) {
            age = ChronoUnit.YEARS.between(birthDate, LocalDate.now()); // a kettő közötti évkülönbséget adja vissza
        }
    }

}

