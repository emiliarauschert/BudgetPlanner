package BudgetPlanner.BudgetPlanner.Service;

import BudgetPlanner.BudgetPlanner.Modell.Expense;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.ExpenseRepository;
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
