package budgetplaner.budgetplaner.Service;

import budgetplaner.budgetplaner.Modell.Expense;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    public List<Expense> getForUser(User user) {
        return repo.findByUser(user);
    }

    public Expense save(Expense expense) {
        return repo.save(expense);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
