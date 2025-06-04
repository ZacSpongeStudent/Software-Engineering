package au.edu.rmit.sct;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class AddDemeritPointsTests {

    public static void clearDemeritPointsFile() {
        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", false)) {
            writer.write(""); // overwrite with empty content
        } catch (IOException e) {
            fail("Failed to clear addDemeritPoints_results.txt file.");
        }
    }

    @Test
    public void testValidDemeritPoints() throws ParseException {
        // Test adding demerit points over a range of valid values
        clearDemeritPointsFile();
        
        // Write a newline to reduce file mismatch issues
        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", true)) {
            writer.write("\n");
        } catch (IOException e) {
            fail("Failed to prepare file for test.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        for (int points = 1; points <= 6; points ++) {
            Person person = new Person("5" + points + "s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
            assertEquals("Success", person.addDemeritPoints(date, points));
            assertEquals(Person.demeritPointRecordToString(person, date, points), Person.getLastLineFromFile("addDemeritPoints_results.txt"));
        }
    }

    @Test
    public void testInvalidDemeritPoints() throws ParseException {
        // Test adding demerit points over a range of valid values
        clearDemeritPointsFile();
        
        // Write a newline to reduce file mismatch issues
        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", true)) {
            writer.write("\n");
        } catch (IOException e) {
            fail("Failed to prepare file for test.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        Person person = new Person("51s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
        assertEquals("Failed", person.addDemeritPoints(date, 0));

        person = new Person("52s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
        assertEquals("Failed", person.addDemeritPoints(date, 7));

        person = new Person("52s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
        assertEquals("Failed", person.addDemeritPoints(date, -7));

        assertEquals("", Person.getLastLineFromFile("addDemeritPoints_results.txt")); 
    }

    @Test
    public void testSuspensionIfDoesNotExceedAllottedPoints() throws ParseException {
        // Person under 21 should be suspended if points is more than 6
        clearDemeritPointsFile();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        // Person under 21 is not suspended if points < 6
        Person personUnder21 = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2006");
        //set the points to 5, check to make sure this works
        assertEquals("Success", personUnder21.addDemeritPoints(date, 5));
        assertEquals(5, personUnder21.getTotalDemeritPoints());

        personUnder21.addDemeritPoints(date, 1);
        assertFalse(personUnder21.getIsSuspended());

        personUnder21.addDemeritPoints(date, 1);
        assertTrue(personUnder21.getIsSuspended());

        // Person over 21 is not suspended if points < 12
        Person personOver21 = new Person("57s_d%&fAB", "Jane", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2000");
        //set the points to 11, check to make sure this works
        assertEquals("Success", personOver21.addDemeritPoints(date, 6));
        assertEquals("Success", personOver21.addDemeritPoints(date, 5));
        assertEquals(11, personOver21.getTotalDemeritPoints());

        personOver21.addDemeritPoints(date, 1);
        assertFalse(personOver21.getIsSuspended());

        personOver21.addDemeritPoints(date, 1);
        assertTrue(personOver21.getIsSuspended());
    }

    @Test
    public void testSuspensionIfExceedsAllottedPoints() throws ParseException {
        // Person under 21 should be suspended if points is more than 6
        clearDemeritPointsFile();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        // Person under 21 is not suspended if points < 6
        Person personUnder21 = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2006");
        //set the points to 6, check to make sure this works
        assertEquals("Success", personUnder21.addDemeritPoints(date, 6));
        assertEquals(6, personUnder21.getTotalDemeritPoints());

        personUnder21.addDemeritPoints(date, 1);
        assertTrue(personUnder21.getIsSuspended());

        // Person over 21 is not suspended if points < 12
        Person personOver21 = new Person("57s_d%&fAB", "Jane", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2000");
        //set the points to 11, check to make sure this works
        assertEquals("Success", personOver21.addDemeritPoints(date, 6));
        assertEquals("Success", personOver21.addDemeritPoints(date, 6));
        assertEquals(12, personOver21.getTotalDemeritPoints());

        personOver21.addDemeritPoints(date, 1);
        assertTrue(personOver21.getIsSuspended());
    }

    @Test
    public void testDemeritPointsOlderThanTwoYearsNotCounted() throws ParseException {
        // Person under 21 should be suspended if points is more than 6
        clearDemeritPointsFile();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Person under 21 is not suspended if points < 6
        Person person = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2006");
        //set the points to 6, check to make sure this works
        assertEquals("Success", person.addDemeritPoints(sdf.parse("25-05-2025"), 6));
        assertEquals(6, person.getTotalDemeritPoints());

        assertFalse(person.getIsSuspended());
        
        person.addDemeritPoints(sdf.parse("25-05-2020"), 1);
        person.addDemeritPoints(sdf.parse("13-03-1969"), 5);

        assertFalse(person.getIsSuspended());
    }
}