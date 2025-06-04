package au.edu.rmit.sct;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UpdatePersonalDetailsTests {
    // tests valid update scenario for adults with odd-numbered IDs
    @Test
    public void testAllValidChangesOver18OddFirstDigit() throws IOException {
        // Set up the file with the initial person data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("updatePersonalDetails_results.txt"))) {
            writer.write("35s_d%&fAB|John|Smith|32|Highland Street|Melbourne|Victoria|Australia|15-11-2002");
            writer.newLine();
        }

        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");

        boolean result = person.updatePersonalDetails(
            "36s_d%&fAB", "Jane", "Doe", "33|Main Street|Melbourne|Victoria|Australia", "15-11-2002", 20
        );

        assertTrue(result, "Should allow all valid changes for person over 18 with odd first digit");
        assertEquals(person.toString(), Person.getLastLineFromFile("updatePersonalDetails_results.txt"));
    }

    // tests age restriction for address changes
    @Test
    public void testUpdateAddressUnder18() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2005");
        String lastLineFromFile = Person.getLastLineFromFile("updatePersonalDetails_results");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33|Main Street|Melbourne|Victoria|Australia", "15-11-2005", 17);
        
        assertFalse(result, "Should not allow address change for person under 18");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatePersonalDetails_results"));
    }

    // tests birthday change restriction, can't change other fields when updating birthday
    @Test
    public void testChangeBirthdayAndOtherField() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2005");
        String lastLineFromFile = Person.getLastLineFromFile("updatePersonalDetails_results");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "Johnny", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "16-11-2005", 18);
        
        assertFalse(result, "Should not allow changing birthday and another field at the same time");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatePersonalDetails_results"));
    }

    // tests ID change restriction for even-numbered IDs
    @Test
    public void testChangeIDWhenFirstDigitEven() {
        Person person = new Person("24s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");
        String lastLineFromFile = Person.getLastLineFromFile("updatePersonalDetails_results");
        
        boolean result = person.updatePersonalDetails("25s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002", 20);
        
        assertFalse(result, "Should not allow changing ID if first digit is even");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatePersonalDetails_results"));
    }

    // tests address format
    @Test
    public void testInvalidNewAddressFormat() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");
        String lastLineFromFile = Person.getLastLineFromFile("updatePersonalDetails_results");
        
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33 Main Street, Melbourne, Victoria, Australia", "15-11-2002", 20);
        
        assertFalse(result, "Should not allow update with invalid address format");
        assertEquals(lastLineFromFile, Person.getLastLineFromFile("updatePersonalDetails_results"));
    }
}
