package service;

import dao.UserDao;
import entity.User;
import exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao<User, Long> userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_validUser_savesSuccessfully() {
        User user = createValidUser();
        userService.saveUser(user);
        verify(userDao).create(user);
    }

    @Test
    void saveUser_invalidUser_throwsException() {
        assertThrows(UserException.class, () -> userService.saveUser(null));
        verifyNoInteractions(userDao);
    }

    @Test
    void saveUser_blankName_throwsException() {
        User user = createValidUser();
        user.setUsername("");
        assertThrows(UserException.class, () -> userService.saveUser(user));
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_validId_returnsUser() {
        Long id = 1L;
        User expectedUser = createValidUser();
        when(userDao.findById(id)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(id);
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void getUserById_invalidId_throwsException() {
        assertThrows(UserException.class, () -> userService.getUserById(0L));
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_nonExistentId_returnsEmptyOptional() {
        Long id = 999L;
        when(userDao.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_nonEmptyList_returnsUsers() {
        List<User> users = List.of(createValidUser());
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllUsers_emptyList_throwsException() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());
        assertThrows(UserException.class, () -> userService.getAllUsers());
    }

    @Test
    void updateUser_validUser_updatesSuccessfully() {
        User user = createValidUser();
        user.setId(1L);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    void updateUser_nonExistentUser_throwsException() {
        User user = createValidUser();
        user.setId(999L);
        when(userDao.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.updateUser(user));
        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_validUser_deletesSuccessfully() {
        Long validUserId = 1L;
        User existingUser = createValidUser();
        existingUser.setId(validUserId);
        when(userDao.findById(validUserId)).thenReturn(Optional.of(existingUser));
        when(userDao.delete(validUserId)).thenReturn(true);

        boolean deleted = userService.deleteUser(validUserId);
        verify(userDao).delete(validUserId);

        assertTrue(deleted);
    }

    @Test
    void deleteUser_nonExistentUser_throwsException() {
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.deleteUser(1L));
    }


    private User createValidUser() {
        return User.builder()
                .username("Valid Name")
                .email("valid@email.com")
                .age(30)
                .build();
    }
}
