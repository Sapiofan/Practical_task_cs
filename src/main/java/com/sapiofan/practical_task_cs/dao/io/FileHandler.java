package com.sapiofan.practical_task_cs.dao.io;

import com.sapiofan.practical_task_cs.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class FileHandler {

    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    private static final String CSV_FILE_PATH = "src/main/resources/users.csv";
    private static final String CSV_DELIMITER = ",";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public FileHandler() {
        try {
            File csv = new File(CSV_FILE_PATH);
            if (csv.createNewFile()) {
                log.info("File created: " + csv.getName());
            } else {
                log.info("CSV file for users exists");
            }
        } catch (IOException e) {
            log.error("An error occurred while checking of file existence: " + e);
        }
    }

    public User createUser(User user) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true));
            writer.write(toStringForCsv(user));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            log.error("An error occurred while creating of a user: " + e);
        }
        return user;
    }

    public User updateUser(String email, String firstName, String lastName, Date birthDate, String address, String phone) {
        List<User> users = readUsersFromCsv();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setBirthDate(birthDate);
                user.setAddress(address);
                user.setPhone(phone);
                writeUsersToCsv(users);
                return user;
            }
        }

        return null;
    }

    public User updateUser(String email, User updatedUser) {
        List<User> users = readUsersFromCsv();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                user.setEmail(updatedUser.getEmail());
                user.setBirthDate(updatedUser.getBirthDate());
                user.setAddress(updatedUser.getAddress());
                user.setPhone(updatedUser.getPhone());
                writeUsersToCsv(users);
                return user;
            }
        }

        return null;
    }

    public void deleteUser(String email) {
        List<User> users = readUsersFromCsv();
        users.removeIf(user -> user.getEmail().equals(email));
        writeUsersToCsv(users);
    }

    public List<User> searchUsersByDates(Date from, Date to) {
        List<User> users = readUsersFromCsv();
        List<User> matchingUsers = new ArrayList<>();
        for (User user : users) {
            Date birthDate = user.getBirthDate();
            if (birthDate.after(from) && birthDate.before(to)) {
                matchingUsers.add(user);
            }
        }
        return matchingUsers;
    }

    private String toStringForCsv(User user) {
        return user.getFirstName() + CSV_DELIMITER + user.getLastName() + CSV_DELIMITER + user.getEmail() + CSV_DELIMITER +
                DATE_FORMAT.format(user.getBirthDate()) + CSV_DELIMITER + user.getAddress() + CSV_DELIMITER + user.getPhone();
    }

    private List<User> readUsersFromCsv() {
        List<User> users = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(CSV_FILE_PATH));
            while (scanner.hasNextLine()) {
                String[] userData = scanner.nextLine().split(CSV_DELIMITER);
                if (userData.length >= 4) {
                    String firstName = userData[0];
                    String lastName = userData[1];
                    String email = userData[2];
                    Date birthDate = DATE_FORMAT.parse(userData[3]);
                    String address = (userData.length > 4 && !userData[4].equalsIgnoreCase("null"))
                            ? userData[4] : null;
                    String phone = (userData.length > 5 && !userData[5].equalsIgnoreCase("null"))
                            ? userData[5] : null;
                    users.add(new User(firstName, lastName, email, birthDate, address, phone));
                }
            }
            scanner.close();
        } catch (IOException | ParseException e) {
            log.error("An error occurred while reading of users from csv file: " + e);
        }
        return users;
    }

    private void writeUsersToCsv(List<User> users) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH));
            for (User user : users) {
                writer.write(toStringForCsv(user));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            log.error("An error occurred while writing data in csv file: " + e);
        }
    }
}
