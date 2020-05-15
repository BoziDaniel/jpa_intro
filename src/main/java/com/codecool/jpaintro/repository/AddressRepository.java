package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("update Address a set a.country= 'USA' where a.id in " + "(select  s.address.id from Student s where s.name Like :name)")
    @Modifying(clearAutomatically = true)
    int updateAllToUSAByStudentName(@Param("name") String name);
    //az összes olyan address country mezőjét usára updateljük ahol a student neve hasonló valamilyen keresési paraméterhez
    // a :name kapcsolatban van a @param("name") el, így adok be paramétert
    //A @Modifying annotáció azt jelzi h valamilyen adatbázis módosulás leszen végrehajtva, kötelező kül nem müxgit push -u origin masterik
    //clearAutomatically = true : az entitymanager flasholása miatt kell
}
