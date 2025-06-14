package au.edu.rmit.sct;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
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

    //saves the person data in the default place, both are public because saving it to another file is used for testing
    public boolean addPerson() {
        return addPerson("addPerson_results.txt");
    }

    //adds person data to a file if the inputs are valid
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
        
        //check is valid demerit record
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

        //the record is valid, write to the file
        try (FileWriter writer = new FileWriter("addDemeritPoints_results.txt", true)) {
            //try to write the person's information and offense date to the text file
            writer.write(Person.demeritPointRecordToString(this, offenseDate, points) + "\n");
        } catch (IOException e) {
            return "Failed"; //return failed if the write fails
        }

        //get the total demerit points in the past 2 years from the current date
        Date currentDate = new Date();
        int recentPoints = calculateTotalPointsInPastYears(currentDate, 2);

        int age = getAgeAtDate(currentDate);

        //apply the suspension rules based on age and recent demerit points
        if ((age < 21 && recentPoints > 6) || age >= 21 && recentPoints > 12) {
            isSuspended = true;
        }

        return "Success";
    }

    // Helper function for addDemeritPoints:

    //a to string method for the record
    public static String demeritPointRecordToString(Person person, Date offenseDate, int points) {
        String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(offenseDate);
        return person.personID + "," + dateStr + "," + points;
    }

    // Totals the demerit points found in the text file within the last 200 years
    public int getTotalDemeritPoints() {
        return calculateTotalPointsInPastYears(new Date(), 200); // effectively gets all points
    }

    // Totals the demerit points found in the text file within the last x years.
    private int calculateTotalPointsInPastYears(Date currentDate, int years) {
        int sum = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Calculate the threshold date
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.YEAR, -years);
        Date thresholdDate = cal.getTime();

        try (BufferedReader reader = new BufferedReader(new FileReader("addDemeritPoints_results.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String id = parts[0].trim();
                String dateStr = parts[1].trim();
                String pointsStr = parts[2].trim();

                if (!id.equals(this.personID)) continue;

                try {
                    Date offenseDate = sdf.parse(dateStr);
                    int points = Integer.parseInt(pointsStr);

                    if (!offenseDate.before(thresholdDate)) {
                        sum += points;
                    }
                } catch (ParseException | NumberFormatException e) {
                    // Skip lines with invalid date or points
                }
            }
        } catch (IOException e) {
            // Optionally log error or handle file access failure
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

        // read all people from the file
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