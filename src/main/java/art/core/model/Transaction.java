package art.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private Double amount;
    private String category;
    private Boolean isIncome;
    private LocalDateTime timestamp;

    public Transaction(Double amount, String category, Boolean isIncome) {
        this.amount = amount;
        this.category = category;
        this.isIncome = isIncome;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction t = (Transaction) o;
        return t.getCategory().equals(this.category) && t.getAmount().equals(this.amount) && t.getTimestamp().equals(this.timestamp) && t.getIsIncome().equals(this.isIncome);
    }

    @Override
    public int hashCode() {
        return amount.hashCode() + category.hashCode() + isIncome.hashCode() + timestamp.hashCode();
    }
}
