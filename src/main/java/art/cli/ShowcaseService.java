/**
 * Service class for displaying menus and statistics in the application.
 */
package art.cli;

import art.cli.enums.Examples;
import art.cli.enums.Help;
import art.core.model.Transaction;
import art.core.model.User;
import art.core.service.BalanceService;
import art.core.service.LoginService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for displaying user interface elements and statistics.
 */
public class ShowcaseService {
    /**
     * Login service instance.
     */
    private LoginService loginService;

    /**
     * Constant for pecent limit of the budget.
     */
    private static final int LIMIT_PERCENTAGE = 80;
    /**
     * Balance service instance.
     */
    private BalanceService balanceService;

    /**
     * Format string for statistics.
     */
    private static final String REGEX_STATS = "%20s %n";

    /**
     * Constructor for ShowcaseService.
     *
     * @param loginServiceParam the login service
     * @param balanceServiceParam the balance service
     */
    public ShowcaseService(final LoginService loginServiceParam,
                           final BalanceService balanceServiceParam) {
        this.loginService = loginServiceParam;
        this.balanceService = balanceServiceParam;
    }

    /**
     * Displays the login menu.
     */
    public void showLoginMenu() {
        System.out.println("\n========== ВХОД / РЕГИСТРАЦИЯ ==========");
        System.out.println("1. Регистрация нового пользователя");
        System.out.println("2. Войти в существующий аккаунт");
        System.out.println("3. Выйти из приложения");
        System.out.println("4. Загрузить данные пользователя из файла");
        System.out.print("Выберите действие: ");
    }

    /**
     * Displays the main menu.
     */
    public void showMainMenu() {
        System.out.println("\n========== ГЛАВНОЕ МЕНЮ ==========");
        System.out.println("1. Управление транзакциями");
        System.out.println("2. Управление бюджетом");
        System.out.println("3. Просмотр статистики");
        System.out.println("4. Перевести деньги другому пользователю");
        System.out.println("5. Выйти из аккаунта");
        System.out.println("6. Управление данными пользователя");
        System.out.print("Выберите действие: ");
    }

    /**
     * Displays JSON operations menu.
     */
    public void showJsons() {
        System.out.println("\n========== УПРАВЛЕНИЕ ДАННЫМИ ПОЛЬЗОВАТЕЛЯ ==========");
        System.out.println("1. Загрузить данные пользователя из файла");
        System.out.println("2. Сохранить данные пользователя в файл");
        System.out.println("3. Удалить текущего пользователя");
        System.out.println("4. Назад в главное меню");
        System.out.print("Выберите действие: ");
    }

    /**
     * Displays transaction operations menu.
     */
    public void showTransactionMenu() {
        System.out.println("\n========== ТРАНЗАКЦИИ ==========");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Изменить существующую транзакцию");
        System.out.println("4. Вернуться назад");
        System.out.print("Выберите действие: ");
    }

    /**
     * Displays transaction editing options.
     */
    public void showChangeTransaction() {
        System.out.println("\n========== РЕДАКТИРОВАНИЕ ТРАНЗАКЦИИ ==========");
        System.out.println("1. Изменить категорию");
        System.out.println("2. Изменить сумму");
        System.out.println("3. Изменить тип (доход / расход)");
        System.out.println("4. Отмена");
        System.out.print("Выберите действие: ");
    }

    /**
     * Displays all transactions for the current user.
     */
    public void showAllTransactions() {
        User user = loginService.getCurrentUser();
        int[] num = {1};
        System.out.println("\n========== СПИСОК ТРАНЗАКЦИЙ ==========");
        System.out.printf("%-5s %-15s %-10s %-20s%n", "№", "Категория", "Сумма",
                "Дата и время");
        System.out.println("---------------------------------------------------------");
        user.getWallet().getTransactions().forEach(t -> {
            String income = t.getIsIncome() ? " +" : " -";
            System.out.printf("%-5s %-10s %-10s %-10s %n", num[0]++,
                    t.getCategory(), income + t.getAmount(), t.getTimestamp());
        });
        System.out.println("\nДальнейшие действия:");
        System.out.println("1. Удалить транзакцию");
        System.out.println("2. Изменить транзакцию");
        System.out.println("3. Вернуться в меню");
        System.out.print("Ваш выбор: ");
    }

