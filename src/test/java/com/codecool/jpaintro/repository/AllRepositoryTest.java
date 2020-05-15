package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//A junit elérhető lesz a springes tesztek számára
@RunWith(SpringRunner.class)
//minden ebben a classban található tesztre egy új db-t hoz létre,
// új izolált környezet, nem kell karbantartani
@DataJpaTest
@ActiveProfiles("test") //ez nem fut le csak ha a teszt profilon futtatok, azaz tesztelek
public class AllRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager entityManager; //olyan osztály amiben helper metódusok vannak, nem kell feltétlen

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    public void saveOneSimple() {
        Student jani = Student.builder()
                .email("a@a.com")
                .name("jani")
                .build();
        studentRepository.save(jani);
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(1);
    }

    @Test(expected = DataIntegrityViolationException.class) //ezt a hibát várom
    public void saveUniqueFieldTwice() {
        Student student = Student.builder()
                .email("a@a.com")
                .name("John")
                .build();
        studentRepository.save(student);

        Student student2 = Student.builder()
                .email("a@a.com")
                .name("Gizi")
                .build();
        studentRepository.saveAndFlush(student2);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void emailShouldBeNotNull() {
        Student student = Student.builder()
                .name("John")
                .build();
        studentRepository.save(student);
    }

    @Test
    public void transientIsNotSaved() {
        Student student = Student.builder()
                .birthDate(LocalDate.of(1990, 1, 2))
                .email("a@a.com")
                .name("John")
                .build();
        student.calculateAge();
        assertThat(student.getAge()).isGreaterThanOrEqualTo(25);

        studentRepository.save(student);
        entityManager.clear(); //ez kell, ha nem teszem akkor ez managelt stateű enttás lenne, a további tesztek nem tudnák garantálni h egy új állapotról hívok meg minden lekérdezést
        List<Student> students = studentRepository.findAll();
        assertThat(students).allMatch(student1 -> student1.getAge() == 0L); //a findall visszaadja az összes recordot ami a db-ben van
        //allmatch: minden egyes elemre igaz az h a student1.getAge() -e 0 Long, booleant ad vissza
        //hiába van ellenőrizve, kiszámolva és elmentve h azz életkora a studentnek x, amikor egy tiszta entity managerbe visszaolvasunk mindent akkor az age nem lesz perzisztálva, nem lesz bent a dbben

    }

    @Test
    public void adressIsPersistedWithStudent() {
        Address address = Address.builder()
                .country("Hungary")
                .city("Bp")
                .address("Nagymező utca 45")
                .zipCode(1065).build();
        Student laci = Student.builder()
                .email("laci@csaci.com")
                .address(address)
                .build();
        studentRepository.save(laci);

        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses)
                .hasSize(1)
                .allMatch(address1 -> address1.getId() > 0L); //megnézem h minden addressemhez generálódott-e egy id
    }

    @Test
    public void studentsArePersistentAndDeletedWithNewSchool() {
        Set<Student> students = IntStream.range(1, 10)
                .boxed()
                .map(integer -> Student.builder().email("student" + integer + "@codecool.com").build())
                .collect(Collectors.toSet());
        School school = School.builder()
                .students(students)
                .location(Location.BUDEPEST)
                .build();
        schoolRepository.save(school);
        assertThat(studentRepository.findAll())
                .hasSize(9)
                .anyMatch(student -> student.getEmail().equals("student9@codecool.com"));
        schoolRepository.deleteAll();
        assertThat(schoolRepository.findAll())
                .hasSize(0);
    }

    @Test
    public void findByNameStartingWithOrBirthDayBetween() {
        Student john = Student.builder()
                .email("john@csaci.com")
                .name("John")
                .build();
        Student jane = Student.builder()
                .email("jane@csaci.com")
                .name("Jane")
                .build();
        Student martha = Student.builder()
                .email("martha@csaci.com")
                .name("")

                .build();
        Student peter = Student.builder()
                .email("peter@csaci.com")
                .birthDate(LocalDate.of(2010, 10, 3))
                .build();
        Student steve = Student.builder()
                .email("steve@csaci.com")
                .birthDate(LocalDate.of(2011, 12, 5))
                .build();
        studentRepository.saveAll(Lists.newArrayList(john, jane, martha, peter, steve));
        List<Student> filteredStudents = studentRepository.findByNameStartingWithOrBirthDateBetween("J",
                LocalDate.of(1000, 1, 1),
                LocalDate.of(2011, 1, 1));
        assertThat(filteredStudents).containsExactlyInAnyOrder(john, jane, peter);


    }

    @Test
    public void findAllCountry() {
        Student first = Student.builder()
                .email("a@a.com")
                .address(Address.builder().country("Hungary").build())
                .build();
        Student second = Student.builder()
                .email("b@a.com")
                .address(Address.builder().country("Poland").build())
                .build();
        Student third = Student.builder()
                .email("c@a.com")
                .address(Address.builder().country("Poland").build())
                .build();
        Student fourth = Student.builder()
                .email("d@a.com")
                .address(Address.builder().country("Hungary").build())
                .build();
        studentRepository.saveAll(Lists.newArrayList(first, second, third, fourth));
        List<String> allCountry = studentRepository.findAllCountry();
        assertThat(allCountry)
                .hasSize(2)
                .containsOnlyOnce("Hungary", "Poland");
    }

    @Test
    public void UpdateAllToUSAByStudentName() {
        Address adress1 = Address.builder()
                .country("Hungary")
                .build();
        Address adress2 = Address.builder()
                .country("Poland")
                .build();
        Address adress3 = Address.builder()
                .country("Germany")
                .build();
        Student student = Student.builder()
                .name("temp")
                .email("a@a.com")
                .address(adress1)
                .build();
        studentRepository.save(student);
        addressRepository.save(adress2);
        addressRepository.save(adress3);
        assertThat(addressRepository.findAll())
                .hasSize(3)
                .noneMatch(address -> address.getCountry().equals("USA"));
        int updateRows = addressRepository.updateAllToUSAByStudentName("temp");
        assertThat(updateRows).isEqualTo(1);
        assertThat(addressRepository.findAll())
                .hasSize(3)
                .anyMatch(address -> address.getCountry().equals("USA"));
    }
}