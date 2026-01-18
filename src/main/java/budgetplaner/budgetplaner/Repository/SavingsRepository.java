package budgetplaner.budgetplaner.Repository;

import budgetplaner.budgetplaner.Modell.SavingsGoal;
import budgetplaner.budgetplaner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsRepository extends JpaRepository <SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}

