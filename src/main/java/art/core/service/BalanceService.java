package art.core.service;

import art.core.model.Transaction;
import art.core.model.User;
import art.core.model.Wallet;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceService {

    public void addIncome(User user, String category, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Wallet userWallet = user.getWallet();
        Transaction transaction = new Transaction(amount, category, true);
        userWallet.addTransaction(transaction);
    }
    public void addOutcome(User user, String category, double amount) {
        Wallet userWallet = user.getWallet();
        Transaction transaction = new Transaction(amount, category, false);
        userWallet.addTransaction(transaction);
    }
    public double getAllIncome(User user) {
        Wallet userWallet = user.getWallet();
        return userWallet.getTransactions().stream().filter(transaction -> transaction.getIsIncome()).collect(Collectors.summarizingDouble(Transaction::getAmount)).getSum();
    }
    public double getAllOutcome(User user) {
        Wallet userWallet = user.getWallet();
        return userWallet.getTransactions().stream().filter(transaction -> !transaction.getIsIncome()).collect(Collectors.summarizingDouble(Transaction::getAmount)).getSum();
    }
    public Map<String,Double> getIncomeByCategory(User user ) {
        Wallet userWallet = user.getWallet();
        return userWallet.getTransactions().stream()
                .filter(t -> t.getIsIncome())
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }
    public Map<String,Double> getOutcomeByCategory(User user) {
        Wallet userWallet = user.getWallet();
        return userWallet.getTransactions().stream()
                .filter(t -> !t.getIsIncome())
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }

    public Double getBudgetCategory(User user, String category) {
        return getBudget(user,category) - getSpent(user,category);
    }
    public double getBudget(User user, String category) {
        return user.getWallet().getBudget(category) != null ?
                user.getWallet().getBudget(category) : 0.0 ;
    }
    public double getSpent(User user, String category) {
        return getOutcomeByCategory(user).getOrDefault(category, 0.0);
    }
    //Оповещать пользователя, если превышен лимит бюджета по категории
    public boolean budgetOverLimit(User user, String category) {
       return getBudgetCategory(user, category) < 0;
    }
    public boolean budgetIsZero(User user, String category) {
        return getBudgetCategory(user, category) == 0;
    }
    public boolean budgetOverLimitPersent(User user, String category, double percent) {
        return ((getBudget(user,category) * percent) / 100 <= getSpent(user,category));
    }
    // Оповещать пользователя, если расходы превысили доходы.
    public boolean outcomeOverIncomeAll(User user) {
        return getAllIncome(user) < getAllOutcome(user);
    }
    public void setBudget(User user, String category, double amount) {
        Wallet userWallet = user.getWallet();
        userWallet.setBudget(category, amount);
    }
    //Гибкий выбор категорий или периода, корректные уведомления при отсутствии данных
    //Тут сделать обработку ошибки, духовно тут если выпала ошибка, значит, нету указанной категории (Пока ошибка возможная - пустая мапа. Если она пустая - значит ничего не нашел)
    public List<Transaction> getTransactionByCategories(User user, Set categories) throws NullPointerException {
        Wallet userWallet = user.getWallet();

        return userWallet.getTransactions().stream()
                .filter(t -> categories.contains(t.getCategory())).collect(Collectors.toList());
    }
    public List<Transaction> getTransactionByCategories(User user, LocalDateTime timeFrom, LocalDateTime timeTo, Set categories) throws NullPointerException {
        List<Transaction> list = getTransactionByCategories(user, categories);
        return list.stream()
                .filter(t -> t.getTimestamp().isAfter(timeFrom) && t.getTimestamp().isBefore(timeTo))
                .collect(Collectors.toList());
    }


}
