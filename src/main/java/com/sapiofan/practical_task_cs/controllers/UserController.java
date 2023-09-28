package com.sapiofan.practical_task_cs.controllers;

import com.sapiofan.practical_task_cs.entities.User;
import com.sapiofan.practical_task_cs.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/add")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/user/update")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestParam("email") String email,
                           @RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("birthDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date birthDate,
                           @RequestParam("address") String address,
                           @RequestParam("phone") String phone) {
        return userService.updateUser(email, firstName, lastName, birthDate, address, phone);
    }

    @PutMapping("/user/change")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestParam("email") String email, @RequestBody User updatedUser) {
        return userService.updateUser(email, updatedUser);
    }

    @DeleteMapping("/user/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestParam("email") String email) {
        userService.removeUser(email);
    }

    @GetMapping("/search/dates")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersByDates(@RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
                                      @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date to) {
        return userService.findUsersByDates(from, to);
    }
}
