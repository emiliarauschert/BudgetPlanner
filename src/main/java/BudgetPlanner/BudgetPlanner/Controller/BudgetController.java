package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","https://budgetplanner-frontend.onrender.com"})
@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<Budget> getBudgets() {
        return budgetService.getAllBudgets();
    }

    @PostMapping
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetService.saveBudget(budget);
    }
    @DeleteMapping
    public String clearBudgets() {
        return "Alle Budgets gelöscht!";
    }

}





