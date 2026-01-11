package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Expense;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Service.ExpenseService;
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

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping
    public List<Expense> getExpenses(Authentication auth) {
        return expenseService.getForUser(currentUser(auth));
    }

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense, Authentication auth) {
        expense.setUser(currentUser(auth));
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        return expenseService.save(expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
    }
}
