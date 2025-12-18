import service.UserService;

import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Создать нового пользователя");
            System.out.println("2. Просмотреть всех пользователей");
            System.out.println("3. Найти пользователя по ID");
            System.out.println("4. Обновить данные пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("6. Выход");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    userService.createUser(scanner);
                    System.out.println("Нажмите Enter, чтобы вернуться в меню...");
                    scanner.nextLine();
                    break;
                case 2:
                    userService.getAllUsers();
                    System.out.println("Нажмите Enter, чтобы вернуться в меню...");
                    scanner.nextLine();
                    break;
                case 3:
                    userService.getUserById(scanner);
                    System.out.println("Нажмите Enter, чтобы вернуться в меню...");
                    scanner.nextLine();
                    break;
                case 4:
                    userService.updateUser(scanner);
                    System.out.println("Нажмите Enter, чтобы вернуться в меню...");
                    scanner.nextLine();
                    break;
                case 5:
                    userService.deleteUser(scanner);
                    System.out.println("Нажмите Enter, чтобы вернуться в меню...");
                    scanner.nextLine();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Некорректный выбор!");
            }
        }
    }
}