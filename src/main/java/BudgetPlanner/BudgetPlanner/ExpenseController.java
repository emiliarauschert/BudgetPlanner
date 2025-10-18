package BudgetPlanner.BudgetPlanner;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ExpenseController {

    private List<Expense> expenses = new ArrayList<>();

    @GetMapping("/expense")
    public List<Expense> getExpenses() {
        return getExpenses();
    }

    @PostMapping("/expense")
    public Expense addExpense(@RequestBody Expense entry) {
        return addExpense(entry);
    }

    @DeleteMapping("expense")
    public String clearExpenses() {
        expenses.clear();
        return "Alle Expenses gelöscht!";
    }



}

