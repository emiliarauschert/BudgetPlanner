package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String month;
    private double limit;
    //private List<Expense> expenses;
    //private List<Income> income;

    public Budget() {}

    public Budget(String month, double limit, List<Expense> expenses, List<Income> income) {
        this.month = month;
        this.limit = limit;
        //this.expenses = expenses;
       // this.income = income;
    }

    public int getId() {return id;}
    public String getMonth() { return month; }
    public double getLimit() { return limit; }
    //public List<Expense> getExpenses() { return expenses; }
    //public List<Income> getIncome() { return income; }

    public void setId(int id) {this.id = id;}
    public void setMonth(String month) {this.month = month;}
    public void setLimit(double limit) {this.limit = limit;}
    }

