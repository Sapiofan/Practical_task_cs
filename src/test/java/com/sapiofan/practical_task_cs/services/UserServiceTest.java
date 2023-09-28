package com.sapiofan.practical_task_cs.services;

import com.sapiofan.practical_task_cs.dao.io.FileHandler;
import com.sapiofan.practical_task_cs.entities.User;
import com.sapiofan.practical_task_cs.exceptions.InappropriateUserException;
import com.sapiofan.practical_task_cs.exceptions.IncorrectDateOrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:custom.properties")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private FileHandler fileHandler;

    @Test
    public void addUserTest() {
        Date date = new Date();
        date.setTime(1000);
        User user = new User("name", "last name", "somemail@gmail.com", date);

        when(fileHandler.createUser(user)).thenReturn(user);
        userService.addUser(user);
        verify(fileHandler).createUser(user);

        List<User> users = getUsers();
        for (User userFromList : users) {
            assertThatThrownBy(() -> userService.addUser(userFromList)).isInstanceOf(InappropriateUserException.class);
        }
    }

    @Test
    public void updateUserTest() {
        Date date = new Date();
        date.setTime(1000);
        User user = new User("name", "last name", "somemail@gmail.com", date);
        User user2 = new User("name2", "last name2", "somemail@gmail.com", date,
                "address", "0505505050");

        when(fileHandler.updateUser("somemail@gmail.com", "name2", "last name2", date,
                "address", "0505505050")).thenReturn(user2);
        User user3 = userService.updateUser("somemail@gmail.com", "name2", "last name2", date,
                "address", "0505505050");
        verify(fileHandler).updateUser("somemail@gmail.com", "name2", "last name2", date,
                "address", "0505505050");
        Assertions.assertEquals(user2.getAddress(), user3.getAddress());
        Assertions.assertEquals(user2.getFirstName(), user3.getFirstName());
        Assertions.assertEquals(user2.getEmail(), user3.getEmail());

    }

    @Test
    public void updateUserFullyTest() {

        List<User> users = getUsers();
        for (User userFromList : users) {
            assertThatThrownBy(() -> userService.updateUser("somemail@gmail.com", userFromList))
                    .isInstanceOf(InappropriateUserException.class);
        }
    }

    @Test
    public void removeUserTest() {
        userService.removeUser("someimpossibleemail@domain.com.in");
        verify(fileHandler).deleteUser("someimpossibleemail@domain.com.in");
    }

    @Test
    public void findUsersByDatesTest() {
        Date date1 = new Date();
        Date date2 = new Date();
        date1.setTime(1000);

        assertThatThrownBy(() -> userService.findUsersByDates(date2, date1)).isInstanceOf(IncorrectDateOrderException.class);

        List<User> users = new ArrayList<>();
        when(fileHandler.searchUsersByDates(date1, date2)).thenReturn(users);
        userService.findUsersByDates(date1, date2);
        verify(fileHandler).searchUsersByDates(date1, date2);
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        Date date = new Date();
        date.setTime(1000);
        users.add(new User("", "last name", "somemail@gmail.com", date));
        users.add(new User("name", "", "somemail@gmail.com", date));
        users.add(new User("name", "last name", "somemail@gmail.com.", date));
        users.add(new User("name", "last name", ".somemail@gmail.com", date));
        users.add(new User("name", "last name", "somemailgmail.com", date));
        users.add(new User("name", "last name", "somemail@gmail", date));
        users.add(new User("name", "last name", "gmail.com", date));
        users.add(new User("name", "last name", "", date));
        date.setTime(new Date().getTime() - 1000);
        users.add(new User("name", "last name", "somemail@gmail.com", date));
        date.setTime(date.getTime() + 1000000);
        users.add(new User("name", "last name", "somemail@gmail.com", date));

        return users;
    }
}
