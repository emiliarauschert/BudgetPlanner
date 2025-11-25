package BudgetPlanner.BudgetPlanner;

public class Income {
    private final String name;
    private final double amount;

    public Income(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getMonth() { return name; }
    public double getAmount() { return amount; }

}
