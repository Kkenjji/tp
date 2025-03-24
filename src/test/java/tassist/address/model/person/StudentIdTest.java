package tassist.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        String invalidStudentId = "";
        assertThrows(IllegalArgumentException.class, () -> new StudentId(invalidStudentId));
    }

    @Test
    public void isValidStudentId() {
        // null name
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // invalid name
        assertFalse(StudentId.isValidStudentId("")); // empty string
        assertFalse(StudentId.isValidStudentId(" ")); // spaces only
        assertFalse(StudentId.isValidStudentId("^")); // only non-alphanumeric characters
        assertFalse(StudentId.isValidStudentId("1234567")); // only contains numeric characters
        assertFalse(StudentId.isValidStudentId("B1234567N")); // first character is not 'A'
        assertFalse(StudentId.isValidStudentId("A12345*7N")); // contains non-alphanumeric characters
        assertFalse(StudentId.isValidStudentId("A123456N")); // only contains 6 digits
        assertFalse(StudentId.isValidStudentId("a123456N")); // lowercase 'a' at the start


        // valid name
        assertTrue(StudentId.isValidStudentId("A0287670M")); // first character is 'A', followed by 7 numbers,
        // followed by an uppercase character 'M'
        assertTrue(StudentId.isValidStudentId("A0000000Z")); // all the same digits
        assertTrue(StudentId.isValidStudentId("A0000000A")); // last letter is 'A'

    }

    @Test
    public void equals() {
        StudentId value = new StudentId("A0000000A");

        // same values -> returns true
        assertTrue(value.equals(new StudentId("A0000000A")));

        // same object -> returns true
        assertTrue(value.equals(value));

        // null -> returns false
        assertFalse(value.equals(null));

        // different types -> returns false
        assertFalse(value.equals(5.0f));

        // different values -> returns false
        assertFalse(value.equals(new StudentId("A0000000B")));
    }
}
