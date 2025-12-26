package service;

import dao.UserDao;
import entity.User;
import exception.UserException;

import java.util.List;
import java.util.Optional;

public record UserService(UserDao<User, Long> userDao) {

    public List<User> getAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            throw new UserException("В системе пока нет пользователей");
        }
        return users;
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new UserException("Некорректный ID пользователя");
        }
        return userDao.findById(id);
    }

    public boolean saveUser(User user) {
        validateUser(user);
        return userDao.create(user);
    }

    public boolean updateUser(User user) {
        validateUser(user);
        if (userNotExist(user.getId())) {
            throw new UserException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userDao.update(user);
    }

    public boolean deleteUser(Long id) {
        if (userNotExist(id)) {
            throw new UserException("Пользователь с ID " + id + " не найден");
        }
        return userDao.delete(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserException("Пользователь не может быть null");
        }
        if (user.getUsername().isEmpty()) {
            throw new UserException("Имя пользователя обязательно");
        }
        if (user.getEmail().isEmpty()) {
            throw new UserException("Email пользователя обязателен");
        }
    }

    private boolean userNotExist(Long id) {
        return id == null || userDao.findById(id).isEmpty();
    }
}