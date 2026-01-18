package budgetplaner.budgetplaner.Repository;

import budgetplaner.budgetplaner.Modell.Budget;
import budgetplaner.budgetplaner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
}