    /**
     * Displays all statistics for the current user.
     */
    public void showAllStatistic() {
        User user = loginService.getCurrentUser();
        System.out.println("\n========== СТАТИСТИКА ==========");
        System.out.println("\n--- Общие данные ---");
        System.out.println("Общая сумма доходов: " + balanceService.getAllIncome(user));
        System.out.println("Общая сумма расходов: " + balanceService.getAllOutcome(user));
        System.out.printf(REGEX_STATS, "\nБюджеты:");
        printBudgets(user.getWallet().getBudgetsCategories());
        System.out.printf(REGEX_STATS, "\nДоходы по категориям:");
        printIncomes(balanceService.getIncomeByCategory(user));
        System.out.printf(REGEX_STATS, "\nРасходы по категориям:");
        printOutcomes(balanceService.getOutcomeByCategory(user));
    }

    /**
     * Displays statistics menu.
     */
    public void showStatistic() {
        System.out.println("\n========== МЕНЮ СТАТИСТИКИ ==========");
        System.out.println("1. Показать полную статистику");
        System.out.println("2. Показать статистику по времени и категориям");
        System.out.println("3. Вернуться в меню");
        System.out.print("Выберите вариант: ");
    }

    /**
     * Displays statistics by category and time period.
     *
     * @param firstTime start time
     * @param secondTime end time
     * @param categories array of categories
     */
    public void showStatisticByCategory(final LocalDateTime firstTime,
                                        final LocalDateTime secondTime,
                                        final String[] categories) {
        try {
            User user = loginService.getCurrentUser();
            Set<String> categoriesSet = new HashSet<>(Arrays.asList(categories));
            if (categoriesSet.isEmpty()) {
                categoriesSet.addAll(user.getWallet().getBudgetsCategories().keySet());
            }
            Map<String, Double> budgets = user.getWallet()
                    .getBudgetsCategories().entrySet().stream()
                    .filter(e -> categoriesSet.contains(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            System.out.println("\n========== СТАТИСТИКА ПО КАТЕГОРИИ ==========");
            System.out.println("Выбранный период: " + firstTime + " — " + secondTime);
            System.out.println("Категория(и): " + categoriesSet + "\n");

            printBudgets(budgets);

            List<Transaction> list = balanceService.getTransactionByCategories(
                    user, firstTime, secondTime, categoriesSet);

            Map<String, Double> mapOfIncomes = list.stream()
                    .filter(Transaction::getIsIncome)
                    .collect(Collectors.toMap(Transaction::getCategory,
                            Transaction::getAmount));
            printIncomes(mapOfIncomes);

            Map<String, Double> mapOfOutcomes = list.stream()
                    .filter(t -> !t.getIsIncome())
                    .collect(Collectors.toMap(Transaction::getCategory,
                            Transaction::getAmount));
            printOutcomes(mapOfOutcomes);
        } catch (NullPointerException e) {
            System.out.println("Ничего не найдено по выбранным категориям!");
        }
    }

    /**
     * Prints income statistics.
     *
     * @param getIncome map of categories to income amounts
     */
    public void printIncomes(final Map<String, Double> getIncome) {
        if (!getIncome.isEmpty()) {
            System.out.printf("%-20s %-10s%n", "Категория", "Доход");
            System.out.println("--------------------------------------");
            getIncome.forEach((k, v) -> System.out.printf("%-20s %-10.2f%n", k, v));
        } else {
            System.out.println("Нет данных о доходах.");
        }
    }

    /**
     * Prints budget information.
     *
     * @param budgets map of categories to budget amounts
     */
    public void printBudgets(final Map<String, Double> budgets) {
        if (!budgets.isEmpty()) {
            System.out.printf("%-20s %-10s%n", "Категория", "Бюджет");
            System.out.println("--------------------------------------");
            budgets.forEach((k, v) -> System.out.printf("%-20s %-10.2f%n", k, v));
        } else {
            System.out.println("Бюджеты отсутствуют.");
        }
    }

    /**
     * Prints outcome statistics with budget information.
     *
     * @param getOutcome map of categories to outcome amounts
     */
    public void printOutcomes(final Map<String, Double> getOutcome) {
        User user = loginService.getCurrentUser();

        if (!getOutcome.isEmpty()) {
            System.out.printf("%-15s %-10s %-10s %-10s %n",
                    "Категория", "Бюджет", "Расходы", "Остаток");
            System.out.println("--------------------------------------");
            getOutcome.forEach((k, v) -> {
                double budget = user.getWallet().getBudget(k) != null
                        ? user.getWallet().getBudget(k) : 0.0;
                double remaining = balanceService.getBudgetCategory(user, k);
                System.out.printf("%-15s %-10.1f %-10.1f %-10.1f",
                        k, budget, v, remaining);
                if (balanceService.budgetOverLimit(user, k)) {
                    System.out.print("\n! ^ Расход превышает лимит!");
                } else if (balanceService.budgetIsZero(user, k)) {
                    System.out.print("\n! ^ Бюджет исчерпан!");
                } else if (balanceService.budgetOverLimitPersent(user, k, LIMIT_PERCENTAGE)) {
                    System.out.print("\n ! ^ Осталось менее 20% бюджета!");
                }
            });
        } else {
            System.out.println("Нет данных о расходах.");
        }
        if (balanceService.outcomeOverIncomeAll(user)) {
            System.out.println("\nВаши общие расходы превышают доходы!");
        }
    }

    /**
     * Prints help information for all features.
     */
    public void printHelp() {
        System.out.println("\n========== СПРАВКА ПО МЕНЮ ==========\n");

        // --- Транзакции ---
        System.out.println("ТРАНЗАКЦИИ:");
        System.out.println("  " + Help.TRANSACTION_OPERATION.getDescription());
        System.out.println("  " + Help.TRANSACTION_ADD.getDescription());
        System.out.println("  " + Help.TRANSACTION_REMOVE.getDescription());
        System.out.println("  " + Help.TRANSACTION_UPDATE.getDescription());
        System.out.println();

        // --- Бюджеты ---
        System.out.println("БЮДЖЕТЫ:");
        System.out.println("  " + Help.BUDGET_OPERATION.getDescription());
        System.out.println("  " + Help.BUDGET_ADD.getDescription());
        System.out.println("  " + Help.BUDGET_REMOVE.getDescription());
        System.out.println();

        // --- Статистика ---
        System.out.println("СТАТИСТИКА:");
        System.out.println("  " + Help.STATISTICS_OPERATION.getDescription());
        System.out.println("  " + Help.STATISTICS_ALL.getDescription());
        System.out.println("  " + Help.STATISTICS_BY_CATEGORY.getDescription());
        System.out.println();

        // --- Переводы ---
        System.out.println("ПЕРЕВОДЫ:");
        System.out.println("  " + Help.TRANSACTION_OPERATION_SEND_TO_USER.getDescription());
        System.out.println();

        // --- Управление пользователем ---
        System.out.println("УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЕМ:");
        System.out.println("  " + Help.JSON_OPERATION.getDescription());
        System.out.println("  " + Help.JSON_UPLOAD.getDescription());
        System.out.println("  " + Help.JSON_UNLOAD.getDescription());
        System.out.println("  " + Help.DELETE_USER.getDescription());
        System.out.println();

        System.out.println("=====================================");
    }

    /**
     * Prints usage examples for all features.
     */
    public void printExamples() {
        System.out.println("\n========== ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ ==========\n");

        // --- Транзакции ---
        System.out.println("ТРАНЗАКЦИИ:");

        System.out.println("  Добавление дохода:");
        System.out.println("    " + Examples.ADD_INCOME.getDescription() + "\n");

        System.out.println("  Добавление расхода:");
        System.out.println("    " + Examples.ADD_OUTCOME.getDescription() + "\n");

        System.out.println("  Изменение транзакции:");
        System.out.println("    " + Examples.CHANGE_TRANSACTION.getDescription() + "\n");

        System.out.println("  Удаление транзакции:");
        System.out.println("    " + Examples.DELETE_TRANSACTION.getDescription() + "\n");

        // --- Бюджеты ---
        System.out.println("БЮДЖЕТЫ:");

        System.out.println("  Добавление бюджета:");
        System.out.println("    " + Examples.BUDGET_ADD.getDescription() + "\n");

        System.out.println("  Удаление бюджета:");
        System.out.println("    " + Examples.BUDGET_REMOVE.getDescription() + "\n");

        // --- Статистика ---
        System.out.println("СТАТИСТИКА:");

        System.out.println("  Полная статистика:");
        System.out.println("    " + Examples.STATS_FULL.getDescription() + "\n");

        System.out.println("  Статистика по категориям:");
        System.out.println("    " + Examples.STATS_BY_CATEGORY.getDescription() + "\n");

        // --- Перевод ---
        System.out.println("ПЕРЕВОД СРЕДСТВ:");

        System.out.println("  Пример перевода:");
        System.out.println("    " + Examples.TRANSFER_TO_USER.getDescription() + "\n");

        // --- Управление пользователем ---
        System.out.println("УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЕМ:");

        System.out.println("  Загрузка JSON:");
        System.out.println("    " + Examples.JSON_LOAD.getDescription() + "\n");

        System.out.println("  Сохранение JSON:");
        System.out.println("    " + Examples.JSON_SAVE.getDescription() + "\n");

        System.out.println("  Удаление пользователя:");
        System.out.println("    " + Examples.DELETE_USER.getDescription() + "\n");

        System.out.println("===========================================");
    }
}
