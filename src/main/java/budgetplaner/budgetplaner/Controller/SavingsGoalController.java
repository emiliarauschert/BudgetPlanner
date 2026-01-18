package budgetplaner.budgetplaner.Controller;

import budgetplaner.budgetplaner.Modell.SavingsGoal;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Service.SavingsGoalService;
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

    // Get (User)
    private User currentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    //Get
    @GetMapping
    public List<SavingsGoal> getGoals(Authentication authentication) {
        return service.getForUser(currentUser(authentication));
    }

    // Post
    @PostMapping
    public SavingsGoal create(@RequestBody SavingsGoal goal, Authentication authentication) {
        return service.create(goal, currentUser(authentication));
    }

    //Update
    @PutMapping("/{id}")
    public SavingsGoal update(@PathVariable Long id, @RequestBody SavingsGoal goal, Authentication authentication) {
        return service.update(id, goal, currentUser(authentication));
    }

    //Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, currentUser(authentication));
    }
}
