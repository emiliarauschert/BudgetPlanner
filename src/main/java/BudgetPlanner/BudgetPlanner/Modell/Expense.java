package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.util.List;

public class Expense {

    private String name;
    private double amount;

    public Expense() {}

        public Expense(String name, double amount) {
        this.name = name;
        this.amount = amount;
        }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }


}
