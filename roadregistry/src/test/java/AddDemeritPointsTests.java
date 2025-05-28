import static org.junit.jupiter.api.Assertions.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import au.edu.rmit.sct.Person;

import java.util.Date;

public class AddDemeritPointsTests {

    @Test
    public void testdemeritPointsInRange() throws ParseException {
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-1999");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");
        assertEquals("Failed", p.addDemeritPoints(date, 0));
        assertEquals("Success", p.addDemeritPoints(date, 1));
        assertEquals("Success", p.addDemeritPoints(date, 4));
        assertEquals("Success", p.addDemeritPoints(date, 6));
        assertEquals("Failed", p.addDemeritPoints(date, 10)); 
    }

    @Test
    public void testPersonUnder21IsNotSuspended() throws ParseException {
        // Person under 21 is not suspended if points < 6
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2005");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        // Total demerits = 5
        p.addDemeritPoints(date, 2);
        p.addDemeritPoints(date, 3);
        assertFalse(p.getIsSuspended());
    }


    @Test
    public void testPersonUnder21IsSuspended() throws ParseException {
        // Person under 21 isSuspended if points >= 6
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "1-1-2005");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        // Total demerits = 6
        p.addDemeritPoints(date, 3);
        p.addDemeritPoints(date, 3);
        assertTrue(p.getIsSuspended());

        // Total demerits = 9
        p.addDemeritPoints(date, 3);
        assertTrue(p.getIsSuspended());

        // Total demerits = 12
        p.addDemeritPoints(date, 3);
        assertTrue(p.getIsSuspended());
    }


    @Test
    public void testPersonOver21IsSuspended() throws ParseException {
        // Person over 21 isSuspended if points >= 12
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "01-01-1990");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse("25-05-2025");

        // Total demerits = 11
        p.addDemeritPoints(date, 6); 
        p.addDemeritPoints(date, 5);
        assertFalse(p.getIsSuspended());

        // Total demerits = 12
        p.addDemeritPoints(date, 1); 
        assertTrue(p.getIsSuspended());

        // Total demerits = 15
        p.addDemeritPoints(date, 3); 
        assertTrue(p.getIsSuspended());
    }

    @Test
    public void testDemeritPointsOlderThanTwoYearsNotCounted() throws ParseException {
        // Person is not suspended if offenses are not within 2 years
        Person p = new Person("56s_d%&fAB", "John", "Doe", "123|Street St|Melbourne|Victoria|Australia", "01-01-2000");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Date within 2 years: 25-05-2025
        Date offenseDate = sdf.parse("25-05-2025");

        // Add points from more than 2 years ago (e.g., 01-01-2020)
        Date oldOffenseDate = sdf.parse("01-01-2020");

        // Total demerits = 12 (Not within 2 years)
        p.addDemeritPoints(oldOffenseDate, 6); 
        p.addDemeritPoints(oldOffenseDate, 6); 

        // Add 5 demerits (within 2 years)
        p.addDemeritPoints(offenseDate, 5); 

        assertFalse(p.getIsSuspended());
    }


    @Test
    public void testValidPerson() {
        // All inputs are valid
        Person p = new Person("56s_d&fAB", "John", "Doe", "12|Main St|Melbourne|Victoria|Australia", "12-03-1992");
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
        Person p = new Person("56s_d&fA", "Jane", "Doe", "12|Main St|Melbourne|Victoria|Australia", "12-03-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddressWrongState() {
        // Invalid address: State is not Victoria
        Person p = new Person("56s_d&fAB", "John", "Doe", "12|Main St|Melbourne|NSW|Australia", "12-03-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testBirthdateInvalidFormatWrongOrder() {
        // Invalid birthdate: Format is YYYY-MM-DD instead of DD-MM-YYYY
        Person p = new Person("56s_d&f*AB", "John", "Doe", "12|Main St|Melbourne|Victoria|Australia", "1992-03-12");
        assertFalse(p.addPerson());
    }

}