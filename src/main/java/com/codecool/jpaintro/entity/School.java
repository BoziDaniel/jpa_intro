package com.codecool.jpaintro.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity // jpa
public class School {

    @Id
    @GeneratedValue
    private Long id;


    private String name;

    @Enumerated(EnumType.STRING)
    //Enumtype.ORDINAL: ha db-be mentem akkor jelen esetben bp-nél 0, miskolcnál 1 es fog bekerülni a táblázatba, az elem "indexe?" az enumban
    //Enumtype.STRING: az enum elemének toStringje kerül be.
    private Location location;


    @Singular
    @OneToMany(mappedBy = "school", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}) //egy isihez több student is tartozhat
    // a mappedBy = "school" rész a student school fieldjére utal, itt a student a karbantartó
    @EqualsAndHashCode.Exclude
    private Set<Student> students;
}
