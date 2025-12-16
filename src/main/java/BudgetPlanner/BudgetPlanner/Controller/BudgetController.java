package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Service.BudgetService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173","https://budgetplanner-frontend.onrender.com"})
@RestController
@RequestMapping("/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/budgets")
    public Budget getBudgets() {
        return budgetService.getAllBudgets();
    }

    @PostMapping("/budgets")
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetService.saveBudget(budget);
    }
    @DeleteMapping("/budgets")
    public String clearBudgets() {
        return "Alle Budgets gelöscht!";
    }

}





