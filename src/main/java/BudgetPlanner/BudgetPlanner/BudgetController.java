package BudgetPlanner.BudgetPlanner;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class BudgetController {

    private List<Budget> budgets = new ArrayList<>();


    @GetMapping("/budgets")
    public List<Budget> getBudgets() {
        return budgets;
    }

    @PostMapping("/budgets")
    public String addBudget(@RequestBody Budget budget) {
        budgets.add(budget);
        return "Budget hinzugefügt";
    }
    @DeleteMapping("/budgets")
    public String clearBudgets() {
        budgets.clear();
        return "Alle Budgets gelöscht!";
    }

}





