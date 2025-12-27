package BudgetPlanner.BudgetPlanner.Service;

import BudgetPlanner.BudgetPlanner.Modell.Stock;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getStocksForUser(User user) {
        return stockRepository.findByUser(user);
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }
}

