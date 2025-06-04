package au.edu.rmit.sct;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class AddDemeritPointsTests {

    @Test
    public void testdemeritPointsInRange() throws ParseException {
        // Test adding demerit points over a range of valid and values
        
        // Write a newline to reduce file mismatch issues
        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", true)) {
            writer.write("\n");
        } catch (IOException e) {
            fail("Failed to prepare file for test.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        for (int points = 2; points < 6; points ++) {
            Person person = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
            assertEquals("Success", person.addDemeritPoints(date, points));
            assertEquals(Person.demeritPointRecordToString(person, date, points), Person.getLastLineFromFile("addDemeritPoints_results.txt"));
        }
    }

    @Test
    public void testPersonUnder21IsNotSuspended() throws ParseException {
        // Person under 21 is not suspended if points < 6
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2005");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    

        // Total demerits = 5
        Date date = sdf.parse("25-05-2025");
        p.addDemeritPoints(date, 2);
        date = sdf.parse("26-05-2025");
        p.addDemeritPoints(date, 3);
        assertFalse(p.getIsSuspended());
    }


    @Test
    public void testPersonUnder21IsSuspended() throws ParseException {
        // Person under 21 isSuspended if points >= 6
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2005");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Total demerits = 6
        Date date = sdf.parse("25-05-2025");
        p.addDemeritPoints(date, 3);

        date = sdf.parse("26-05-2025");
        p.addDemeritPoints(date, 3);

        assertTrue(p.getIsSuspended());

        // Total demerits = 9
        date = sdf.parse("27-05-2025");
        p.addDemeritPoints(date, 3);

        assertTrue(p.getIsSuspended());

        // Total demerits = 12
        date = sdf.parse("28-05-2025");
        p.addDemeritPoints(date, 3);

        assertTrue(p.getIsSuspended());
    }


    @Test
    public void testPersonOver21IsSuspended() throws ParseException {
        // Person over 21 isSuspended if points >= 12
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "01-01-1990");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Total demerits = 11
        Date date = sdf.parse("25-05-2025");
        p.addDemeritPoints(date, 6); 

        date = sdf.parse("26-05-2025");
        p.addDemeritPoints(date, 5);

        assertFalse(p.getIsSuspended());

        // Total demerits = 12
        date = sdf.parse("27-05-2025");
        p.addDemeritPoints(date, 1); 

        assertTrue(p.getIsSuspended());

        // Total demerits = 15
        date = sdf.parse("28-05-2025");
        p.addDemeritPoints(date, 3); 

        assertTrue(p.getIsSuspended());
    }

    @Test
    public void testDemeritPointsOlderThanTwoYearsNotCounted() throws ParseException {
        // Person is not suspended if offenses are not within 2 years
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "01-01-2000");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Add offense dates from more than 2 years ago (e.g., 01-01-2020)
        Date oldOffenseDate1 = sdf.parse("01-01-2020");
        Date oldOffenseDate2 = sdf.parse("01-01-2021");
        // Date within 2 years: 25-05-2025
        Date recentOffenseDate = sdf.parse("25-05-2025");

        // Total demerits = 12 (Not within 2 years)
        p.addDemeritPoints(oldOffenseDate1, 6); 
        p.addDemeritPoints(oldOffenseDate2, 6); 

        // Add 5 demerits (within 2 years)
        p.addDemeritPoints(recentOffenseDate, 5); 

        assertFalse(p.getIsSuspended());
    }

}