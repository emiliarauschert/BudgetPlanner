package BudgetPlanner.BudgetPlanner;

import java.util.List;

    public class Budget {
        private String month;
        private double limit;
        private List<Expense> expenses;
        private List<Income> income;

        public Budget(String month, double limit, List<Expense> expenses, List<Income> income) {
            this.month = month;
            this.limit = limit;
            this.expenses = expenses;
            this.income = income;
        }

        public String getMonth() { return month; }
        public double getLimit() { return limit; }
        public List<Expense> getExpenses() { return expenses; }
        public List<Income> getIncome() { return income; }
    }

