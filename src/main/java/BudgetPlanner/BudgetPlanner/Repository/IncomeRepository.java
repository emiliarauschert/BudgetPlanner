package BudgetPlanner.BudgetPlanner.Repository;

import BudgetPlanner.BudgetPlanner.Modell.Income;
import BudgetPlanner.BudgetPlanner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
}

