package budgetplaner.budgetplaner.Controller;

import budgetplaner.budgetplaner.Modell.Expense;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Service.ExpenseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    // Get (User)
    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    // Get
    @GetMapping
    public List<Expense> getExpenses(Authentication auth) {
        return expenseService.getForUser(currentUser(auth));
    }

    // Post
    @PostMapping
    public Expense addExpense(@RequestBody Expense expense, Authentication auth) {
        expense.setUser(currentUser(auth));
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        return expenseService.save(expense);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
    }
}