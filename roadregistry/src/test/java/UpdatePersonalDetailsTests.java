import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import au.edu.rmit.sct.Person;

public class UpdatePersonalDetailsTests {

    @Test
    public void testUpdateAddressUnder18() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2005");
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33|Main Street|Melbourne|Victoria|Australia", "15-11-2005", 17);
        assertFalse(result, "Should not allow address change for person under 18");
    }

    @Test
    public void testChangeBirthdayAndOtherField() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2005");
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "Johnny", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "16-11-2005", 18);
        assertFalse(result, "Should not allow changing birthday and another field at the same time");
    }

    @Test
    public void testChangeIDWhenFirstDigitEven() {
        Person person = new Person("24s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");
        boolean result = person.updatePersonalDetails("25s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002", 20);
        assertFalse(result, "Should not allow changing ID if first digit is even");
    }

    @Test
    public void testAllValidChangesOver18OddFirstDigit() throws IOException {
        // Set up the file with the initial person data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("persons.txt"))) {
            writer.write("35s_d%&fAB|John|Smith|32|Highland Street|Melbourne|Victoria|Australia|15-11-2002");
            writer.newLine();
        }
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");

        boolean result = person.updatePersonalDetails(
            "36s_d%&fAB", "Jane", "Doe", "33|Main Street|Melbourne|Victoria|Australia", "15-11-2002", 20
        );
        assertTrue(result, "Should allow all valid changes for person over 18 with odd first digit");
    }


    @Test
    public void testInvalidNewAddressFormat() {
        Person person = new Person("35s_d%&fAB", "John", "Smith", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2002");
        boolean result = person.updatePersonalDetails("35s_d%&fAB", "John", "Smith", "33 Main Street, Melbourne, Victoria, Australia", "15-11-2002", 20);
        assertFalse(result, "Should not allow update with invalid address format");
    }
}
