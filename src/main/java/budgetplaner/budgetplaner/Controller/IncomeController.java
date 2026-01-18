package budgetplaner.budgetplaner.Controller;

import budgetplaner.budgetplaner.Modell.Income;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Service.IncomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
@CrossOrigin
public class IncomeController {

    private final IncomeService incomeService;
    private final UserRepository userRepository;

    public IncomeController(IncomeService incomeService, UserRepository userRepository) {
        this.incomeService = incomeService;
        this.userRepository = userRepository;
    }

    // Get (User)
    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    // Get
    @GetMapping
    public List<Income> getIncome(Authentication auth) {
        return incomeService.getForUser(currentUser(auth));
    }

    // Post
    @PostMapping
    public Income addIncome(@RequestBody Income income, Authentication auth) {
        income.setUser(currentUser(auth));
        if (income.getDate() == null) income.setDate(LocalDate.now());
        return incomeService.save(income);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteIncome(@PathVariable Long id) {
        incomeService.delete(id);
    }
}
