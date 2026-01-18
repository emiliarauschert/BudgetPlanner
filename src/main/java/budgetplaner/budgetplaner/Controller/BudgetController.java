package budgetplaner.budgetplaner.Controller;

import budgetplaner.budgetplaner.Modell.Budget;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Service.BudgetService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepository;

    public BudgetController(BudgetService budgetService, UserRepository userRepository) {
        this.budgetService = budgetService;
        this.userRepository = userRepository;
    }

    // Get
    @GetMapping
    public List<Budget> getBudgets(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return budgetService.getForUser(user);
    }

    // Post
    @PostMapping
    public Budget addBudget(@RequestBody Budget budget, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        budget.setUser(user);
        return budgetService.save(budget);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetService.delete(id);
    }
}






