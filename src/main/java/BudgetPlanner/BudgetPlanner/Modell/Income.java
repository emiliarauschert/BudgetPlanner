package BudgetPlanner.BudgetPlanner.Modell;

public class Income {
    private final String name;
    private final double amount;

    public Income(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() { return name; }
    public double getAmount() { return amount; }
}
