package BudgetPlanner.BudgetPlanner.dto;

public class ReportRow {
    private String category;
    private double budgetLimit;
    private double spent;
    private double remaining;
    private double percentUsed;

    public ReportRow() {}

    public ReportRow(String category, double budgetLimit, double spent) {
        this.category = category;
        this.budgetLimit = budgetLimit;
        this.spent = spent;
        this.remaining = budgetLimit - spent;
        this.percentUsed = budgetLimit <= 0 ? 0 : Math.round((spent / budgetLimit) * 100.0);
    }

    public String getCategory() { return category; }
    public double getBudgetLimit() { return budgetLimit; }
    public double getSpent() { return spent; }
    public double getRemaining() { return remaining; }
    public double getPercentUsed() { return percentUsed; }

    public void setCategory(String category) { this.category = category; }
    public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }
    public void setSpent(double spent) { this.spent = spent; }
    public void setRemaining(double remaining) { this.remaining = remaining; }
    public void setPercentUsed(double percentUsed) { this.percentUsed = percentUsed; }
}

