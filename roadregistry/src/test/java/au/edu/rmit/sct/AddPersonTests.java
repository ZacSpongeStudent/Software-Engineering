package au.edu.rmit.sct;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;
public class AddPersonTests {
    //testing function
    private String getLastLineFromFile(String filename) {
        String lastLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
        } catch (IOException e) {
            fail("Failed to read from file.");
            return null;
        }
        return lastLine;
    }

    @Test
    public void testValidPerson() {
        // All inputs are valid
        Person p1 = new Person("23$%g_f#XY", "Alice", "Smith", "45|George St|Melbourne|Victoria|Australia", "01-01-2000");
        Person p2 = new Person("39!x@_&zZZ", "Bob", "Brown", "99|Queen Rd|Melbourne|Victoria|Australia", "31-12-1985");
        Person p3 = new Person("78*&^df!KL", "Charlie", "Lee", "10|Sunset Blvd|Melbourne|Victoria|Australia", "05-05-1995");
        
        //write to file to remove any chance of duplicate lines and false positives
        try (FileWriter writer = new FileWriter("addPerson_results.txt", true)) {
            writer.write("\n");
        } catch (IOException e) {
            fail("Failed to read from file.");
        }

        //add all people and check if the last line is equal to the person information
        assertTrue(p1.addPerson());
        assertEquals(p1.toString(), getLastLineFromFile("addPerson_results.txt"));

        assertTrue(p2.addPerson());
        assertEquals(p2.toString(), getLastLineFromFile("addPerson_results.txt"));

        assertTrue(p3.addPerson());
        assertEquals(p3.toString(), getLastLineFromFile("addPerson_results.txt"));
    }

    @Test
    public void testPersonIDMissingSpecialCharacters() {
        // Invalid ID: No special characters between characters 3–8
        Person p1 = new Person("45abcdeXYZ", "Daniel", "Mills", "10|Main St|Melbourne|Victoria|Australia", "14-07-1991");
        Person p2 = new Person("67ghijklMN", "Emily", "Turner", "34|Bourke St|Melbourne|Victoria|Australia", "23-08-1989");
        Person p3 = new Person("23mnopqrST", "Frank", "Hill", "21|River Rd|Melbourne|Victoria|Australia", "09-09-1993");

        String lastLineFromFile = getLastLineFromFile("addPerson_results.txt");

        assertFalse(p1.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p2.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p3.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));
    }

    @Test
    public void testPersonIDTooShort() {
        // Invalid ID: not enough characters
        Person p1 = new Person("56s_d&*A", "Grace", "Evans", "55|Palm Ave|Melbourne|Victoria|Australia", "11-03-1990");
        Person p2 = new Person("67s&*B", "Henry", "Green", "12|Park Rd|Melbourne|Victoria|Australia", "02-06-1988");
        Person p3 = new Person("78$%fT", "Ivy", "White", "73|Ocean Dr|Melbourne|Victoria|Australia", "29-01-2001");

        String lastLineFromFile = getLastLineFromFile("addPerson_results.txt");

        assertFalse(p1.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p2.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p3.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));
    }

    @Test
    public void testAddressWrongState() {
        // Invalid address: State is not Victoria
        Person p1 = new Person("56s_d&f*AB", "Jack", "Black", "11|Main St|Melbourne|NSW|Australia", "10-10-1992");
        Person p2 = new Person("56s_d&f*AB", "Kate", "Moss", "22|High St|Melbourne|Queensland|Australia", "15-04-1987");
        Person p3 = new Person("56s_d&f*AB", "Leo", "King", "33|Bridge Rd|Melbourne|Tasmania|Australia", "08-08-1998");
          
        String lastLineFromFile = getLastLineFromFile("addPerson_results.txt");

        assertFalse(p1.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p2.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p3.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));
    }

    @Test
    public void testBirthdateInvalidFormatWrongOrder() {
        // Invalid birthdate: Format is not DD-MM-YYYY
        Person p1 = new Person("56s_d&f*AB", "Mia", "Scott", "14|River St|Melbourne|Victoria|Australia", "1990/12/25");
        Person p2 = new Person("56s_d&f*AB", "Noah", "Wright", "90|Station Rd|Melbourne|Victoria|Australia", "12.11.1980");
        Person p3 = new Person("56s_d&f*AB", "Olivia", "Brown", "67|Sunshine Blvd|Melbourne|Victoria|Australia", "March 5, 1993");
        
        String lastLineFromFile = getLastLineFromFile("addPerson_results.txt");

        assertFalse(p1.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p2.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));

        assertFalse(p3.addPerson());
        assertEquals(lastLineFromFile, getLastLineFromFile("addPerson_results.txt"));
    }

}