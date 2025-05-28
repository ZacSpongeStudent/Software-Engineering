package au.edu.rmit.sct;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings ("unused")

public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritPoints = new HashMap<>();
    private boolean isSuspended;

    //constructs a person object
    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }

    //adds person data to a file if valid
    public boolean addPerson() {
        if (!validatePersonID(personID) || !validateAddress(address) || !validateBirthdate(birthdate)) {
            return false;
        }

        try (FileWriter writer = new FileWriter("people.txt", true)) { //try to write to file
            writer.write(this.toString() + "\n");
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    //validates that the person id matches the format
    private boolean validatePersonID(String id) {
        if (id.length() != 10) return false;
        if (!id.substring(0, 2).matches("[2-9]{2}")) return false; //first two characters are digits

        String middle = id.substring(2, 8); //gets the middle 6 characters

        //counts the special characters
        int specialCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        if (specialCount < 2) return false; //returns false if there are less than two special characters in this part of the string

        if (!id.substring(8).matches("[A-Z]{2}")) return false; //last two characters are uppercase letters

        return true;
    }

    //validates that the address is in the correct format and the state is victoria
    private boolean validateAddress(String address) {
        String[] parts = address.split("\\|"); //splits the address by '|'
        if (parts.length != 5) return false; //check there are exactly 5 parts
        for (String part : parts) {
            if (part.trim().isEmpty()) return false; //check none of the parts are empty
        }
        return parts[3].equalsIgnoreCase("Victoria"); //check state is victoria
    }

    //validates that the birthdate is in the dd-mm-yyyy format
    private boolean validateBirthdate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT); //checks for real dates
        
        try {
            LocalDate.parse(date, formatter); //try to parse the date
            return true;
        } catch (DateTimeParseException e) {
            return false; //invalid format or impossible date
        }
    }

    //returns the person data as a string separated by '|'
    @Override
    public String toString() {
        return personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthdate;
    }










    public String addDemeritPoints(Date offenseDate, int points) {
        if (points < 1 || points > 6) return "Failed";

        demeritPoints.put(offenseDate, points);

        int recentPoints = calculateRecentPoints(offenseDate, 2);
        int age = getAgeAtDate(offenseDate);

        if (age < 21 && recentPoints > 6) {
            isSuspended = true;
        } else if (age >= 21 && recentPoints > 12) {
            isSuspended = true;
        }

        try (FileWriter writer = new FileWriter("demerit_records.txt", true)) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateStr = sdf.format(offenseDate);
        writer.write(personID + "," + dateStr + "," + points + "\n");
        } catch (IOException e) {
            return "Failed";
        }

        return "Success";
    }

    // Helper function for addDemeritPoints:
    // Totals the demerit points found in hash map.

    public int getTotalDemeritPoints() {
        int total = 0;
        for (int points : demeritPoints.values()) {
            total += points;
        }
        return total;
    }

    // Helper function for addDemeritPoints:
    // Totals the demerit points found in hash map within the last two years.

    private int calculateRecentPoints(Date currentDate, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.YEAR, -years);
        Date thresholdDate = cal.getTime();

        int sum = 0;
        for (Map.Entry<Date, Integer> entry : demeritPoints.entrySet()) {
            if (!entry.getKey().before(thresholdDate)) {
                sum += entry.getValue();
            }
        }
        return sum;
    }

    // Helper function for addDemeritPoints:
    // Returns age of person based on date

    private int getAgeAtDate(Date targetDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date birth = sdf.parse(this.birthdate);
            Calendar birthCal = Calendar.getInstance();
            Calendar targetCal = Calendar.getInstance();
            birthCal.setTime(birth);
            targetCal.setTime(targetDate);

            int age = targetCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            if (targetCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return 0; 
        }
    }

    // Checks if person should be suspended
    public boolean getIsSuspended() {
        return isSuspended;
    }
}