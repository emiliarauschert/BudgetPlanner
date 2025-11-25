package BudgetPlanner.BudgetPlanner;

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
        return entry;
    }

    @DeleteMapping("/income")
    public String clearIncome() {
        income.clear();
        return "Alle Incomes gelöscht!";
    }
}

