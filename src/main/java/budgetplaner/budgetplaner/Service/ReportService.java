package budgetplaner.budgetplaner.Service;

import budgetplaner.budgetplaner.Modell.Budget;
import budgetplaner.budgetplaner.Modell.Expense;
import budgetplaner.budgetplaner.Modell.Income;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.BudgetRepository;
import budgetplaner.budgetplaner.Repository.ExpenseRepository;
import budgetplaner.budgetplaner.Repository.IncomeRepository;
import budgetplaner.budgetplaner.dto.ReportRow;
import budgetplaner.budgetplaner.dto.ReportResponse;
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
                        b -> normalizeCategory(b.getCategory()),
                        Collectors.summingDouble(Budget::getLimitAmount)
                ));

        // Ausgaben pro Kategorie
        Map<String, Double> spentByCat = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> normalizeCategory(e.getCategory()),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        // Alle Kategorien
        Set<String> categories = new TreeSet<>();
        categories.addAll(budgetByCat.keySet());
        categories.addAll(spentByCat.keySet());

        List<ReportRow> rows = categories.stream()
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

   //Kategorien robust
    private String normalizeCategory(String raw) {
        if (raw == null) return "OTHER";

        String s = raw.trim();
        if (s.isEmpty()) return "OTHER";

        // Entfernt führende Sonderzeichen
        s = s.replaceAll("^[^\\p{L}\\p{N}]+", "").trim();

        String upper = s.toUpperCase(Locale.ROOT);

        switch (upper) {
            case "FOOD":
            case "RENT":
            case "FUN":
            case "TRAVEL":
            case "TECH":
            case "OTHER":
                return upper;
        }

        // deutsche Labels / Varianten
        String lower = s.toLowerCase(Locale.ROOT);

        if (lower.contains("essen")) return "FOOD";
        if (lower.contains("miete")) return "RENT";
        if (lower.contains("freizeit")) return "FUN";
        if (lower.contains("reise")) return "TRAVEL";
        if (lower.contains("technik")) return "TECH";
        if (lower.contains("sonstig")) return "OTHER";

        // Fallback: wenn es nichts matcht, dann "OTHER"
        return "OTHER";
    }
}
