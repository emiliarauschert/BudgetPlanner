package BudgetPlanner.BudgetPlanner.Repository;

import BudgetPlanner.BudgetPlanner.Modell.Stock;
import BudgetPlanner.BudgetPlanner.Modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByUser(User user);

    @Transactional
    void deleteByIdAndUser(Long id, User user);

    @Transactional
    void deleteByUser(User user);
}
