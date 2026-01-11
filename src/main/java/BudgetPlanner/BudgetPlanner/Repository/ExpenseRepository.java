package BudgetPlanner.BudgetPlanner.Repository;

import BudgetPlanner.BudgetPlanner.Modell.Expense;
import BudgetPlanner.BudgetPlanner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
}

