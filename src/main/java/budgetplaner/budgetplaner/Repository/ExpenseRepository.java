package budgetplaner.budgetplaner.Repository;

import budgetplaner.budgetplaner.Modell.Expense;
import budgetplaner.budgetplaner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
}

