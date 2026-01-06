package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Income;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class IncomeController {

    private List<Income> income = new ArrayList<>();

    @GetMapping("/income")
    public List<Income> getIncome() {
        return income;
    }

    @PostMapping("/income")
    public Income addIncome(@RequestBody Income entry) {
        income.add(entry);
        return entry;
    }

    @DeleteMapping("/income")
    public String clearIncome() {
        income.clear();
        return "Alle Incomes gelöscht!";
    }
}

