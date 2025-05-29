import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import au.edu.rmit.sct.Person;

public class AddPersonTests {

    @Test
    public void testValidPerson() {
        // All inputs are valid
        Person p = new Person("56s_d&f*AB", "John", "Doe", "12|Main St|Melbourne|Victoria|Australia", "12-03-1992");
        assertTrue(p.addPerson());
    }

    @Test
    public void testPersonIDMissingSpecialCharacters() {
        // Invalid ID: No special characters between characters 3–8
        Person p = new Person("56abcdfgAB", "Jane", "Doe", "12|Main St|Melbourne|Victoria|Australia", "12-03-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testPersonIDTooShort() {
        // Invalid ID: Only 9 characters
        Person p = new Person("56s_d&f*A", "Jane", "Doe", "12|Main St|Melbourne|Victoria|Australia", "12-03-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddressWrongState() {
        // Invalid address: State is not Victoria
        Person p = new Person("56s_d&f*AB", "John", "Doe", "12|Main St|Melbourne|NSW|Australia", "12-03-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testBirthdateInvalidFormatWrongOrder() {
        // Invalid birthdate: Format is YYYY-MM-DD instead of DD-MM-YYYY
        Person p = new Person("56s_d&f*AB", "John", "Doe", "12|Main St|Melbourne|Victoria|Australia", "1992-03-12");
        assertFalse(p.addPerson());
    }

}