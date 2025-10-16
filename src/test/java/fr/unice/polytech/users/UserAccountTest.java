package fr.unice.polytech.users;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserAccountTest {

    private final String NAME = "John";
    private final String SURNAME = "Doe";
    private final String EMAIL = "john.doe@polytech.unice.fr";

    // Test 1: Constructor with name and surname only
    @Test
    void testUserAccountConstructor_TwoArgs() {
        UserAccount user = new UserAccount(NAME, SURNAME);
        
        assertEquals(NAME, user.getName(), "Name should be initialized.");
        assertEquals(SURNAME, user.getSurname(), "Surname should be initialized.");
        assertNull(user.getEmail(), "Email should be null when not provided.");
    }

    // Test 2: Constructor with name, surname, and email
    @Test
    void testUserAccountConstructor_ThreeArgs() {
        UserAccount user = new UserAccount(NAME, SURNAME, EMAIL);
        
        assertEquals(NAME, user.getName(), "Name should be initialized.");
        assertEquals(SURNAME, user.getSurname(), "Surname should be initialized.");
        assertEquals(EMAIL, user.getEmail(), "Email should be initialized.");
    }

    // Test 3: Setters for all attributes
    @Test
    void testSetters() {
        UserAccount user = new UserAccount("OldName", "OldSurname", "old@mail.com");

        user.setName("NewName");
        user.setSurname("NewSurname");
        user.setEmail("new@mail.com");

        assertEquals("NewName", user.getName(), "Name setter should update the name.");
        assertEquals("NewSurname", user.getSurname(), "Surname setter should update the surname.");
        assertEquals("new@mail.com", user.getEmail(), "Email setter should update the email.");
    }
}