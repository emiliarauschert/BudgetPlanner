package budgetplaner.budgetplaner.Repository;

import budgetplaner.budgetplaner.Modell.Stock;
import budgetplaner.budgetplaner.Modell.User;
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
