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

            addExpenses();
            addIncome();
        }

        public String getMonth() { return month; }
        public double getLimit() { return limit; }
        public List<Expense> getExpenses() { return expenses; }
        public List<Income> getIncome() { return income; }

        public void addExpenses() {
            Expense expense = new Expense("Miete", 500.00);
            expenses.add(expense);
            Expense expense2 = new Expense("Essen", 200.00);
            expenses.add(expense2);
            Expense expense3 = new Expense("Tanken", 500.00);
            expenses.add(expense3);
        }

        public void addIncome() {
            Income income = new Income("Gehalt", 2000.00);
        }


    }

