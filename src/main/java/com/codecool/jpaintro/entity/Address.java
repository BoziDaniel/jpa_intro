package com.codecool.jpaintro.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity // jpa
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    private String country;
    private String city;
    private String address;
    private Integer zipCode;

    @OneToOne(mappedBy = "address") // a student oldalon az address field felelős a kapcsolat karbantartásáért
    @EqualsAndHashCode.Exclude
    private Student student;
}
