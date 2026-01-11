package BudgetPlanner.BudgetPlanner.dto;

import java.util.List;

public class ReportResponse {
    private String month;
    private double incomeSum;
    private double expenseSum;
    private double net;
    private List<ReportRow> rows;

    public ReportResponse() {}

    public ReportResponse(String month, double incomeSum, double expenseSum, List<ReportRow> rows) {
        this.month = month;
        this.incomeSum = incomeSum;
        this.expenseSum = expenseSum;
        this.net = incomeSum - expenseSum;
        this.rows = rows;
    }

    public String getMonth() { return month; }
    public double getIncomeSum() { return incomeSum; }
    public double getExpenseSum() { return expenseSum; }
    public double getNet() { return net; }
    public List<ReportRow> getRows() { return rows; }

    public void setMonth(String month) { this.month = month; }
    public void setIncomeSum(double incomeSum) { this.incomeSum = incomeSum; }
    public void setExpenseSum(double expenseSum) { this.expenseSum = expenseSum; }
    public void setNet(double net) { this.net = net; }
    public void setRows(List<ReportRow> rows) { this.rows = rows; }
}
