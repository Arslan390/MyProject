package service;

import entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
@Slf4j
public class UserMenuManager {

    private final Scanner scanner;
    private final UserService userService;
    private boolean running = true;

    public  void run() {
        while (running) {
            try {
                printMenu();
                int choice = getIntInput();

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> running = false;
                    default -> log.warn("Неверный выбор, попробуйте снова.");
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private void printMenu() {
        System.out.println("\nUSER_SERVICE");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private int getIntInput() {
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private long getLongInput() {
        long input = scanner.nextLong();
        scanner.nextLine();
        return input;
    }

    private void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = getIntInput();

        User newUser = User.builder()
                .username(name)
                .email(email)
                .age(age)
                .build();
        if (userService.saveUser(newUser)) {
            log.info("Пользователь {} создан.", name);
        }
    }

    private void findUserById() {
        System.out.print("Id пользователя для поиска : ");
        Long id = getLongInput();

        Optional<User> user = userService.getUserById(id);
        user.ifPresentOrElse(
                u -> log.info("Найден пользователь {}", u),
                () -> log.warn("Пользователь с Id {} не найден", id)
        );
    }

    private void findAllUsers() {
        List<User> users = userService.getAllUsers();
        log.info("Список пользователей\n");
        users.forEach(user -> log.info(user.toString()));
    }

    private void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = getLongInput();
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь c ID {} не найден.", id);
            return;
        }
        User user = optionalUser.get();
        log.info("Текущие данные {}", user);

        System.out.print("Введите новое имя (оставьте пустым для сохранения текущего): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) user.setUsername(name);

        System.out.print("Введите новый email (оставьте пустым для сохранения текущего): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Введите новый возраст (0 для сохранения текущего): ");
        int age = getIntInput();
        if (age > 0) user.setAge(age);

        if (userService.updateUser(user)) {
            log.info("Данные пользователя с Id {} успешно обновленны", id);
        }
    }

    private void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = getLongInput();
        if (userService.deleteUser(id)) {
            log.info("Пользователь с Id {} удален", id);
        }
    }

}