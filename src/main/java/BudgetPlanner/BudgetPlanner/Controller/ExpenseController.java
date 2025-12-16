package BudgetPlanner.BudgetPlanner.Controller;
import BudgetPlanner.BudgetPlanner.Modell.Expense;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ExpenseController {

    private List<Expense> expenses = new ArrayList<>();

    @GetMapping("/expense")
    public List<Expense> getExpenses() {
        return expenses;
    }

    @PostMapping("/expense")
    public Expense addExpense(@RequestBody Expense entry) {
        expenses.add(entry);
        return entry;
    }

    @DeleteMapping("/expense")
    public String clearExpenses() {
        expenses.clear();
        return "Alle Expenses gelöscht!";
    }



}

