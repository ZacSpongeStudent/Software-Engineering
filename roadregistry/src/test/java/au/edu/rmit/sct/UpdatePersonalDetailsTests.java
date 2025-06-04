package au.edu.rmit.sct;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UpdatePersonalDetailsTests {
    // tests valid update scenario for adults with odd-numbered IDs
    @Test
    public void testAllValidChangesOver18OddFirstDigit() throws IOException {
        // Set up the file with the initial person data
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");
        person.addPerson("updatePersonalDetails_results.txt");

        boolean result = person.updatePersonalDetails(
            "36s_d%&fAB", "Jane", "Doe", "33|Main Street|Melbourne|Victoria|Australia", "15-11-2002"
        );

        assertTrue(result, "Should allow all valid changes for person over 18 with odd first digit");
        assertEquals(person.toString(), Person.getLastLineFromFile("updatePersonalDetails_results.txt"));
    }

    // tests age restriction for address changes
    @Test
    public void testUpdateAddressUnder18() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-1-2008");
        person.addPerson("updatepersonalDetails_results.txt");
        String lastLineFromFile = Person.getLastLineFromFile("updatepersonalDetails_results.txt");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33|Main Street|Melbourne|Victoria|Australia", "15-1-2008");
        
        assertFalse(result, "Should not allow address change for person under 18");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatepersonalDetails_results.txt"));
    }

    // tests birthday change restriction, can't change other fields when updating birthday
    @Test
    public void testChangeBirthdayAndOtherField() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2006");
        person.addPerson("updatepersonalDetails_results.txt");
        String lastLineFromFile = Person.getLastLineFromFile("updatepersonalDetails_results.txt");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "Johnny", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "16-11-2006");
        
        assertFalse(result, "Should not allow changing birthday and another field at the same time");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatepersonalDetails_results.txt"));
    }

    // tests ID change restriction for even-numbered IDs
    @Test
    public void testChangeIDWhenFirstDigitEven() {
        Person person = new Person("14s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2004");
        person.addPerson("updatepersonalDetails_results.txt");
        String lastLineFromFile = Person.getLastLineFromFile("updatepersonalDetails_results.txt");
        
        boolean result = person.updatePersonalDetails("25s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2004");
        
        assertFalse(result, "Should not allow changing ID if first digit is even");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatepersonalDetails_results.txt"));
    }

    // tests address format
    @Test
    public void testInvalidNewAddressFormat() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2004");
        person.addPerson("updatepersonalDetails_results.txt");
        String lastLineFromFile = Person.getLastLineFromFile("updatepersonalDetails_results.txt");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33 Main Street, Melbourne, Victoria, Australia", "15-11-2004");
        
        assertFalse(result, "Should not allow update with invalid address format");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatepersonalDetails_results.txt"));
    }
}
