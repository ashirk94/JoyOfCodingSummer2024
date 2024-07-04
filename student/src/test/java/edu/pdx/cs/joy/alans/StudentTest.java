package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the Student class.  In addition to the JUnit annotations,
 * they also make use of the <a href="http://hamcrest.org/JavaHamcrest/">hamcrest</a>
 * matchers for more readable assertion statements.
 */
public class StudentTest
{
  private static Student createStudent(String name) {
    return new Student(name, new ArrayList<>(), 0.0, "Doesn't matter");
  }

  @Test
  void studentNamedPatIsNamedPat() {
    String name = "Pat";
    var pat = createStudent(name);
    assertThat(pat.getName(), equalTo(name));
  }

  @Test
  void allStudentsSayThisClassIsTooMuchWork() {
    Student student = new Student("nam", new ArrayList<>(), 3.98, "female");
    assertThat(student.says(), equalTo("This class is too much work"));
  }

  @Test
  void toStringContainsStudentName() {
    String name = "name";
    Student student = createStudent(name);
    assertThat(student.toString(), containsString(name));
  }

  @Test
  void toStringForDaveContainsDave() {
    String name = "Dave";
    Student student = createStudent(name);
    assertThat(student.toString(), containsString(name));
  }

  @Test
  void toStringContainsGPA() {
    double gpa = 3.45;
    Student student = new Student("name", new ArrayList<>(), gpa, "other");
            assertThat(student.toString(), containsString(String.valueOf(gpa)));
  }

  @Test
  void toStringContainsNameHasAGPAofGPA() {
    double gpa = 3.45;
    String name = "name";
    Student student = new Student(name, new ArrayList<>(), gpa, "other");
            assertThat(student.toString(), containsString(student.getName() + " has a GPA of " + gpa));
  }

  @Test
  void whenGPAisNegativeAnExceptionIsThrown() {
    double gpa = -1.23;

    assertThrows(IllegalArgumentException.class, () -> new Student("name",
            new ArrayList<>(), gpa, "other"));
  }

  @Test
  void whenGPAisGreaterThanFourAnExceptionIsThrown() {
    double gpa = 5.0;

    assertThrows(IllegalArgumentException.class, () -> new Student("name",
            new ArrayList<>(), gpa, "other"));
  }

}
