package BudgetPlanner.BudgetPlanner;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","https://budgetplanner-frontend.onrender.com"})
@RestController
public class BudgetController {



    private List<Budget> budgets = new ArrayList<>(List.of(
            new Budget("November", 1500.00,
                    new ArrayList<>(List.of(
                            new Expense("Miete", 500.00),
                            new Expense("Essen", 200.00),
                            new Expense("Tanken", 100.00)
                    )),
                    new ArrayList<>(List.of(
                            new Income("Gehalt", 2000.00)
                    ))
            ),
            new Budget("Dezember", 1800.00,
                    new ArrayList<>(List.of(
                            new Expense("Miete", 500.00),
                            new Expense("Essen", 250.00),
                            new Expense("Geschenke", 150.00)
                    )),
                    new ArrayList<>(List.of(
                            new Income("Gehalt", 2000.00)
                    ))
            )
    ));

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





