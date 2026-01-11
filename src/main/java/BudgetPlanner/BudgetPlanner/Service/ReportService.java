package BudgetPlanner.BudgetPlanner.Service;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Modell.Expense;
import BudgetPlanner.BudgetPlanner.Modell.Income;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.BudgetRepository;
import BudgetPlanner.BudgetPlanner.Repository.ExpenseRepository;
import BudgetPlanner.BudgetPlanner.Repository.IncomeRepository;
import BudgetPlanner.BudgetPlanner.dto.ReportRow;
import BudgetPlanner.BudgetPlanner.dto.ReportResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public ReportService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository,
            IncomeRepository incomeRepository
    ) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public ReportResponse buildReport(User user, String month) {
        // month: "YYYY-MM"
        YearMonth ym = YearMonth.parse(month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<Budget> budgets = budgetRepository.findByUser(user).stream()
                .filter(b -> month.equals(b.getMonth()))
                .toList();

        List<Expense> expenses = expenseRepository.findByUser(user).stream()
                .filter(e -> e.getDate() != null && !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .toList();

        List<Income> incomes = incomeRepository.findByUser(user).stream()
                .filter(i -> i.getDate() != null && !i.getDate().isBefore(from) && !i.getDate().isAfter(to))
                .toList();

        // Budget-Limits pro Kategorie
        Map<String, Double> budgetByCat = budgets.stream()
                .collect(Collectors.groupingBy(
                        Budget::getCategory,
                        Collectors.summingDouble(Budget::getLimitAmount)
                ));

        // Ausgaben pro Kategorie
        Map<String, Double> spentByCat = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        // Alle Kategorien (Union)
        Set<String> categories = new HashSet<>();
        categories.addAll(budgetByCat.keySet());
        categories.addAll(spentByCat.keySet());

        List<ReportRow> rows = categories.stream()
                .sorted()
                .map(cat -> new ReportRow(
                        cat,
                        budgetByCat.getOrDefault(cat, 0.0),
                        spentByCat.getOrDefault(cat, 0.0)
                ))
                .toList();

        double incomeSum = incomes.stream().mapToDouble(Income::getAmount).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getAmount).sum();

        return new ReportResponse(month, incomeSum, expenseSum, rows);
    }
}

