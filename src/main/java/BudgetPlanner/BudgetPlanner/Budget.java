package BudgetPlanner.BudgetPlanner;

import java.util.List;

    public class Budget {
        private String month;
        private double limit;
        private List<Expense> expenses;

        public Budget(String month, double limit, List<Expense> expenses) {
            this.month = month;
            this.limit = limit;
            this.expenses = expenses;
        }

        public String getMonth() { return month; }
        public double getLimit() { return limit; }
        public List<Expense> getExpenses() { return expenses; }
    }

