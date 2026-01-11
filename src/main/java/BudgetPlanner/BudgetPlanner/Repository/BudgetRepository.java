package BudgetPlanner.BudgetPlanner.Repository;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
}

