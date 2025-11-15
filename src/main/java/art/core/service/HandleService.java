package art.core.service;

import art.cli.ShowcaseService;
import art.core.exception.PasswordNotFoundException;
import art.core.exception.UserAlreadyCreatedException;
import art.core.exception.UserNotFoundException;
import art.core.model.Transaction;
import art.core.model.User;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HandleService {
    private final LoginService loginService;
    private final BalanceService balanceService;
    private final Scanner scanner;
    private final FileService fileService;
    private final ShowcaseService showcaseService;
    public HandleService(LoginService loginService, BalanceService balanceService, Scanner scanner, FileService fileService, ShowcaseService showcaseService) {
        this.loginService = loginService;
        this.balanceService = balanceService;
        this.scanner = scanner;
        this.fileService = fileService;
        this.showcaseService = showcaseService;
    }
    //Метод изображения меню регистрации
    public void handleLoginMenu(){
        String param = scanner.nextLine().trim();
        switch (param) {
            case "1":
                handleRegister();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                fileService.save(loginService.getUserMap());
                System.exit(0);
                break;
            case "4":
                handleLoadJson();
                break;
            default:
                System.out.println("Некорректный ввод. Используйте вышеперечисленные параметры.");
        }
    }
    //Метод обработки логирования
    public boolean handleLogin(){
        System.out.println("Введите логин");
        String login = scanner.nextLine().trim();

        System.out.println("Введите пароль:");
        String password = scanner.nextLine().trim();

        try {
            loginService.login(login, password);
            System.out.println("Вы зашли в систему!");
            return true;
        }catch (UserNotFoundException e){
            System.out.println("Пользователь не найден! Попробуйте другой аккаунт.");
        }catch (PasswordNotFoundException e){
            System.out.println("Пароль не подходит!");
        }
        return false;
    }

    public void handleRegister(){
        System.out.println("Введите логин");
        String login = scanner.nextLine().trim();
        System.out.println("Введите пароль");
        String password = scanner.nextLine().trim();
        try {
            loginService.registration(login, password);
            System.out.println("Вы успешно прошли регистрацию!");
        }catch (UserAlreadyCreatedException e){
            System.out.println("Такой пользователь уже существует!");
        }catch (IllegalArgumentException e){
            System.out.println("Неправильный ввод");
        }
    }
    // Метод обработки главного меню
    public void handleMainMenu(){
        String param = scanner.nextLine().trim();
        switch (param) {
            case "1":
                handleTransactionMenu();
                break;
            case "2":
                handleBudgetCategory();
                break;
            case "3":
                handleStatistic();
                break;
            case "4":
                handleTransaction();
                break;
            case "5":
                loginService.unLogin();
                break;
            case "6":
                handleJsons();
                break;
            case "7":
                handleHelp();
                break;
            default:
                System.out.println("Вы ввели ошибочное значение. Повторите ещё раз");
        }
    }

    private void handleHelp() {
        System.out.println("Выберите категорию: \n 1. Помощь \n 2. Примеры");
        String help = scanner.nextLine().trim();
        switch (help) {
            case "1":
                showcaseService.printHelp();
                break;
            case "2":
                showcaseService.printExamples();
                break;
        }
    }

    private void handleJsons() {
        showcaseService.showJsons();
        String param = scanner.nextLine().trim();
        switch (param) {
            case "1":
                handleLoadJson();
                break;
            case "2":
                handleSaveJson();
                break;
            case "3":
                handleDeleteUser();
                break;
            case "4":
                break;
        }
    }

    private void handleDeleteUser() {
        System.out.println("Удалить текущий аккаунт? (Y/N)");
        switch (scanner.nextLine().trim().toUpperCase()) {
            case "Y":
                loginService.getUserMap().remove(loginService.getCurrentUser().getUsername());
                loginService.unLogin();
                break;
            case "N":
                break;
            default:
                System.out.println("Введите сообщение в подходящем формате (Y/n)");
        }
    }

    private void handleSaveJson() {
        fileService.saveJSON(loginService.getCurrentUser());
        System.out.println("Пользователь сохранен!");
    }

    private void handleLoadJson() {
        System.out.println("Введите файл. (Имя + .json)");
        String param = scanner.nextLine().trim();
        User loadedUser = fileService.loadJSON(new File(param));
        Map<String, User> userMap = loginService.getUserMap();
        if (userMap.containsKey(loadedUser.getUsername())) {
            System.out.println("Данный пользователь уже есть в системе! Перезаписать? (Y/n)");
            switch (scanner.nextLine().trim().toUpperCase()) {
                case "Y":
                    if (loginService.getCurrentUser().getUsername().equals(loadedUser.getUsername()) || handleLogin()){
                        userMap.replace(loadedUser.getUsername(), loadedUser);
                        loginService.setCurrentUser(loadedUser);
                    }
                    break;
                case "N":
                    break;
                default:
                    System.out.println("Введите сообщение в подходящем формате (Y/n)");
            }
        }else {
            System.out.println("Пользователь добавлен!");
            userMap.put(loadedUser.getUsername(), loadedUser);
        }
    }

    //Метод добавления дохода
    public void handleAddIncome() {
        User user = loginService.getCurrentUser();
        System.out.println("Введите категорию дохода");
        String income = scanner.nextLine().trim();
        System.out.println("Введите сумму дохода");
        Double amount = null;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
            balanceService.addIncome(user, income, amount);
        } catch (NumberFormatException e){
            System.out.println("Вы ввели слова вместо числа");
        } catch (IllegalArgumentException e){
            System.out.println("Сумма ввода не может быть отрицательной!");
        }
        System.out.println("Добавлен доход по категории " + income + " суммой: " + amount);

    }
    public void handleTransactionMenu(){
        showcaseService.showTransactionMenu();
        String param = scanner.nextLine().trim();
        switch (param) {
            case "1":
                handleAddIncome();
                break;
            case "2":
                handleAddOutcome();
                break;
            case "3":
                handleShowcaseTransaction();
                break;
            case "4":
                break;
            default:
                System.out.println("Введите подходящий вариант");
        }
    }
    // Метод добавления расхода
    public void handleAddOutcome(){
        User user = loginService.getCurrentUser();
        System.out.println("Введите категорию расхода");
        String outcome = scanner.nextLine().trim();
        System.out.println("Введите сумму расхода");
        Double amount = Double.parseDouble(scanner.nextLine().trim());
        try {
            balanceService.addOutcome(user, outcome, amount);
        }catch (IllegalArgumentException e){
            System.out.println("Сумма ввода не может быть отрицательной!");
        }
        System.out.println("Добавлен расход по категории " + outcome + " суммой: " + amount);
    }
    // Метод настройки начального баланса
    public void handleBudgetCategory(){
        System.out.println("1. Установить бюджет\n"+ "2. Удалить бюджет\n" + "3. Выход");
        String param = scanner.nextLine().trim();
        switch (param) {
            case "1":
                handleSetBudget();
                break;
            case "2":
                handleRemoveBudget();
                break;
            case "3":
                break;
            default:
                System.out.println("Введите подходящий вариант:");
        }
    }

    private void handleChangeTransaction() {
        try {
            User user = loginService.getCurrentUser();
            System.out.println("Введите номер транзакции");
            String transaction = scanner.nextLine().trim();
            List<Transaction> transactions = user.getWallet().getTransactions();
            if (!transactions.isEmpty()) {
                Transaction transactionObject = transactions.get(Integer.parseInt(transaction) - 1);
                while (true) {
                    showcaseService.showChangeTransaction();
                    String param = scanner.nextLine().trim();
                    switch (param) {
                        case "1":
                            System.out.println("Укажите название категории:");
                            String name = scanner.nextLine().trim();
                            transactionObject.setCategory(name);
                            break;
                        case "2":
                            System.out.println("Укажите сумму:");
                            String amount = scanner.nextLine().trim();
                            transactionObject.setAmount(Double.parseDouble(amount));
                            break;
                        case "3":
                            System.out.println("Укажите тип транзакции (В формате: Доход/Расход):");
                            String type = scanner.nextLine().trim();
                            switch (type) {
                                case "Доход":
                                    transactionObject.setIsIncome(true);
                                    break;
                                case "Расход":
                                    transactionObject.setIsIncome(false);
                                    break;
                                default:
                                    System.out.println("Введите корректный формат сообщения: Доход/Расход!");
                            }
                            break;
                        case "4":
                            return;
                        default:
                            System.out.println("Введите подходящий формат сообщения.");
                    }
                }
            }else {
                System.out.println("Список пуст! Добавьте транзакции.");
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println("Вы ввели значение вне списка!");
        }
    }


    private void handleRemoveTransaction() {
        System.out.println("Введите номер транзакции.");
        String param = scanner.nextLine().trim();
        User user = loginService.getCurrentUser();
        Transaction t = user.getWallet().getTransactions().remove(Integer.parseInt(param)-1);
        System.out.println("Удалена транзакция" + param);

    }

    private void handleRemoveBudget() {
        try{
            User user = loginService.getCurrentUser();
            System.out.println("Введите категорию бюджета");
            String category = scanner.nextLine().trim();
            user.getWallet().getBudgetsCategories().remove(category);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void handleSetBudget() {
        try{
            User user = loginService.getCurrentUser();
            System.out.println("Введите категорию бюджета");
            String category = scanner.nextLine().trim();
            System.out.println("Введите начальную сумму бюджета");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            balanceService.setBudget(user, category, amount);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // Метод демонстрации статистики

    public void handleShowcaseTransaction(){
        try {
            showcaseService.showAllTransactions();
            String param = scanner.nextLine().trim();
            switch (param) {
                case "1":
                    handleRemoveTransaction();
                    break;
                case "2":
                    handleChangeTransaction();
                    break;
                case "3":
                    break;
            }
        }catch (NumberFormatException e){
            System.out.println("Вы ввели не число, попробуйте ещё раз!");
        }

    }
    public void handleTransaction(){
        User user = loginService.getCurrentUser();
        System.out.println("Введите пользователя которому будет совершен перевод");
        User gainUser = loginService.getUserMap().get(scanner.nextLine().trim());
        System.out.println(gainUser);
        System.out.println("Введите сумму перевода");
        Double amount = Double.parseDouble(scanner.nextLine().trim());
        if (loginService.getUserMap().containsKey(gainUser.getUsername()) && !balanceService.outcomeOverIncomeAll(user)){
            balanceService.addOutcome(user, "Перевод", amount);
            balanceService.addIncome(gainUser, "Перевод", amount);
        } else if (balanceService.outcomeOverIncomeAll(user)) {
            System.out.println("Вы не можете перевести деньги, поскольку у вас отрицательный баланс");
        }
    }

    public void handleStatistic(){
        showcaseService.showStatistic();
        String cases = scanner.nextLine().trim();
        switch(cases){
            case "1":
                showcaseService.showAllStatistic();
                break;
            case "2":
                handleStatisticByCategory();
                break;
            case "3":
                break;
        }
    }
    private void handleStatisticByCategory(){
        LocalDateTime firstTime = null;
        LocalDateTime secondTime = null;
        try {
            System.out.println("Введите дату и время (в формате yyyy.MM.dd HH:mm:ss) от которой будет вестись поиск. \nОставьте пустой если нет необходимости");
            String timeFrom = scanner.nextLine().trim();
            firstTime = timeFrom.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(timeFrom, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

            System.out.println("Введите дату и время (в формате yyyy.MM.dd HH:mm:ss) до которой будет вестись поиск. \nОставьте пустой если нет необходимости");
            String timeTo = scanner.nextLine().trim();
            secondTime = timeTo.isEmpty() ? LocalDateTime.MAX : LocalDateTime.parse(timeTo, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        }catch (DateTimeParseException e){
            System.out.println("Не корректный формат ввода времени! Будет использоваться стандартное время поиска!");
            firstTime = LocalDateTime.MIN;
            secondTime = LocalDateTime.MAX;
        }

        System.out.println("Введите категории по которым будет осуществляться поиск. Разделяйте каждую категорию используя \",\"");
        String categories = scanner.nextLine().trim();
        showcaseService.showStatisticByCategory(firstTime, secondTime, categories.split(", "));
    }
}
