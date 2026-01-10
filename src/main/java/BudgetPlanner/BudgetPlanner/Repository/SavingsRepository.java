package BudgetPlanner.BudgetPlanner.Repository;

import BudgetPlanner.BudgetPlanner.Modell.SavingsGoal;
import BudgetPlanner.BudgetPlanner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsRepository extends JpaRepository <SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}

