package budgetplaner.budgetplaner.Repository;

import budgetplaner.budgetplaner.Modell.Income;
import budgetplaner.budgetplaner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
}

