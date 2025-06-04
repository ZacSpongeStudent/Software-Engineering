package au.edu.rmit.sct;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    /**
     * A series of functions for the addPerson() test cases.
     * @author s4096726
     */

    //adds person data to a file if valid

    public boolean addPerson() {
        return addPerson("addPerson_results.txt");
    }

    public boolean addPerson(String filename) {
        if (!validatePersonID(personID) || !validateAddress(address) || !validateBirthdate(birthdate)) {
            return false;
        }

        try (FileWriter writer = new FileWriter(filename, true)) { //try to write to file
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





    /**
     * A series of functions for the addDemeritPoints() test cases.
     * @author s4003200
     */
    public String addDemeritPoints(Date offenseDate, int points) {
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            // Format offenseDate to string
            sdf.setLenient(false); // Enable strict date validation
            dateStr = sdf.format(offenseDate);

            // Parse it back to ensure it's valid
            Date reParsed = sdf.parse(dateStr);

            // Confirm re-parsed date matches original
            if (!reParsed.equals(offenseDate)) {
                return "Failed";
            }
        } catch (ParseException e) {
            return "Failed"; // Date format is invalid
        }

        //the points have to be between 1 and 6 inclusive
        if (points < 1 || points > 6) return "Failed";

        //record the demerit points
        demeritPoints.put(offenseDate, points);

        //get the total demerit points in the past 2 years from the current date
        Date currentDate = new Date();
        int recentPoints = calculateRecentPoints(currentDate, 2);

        int age = getAgeAtDate(currentDate);

        //apply the suspension rules based on age and recent demerit points
        if (age < 21 && recentPoints >= 6) {
            isSuspended = true;
        } else if (age >= 21 && recentPoints >= 12) {
            isSuspended = true;
        }

        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", true)) {
            //try to write the person's information and offense date to the text file
            dateStr = sdf.format(offenseDate);
            writer.write(personID + "," + dateStr + "," + points + "\n"); 
        } catch (IOException e) {
            return "Failed"; //return failed if the write fails
        }

        return "Success";
    }

    // Helper function for addDemeritPoints:
    // Totals the demerit points found in hash map.

    public int getTotalDemeritPoints() {
        //gets the total sum of the demerit points for the driver
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

        //Subtract the number of years to determine the threshold date
        cal.add(Calendar.YEAR, -years);
        Date thresholdDate = cal.getTime();

        int sum = 0;
        for (Map.Entry<Date, Integer> entry : demeritPoints.entrySet()) {
            // Check if the offense date is on or after the threshold date
            if (!entry.getKey().before(thresholdDate)) {
                sum += entry.getValue(); //add to total
            }
        }
        return sum;
    }

    // Helper function for addDemeritPoints:
    // Returns age of person based on date

    private int getAgeAtDate(Date targetDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date birth = sdf.parse(this.birthdate); //tries to parse the Date object into this format, if failed returns 0
            //sets calender objects and time with the birthday and target date
            Calendar birthCal = Calendar.getInstance();
            Calendar targetCal = Calendar.getInstance();
            birthCal.setTime(birth);
            targetCal.setTime(targetDate);

            int age = targetCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR); //simple maths to get the age by the difference of the two calendar objects
            if (targetCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) { //decrement age if the birthday hasn't been reached on the target year
                age--;
            }
            return age;
        } catch (ParseException e) {
            return 0; 
        }
    }

    // Simple getter for the suspended boolean
    public boolean getIsSuspended() {
        return isSuspended;
    }





    /**
     * A series of functions for the updatePersonalDetails() test cases.
     * @author s4058553
     */
    public boolean updatePersonalDetails(
        String newPersonID,
        String newFirstName,
        String newLastName,
        String newAddress,
        String newBirthdate
    ) {
        //Changing personal details will not affect their demerit points or the suspension status.
        // All relevant conditions discussed for the addPerson function also need to be considered and checked in the updatePerson function.
        //Condition 1: If a person is under 18, their address cannot be changed.
        //Condition 2: If a person's birthday is going to be changed, then no other personal detail (i.e, person's ID, firstName, lastName, address) can be changed.
        //Condition 3: If the first character/digit of a person's ID is an even number, then their ID cannot be changed.
        //Instruction: If the Person's updated information meets the above conditions and any other conditions you may want to consider,
        //the Person's information should be updated in the TXT file with the updated information, and the updatePersonalDetails function should return true.
        //Otherwise, the Person's updated information should not be updated in the TXT file, and the updatePersonalDetails function should return false.

        // Condition 2: If birthday is changing, no other field can change
        boolean isBirthdayChanging = !this.birthdate.equals(newBirthdate);
        boolean isOtherFieldChanging =
            !this.personID.equals(newPersonID) ||
            !this.firstName.equals(newFirstName) ||
            !this.lastName.equals(newLastName) ||
            !this.address.equals(newAddress);

        if (isBirthdayChanging && isOtherFieldChanging) {
            return false;
        }

        int currentAge = getAgeAtDate(new Date());
        // Condition 1: If under 18, address cannot be changed
        if (currentAge < 18 && !this.address.equals(newAddress)) {
            return false;
        }

        // Condition 3: If first digit of personID is even, ID cannot be changed
        char firstChar = this.personID.charAt(0);
        if (Character.isDigit(firstChar) && ((firstChar - '0') % 2 == 0) && !this.personID.equals(newPersonID)) {
            return false;
        }

        // validates newPersonID, newAddress, newBirthdate using addPerson rules
        if (!isValidPersonID(newPersonID) || !isValidAddress(newAddress) || !isValidBirthdate(newBirthdate)) {
            return false;
        }

        // read all persons from the file
        List<String> lines = new ArrayList<>();
        boolean found = false;
        String oldLine = this.toString();
        String updatedLine = new Person(newPersonID, newFirstName, newLastName, newAddress, newBirthdate).toString();

        try (BufferedReader reader = new BufferedReader(new FileReader("updatePersonalDetails_results.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // finds the line for this person
                if (line.equals(oldLine) && !found) {
                    lines.add(updatedLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            // if file doesn't exist, treat it as not found
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) {
            return false;
        }

        // write the lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("updatePersonalDetails_results.txt"))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // update
        this.personID = newPersonID;
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.address = newAddress;
        this.birthdate = newBirthdate;

        return true;
    }

    // helper methods for validation
    private boolean isValidPersonID(String id) {
        // check length
        if (id == null || id.length() != 10) return false;
        // checks first two chars
        if (!Character.isDigit(id.charAt(0)) || !Character.isDigit(id.charAt(1))) return false;
        int d1 = id.charAt(0) - '0', d2 = id.charAt(1) - '0';
        if (d1 < 2 || d1 > 9 || d2 < 2 || d2 > 9) return false;
        // check atleast two special chars between 3 and 8
        int specialCount = 0;
        for (int i = 2; i < 8; i++) {
            char c = id.charAt(i);
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        if (specialCount < 2) return false;
        // check last two chars
        if (!Character.isUpperCase(id.charAt(8)) || !Character.isUpperCase(id.charAt(9))) return false;
        return true;
    }

    private boolean isValidAddress(String address) {
        // format = Street Number|Street|City|State|Country, State must be Victoria
        if (address == null) return false; //return false if null
        String[] parts = address.split("\\|"); //split with '|' as delimiter
        if (parts.length != 5) return false; //return false if there aren't exactly 5 strings
        if (!parts[3].equals("Victoria")) return false; //return false if the state isn't correct
        return true;
    }

    private boolean isValidBirthdate(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT); //real date check
        try {
            LocalDate.parse(birthdate, formatter); //attempt to parse date
            return true;
        } catch (DateTimeParseException e) {
            return false; //can't parse the date
        }
    }

    //testing function
    public static String getLastLineFromFile(String filename) {
        String lastLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
        } catch (IOException e) {
            return "Failed to read from file.";
        }
        return lastLine;
    }
}