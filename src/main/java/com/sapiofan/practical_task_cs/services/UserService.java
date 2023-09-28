package com.sapiofan.practical_task_cs.services;

import com.sapiofan.practical_task_cs.dao.io.FileHandler;
import com.sapiofan.practical_task_cs.entities.User;
import com.sapiofan.practical_task_cs.exceptions.InappropriateUserException;
import com.sapiofan.practical_task_cs.exceptions.IncorrectDateOrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${age}")
    private int age;

    private final String emailRegex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)" +
            "*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private FileHandler fileHandler;

    public User addUser(User user) throws InappropriateUserException {
        if (user != null) {
            if (!validateUser(user)) {
                String error = "Some of user data are empty. Required fields: name, last name, email, birth date";
                log.error(error);
                throw new InappropriateUserException(error);
            }
            User createUser = fileHandler.createUser(user);
            log.info("User have been successfully created: " + user);
            return createUser;
        }
        log.warn("User is null. They haven't been created");
        return null;
    }

    public User updateUser(String email, String firstName, String lastName, Date birthDate, String address, String phone) {
        validateEmail(email);
        validateDate(birthDate);
        checkName(firstName, lastName);
        User updatedUser = fileHandler.updateUser(email, firstName, lastName, birthDate, address, phone);
        log.info("User have been successfully updated. Updated user email: " + email);
        return updatedUser;
    }

    public User updateUser(String email, User updatedUser) {
        validateEmail(email);
        if(!validateUser(updatedUser)) {
            String error = "Some of user data are empty. Required fields: name, last name, email, birth date";
            log.error(error);
            throw new InappropriateUserException(error);
        }
        User user = fileHandler.updateUser(email, updatedUser);
        log.info("User have been successfully updated. Updated user email before: " + email +
                "\n and after: " + user.getEmail());
        return user;
    }

    public void removeUser(String email) {
        fileHandler.deleteUser(email);
    }

    public List<User> findUsersByDates(Date from, Date to) {
        if (from.after(to)) {
            String error = "First date must be earlier. Current order: " + DATE_FORMAT.format(from)
                    + " - " + DATE_FORMAT.format(to);
            log.error(error);
            throw new IncorrectDateOrderException(error);
        }
        log.info("Users between suggested dates have been found");

        return fileHandler.searchUsersByDates(from, to);
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            String error = "Email can't be empty: " + email;
            log.error(error);
            throw new InappropriateUserException(error);
        }

        if (!Pattern.compile(emailRegex).matcher(email).matches()) {
            String error = "Email is incorrect: " + email;
            log.error(error);
            throw new InappropriateUserException(error);
        }

        return true;
    }

    private boolean validateDate(Date birthDate) {
        if (birthDate == null || birthDate.getTime() > new Date().getTime()) {
            String error = "Birth date can't be empty or after current time: " + DATE_FORMAT.format(birthDate);
            log.error(error);
            throw new InappropriateUserException(error);
        }
        long currentAge = ((new Date().getTime() - birthDate.getTime()) / (1000L * 60 * 60 * 24 * 365));
        if (currentAge < age) {
            String error = "User must have at least 18 years, but has: " + currentAge;
            log.error(error);
            throw new InappropriateUserException(error);
        }

        return true;
    }

    private boolean checkName(String firstName, String lastName) {
        if (firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty()) {
            String error = "First and last name can't be empty";
            log.error(error);
            throw new InappropriateUserException(error);
        }

        return true;
    }

    private boolean validateUser(User user) {
        return user != null &&
                user.getEmail() != null && !user.getEmail().isEmpty() && validateEmail(user.getEmail()) &&
                user.getBirthDate() != null && validateDate(user.getBirthDate()) &&
                checkName(user.getFirstName(), user.getLastName());
    }
}
