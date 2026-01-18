package budgetplaner.budgetplaner.Service;

import budgetplaner.budgetplaner.Modell.Income;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) {
        this.repo = repo;
    }

    public List<Income> getForUser(User user) {
        return repo.findByUser(user);
    }

    public Income save(Income income) {
        return repo.save(income);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}