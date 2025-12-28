package service;

import dao.UserDao;
import entity.User;
import exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao<User, Long> userDao;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    /** registerUser
     * МЕТОД РЕГИСТРАЦИИ ПОЛЬЗОВАТЕЛЯ!
     * */

    @Test
    @DisplayName("registerUser: сохраняет нового пользователя, если данные валидны.")
    void registerUser_shouldSaveUser_whenValidData() {
        User user = createValidUser();
        when(userDao.create(any(User.class))).thenReturn(true);
        boolean result = userService.saveUser(user);
        assertTrue(result);
        verify(userDao).create(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        assertEquals(user, actualUser);
        verifyNoMoreInteractions(userDao);
    }

    /**
     * validateUser
     * МЕТОД ВАЛИДАЦИИ ПОЛЬЗОВАТЕЛЯ!
     *
     */

    @Test
    @DisplayName("validateUser: должен выбросить исключение, если user = null")
    void validateUser_shouldThrowException_whenUserIsNull() {
        Exception exception = assertThrows(UserException.class,() -> userService.saveUser(null));
        assertEquals("Пользователь не может быть null", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("validateUser: должен выбросить исключение, если email некорректный")
    void validateUser_shouldThrowException_whenInvalidEmail() {
        User user = createValidUser();
        user.setEmail("ismailov05.ru");
        Exception exception = assertThrows(UserException.class,() -> userService.saveUser(user));
        assertEquals("Некорректный адрес электронной почты", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("validateUser: должен выбросить исключение, если имя пустое")
    void validateUser_shouldThrowException_whenNameEmpty() {
        User user = createValidUser();
        user.setUsername("");
        Exception exception = assertThrows(UserException.class,() -> userService.saveUser(user));
        assertEquals("Имя пользователя обязательно", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    /** updateUser
     * МЕТОД ОБНОВЛЕНИЯ ПОЛЬЗОВАТЕЛЯ!
     * */

    @Test
    @DisplayName("updateUser: должен выбросить исключение, если пользователь с переданным ID не найден в базе данных")
    void updateUser_shouldThrowException_whenUserNotFound() {
        User user = createValidUser();
        user.setId(1L);
        when(userDao.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserException.class,() -> userService.updateUser(user));
        assertEquals("Пользователь с ID " + user.getId() + " не найден",  exception.getMessage());
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("updateUser: обновляет данные пользователя.")
    void updateUser_shouldUpdateExistingUser() {
        User user = createValidUser();
        user.setId(1L);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.update(user)).thenReturn(true);
        boolean result = userService.updateUser(user);
        assertTrue(result);
        verify(userDao).update(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        assertEquals(user, actualUser);
        verifyNoMoreInteractions(userDao);
    }

    /** deleteUser
     * МЕТОД УДАЛЕНИЯ ПОЛЬЗОВАТЕЛЯ!
     * */

    @Test
    @DisplayName("deleteUser: успешно удаляет пользователя, если он существует")
    void deleteUser_shouldReturnTrue_whenUserExistsAndDeleted() {
        Long id = 1L;
        when(userDao.findById(id)).thenReturn(Optional.of(new User()));
        when(userDao.delete(id)).thenReturn(true);
        boolean result = userService.deleteUser(id);
        assertTrue(result);
        verify(userDao).findById(id);
        verify(userDao).delete(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("deleteUser: должен выбросить исключение, если пользователь с переданным ID не найден в базе данных")
    void deleteUser_shouldThrowException_whenIdInvalid() {
        Long id = 1L;
        when(userDao.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserException.class,() -> userService.deleteUser(id));
        assertEquals("Пользователь с ID " + id + " не найден",  exception.getMessage());
        verifyNoMoreInteractions(userDao);
    }

    /** getUserById
     * МЕТОД ПОИСКА ПОЛЬЗОВАТЕЛЯ!
     * */

    @Test
    @DisplayName("findUserById: возвращает пользователя, если ID валиден и пользователь найден")
    void findUserById_shouldReturnUser_whenExists() {
        Long id = 1L;
        User user = createValidUser();
        when(userDao.findById(id)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(id);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("findUserById: бросает IllegalArgumentException, если ID == null")
    void findUserById_shouldThrowException_whenIdNull() {
        Exception exception = assertThrows(UserException.class,() -> userService.getUserById(null));
        assertEquals("Некорректный ID пользователя", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("findUserById: бросает IllegalArgumentException, если ID <= 0")
    void findUserById_shouldThrowException_whenIdLessThanZero() {
        Long id = -1L;
        Exception exception = assertThrows(UserException.class,() -> userService.getUserById(id));
        assertEquals("Некорректный ID пользователя", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    /** getAllUsers
     * МЕТОД ПОЛУЧЕНИЯ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ!
     * */

    @Test
    @DisplayName("getAllUsers: Тест на случай успешного возвращения списка пользователей")
    void getAllUsers_shouldReturnAllUsers_whenExists() {
        List<User> users = List.of(
                createValidUser(),
                User.builder().username("Ibrahim").email("ismailov006@yandex.ru").age(31).build()
        );
        when(userDao.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 2);
        assertEquals(users, result);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("getAllUsers: Тест на случай исключения, когда нет ни одного пользователя")
    void estGetAllUsers_NoUsersFound_ThrowsException() {
        when(userDao.findAll()).thenReturn(List.of());
        Exception exception = assertThrows(UserException.class,() -> userService.getAllUsers());
        assertEquals("В системе пока нет пользователей", exception.getMessage());
        verifyNoMoreInteractions(userDao);
    }


    private User createValidUser() {
        return User.builder()
                .username("Arslan")
                .email("ismailov@yandex.ru")
                .age(30)
                .build();
    }
}
