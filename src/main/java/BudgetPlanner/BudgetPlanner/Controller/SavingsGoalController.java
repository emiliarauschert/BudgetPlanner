package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.SavingsGoal;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Service.SavingsGoalService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@CrossOrigin
public class SavingsGoalController {

    private final SavingsGoalService service;
    private final UserRepository userRepository;

    public SavingsGoalController(SavingsGoalService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    @GetMapping
    public List<SavingsGoal> getGoals(Authentication authentication) {
        return service.getForUser(currentUser(authentication));
    }

    @PostMapping
    public SavingsGoal create(@RequestBody SavingsGoal goal, Authentication authentication) {
        return service.create(goal, currentUser(authentication));
    }

    @PutMapping("/{id}")
    public SavingsGoal update(@PathVariable Long id, @RequestBody SavingsGoal goal, Authentication authentication) {
        return service.update(id, goal, currentUser(authentication));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, currentUser(authentication));
    }
}
