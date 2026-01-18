package budgetplaner.budgetplaner.Service;

import budgetplaner.budgetplaner.Modell.SavingsGoal;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.SavingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingsGoalService {

    private final SavingsRepository repo;

    public SavingsGoalService(SavingsRepository repo) {
        this.repo = repo;
    }

    public List<SavingsGoal> getForUser(User user) {
        return repo.findByUser(user);
    }

    public SavingsGoal create(SavingsGoal goal, User user) {
        goal.setUser(user);
        if (goal.getCurrentAmount() < 0) goal.setCurrentAmount(0);
        return repo.save(goal);
    }

    public SavingsGoal update(Long id, SavingsGoal updated, User user) {
        SavingsGoal existing = repo.findById(id).orElseThrow();

        // Safety: nur Owner darf updaten
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        existing.setTitle(updated.getTitle());
        existing.setTargetAmount(updated.getTargetAmount());
        existing.setCurrentAmount(updated.getCurrentAmount());
        existing.setDeadline(updated.getDeadline());

        return repo.save(existing);
    }

    public void delete(Long id, User user) {
        SavingsGoal existing = repo.findById(id).orElseThrow();
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }
        repo.delete(existing);
    }
}

