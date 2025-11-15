package art.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Transaction> transactions;
    private Map<String, Double> budgetsCategories;

    public Wallet() {
        this.transactions = new ArrayList<>();
        this.budgetsCategories = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public Transaction getTransaction(Transaction transaction) {
        return this.transactions.get(this.transactions.indexOf(transaction));
    }

    public void setBudget(String category, double amount) {
        this.budgetsCategories.put(category, amount);
    }

    public Double getBudget(String category) {
        return this.budgetsCategories.get(category);
    }

}
