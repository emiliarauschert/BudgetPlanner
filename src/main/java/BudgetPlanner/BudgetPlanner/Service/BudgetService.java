package BudgetPlanner.BudgetPlanner.Service;

import BudgetPlanner.BudgetPlanner.Modell.Budget;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getForUser(User user) {
        return budgetRepository.findByUser(user);
    }

    public void delete(Long id) {
        budgetRepository.deleteById(id);
    }
}

