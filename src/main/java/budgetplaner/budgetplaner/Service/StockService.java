package budgetplaner.budgetplaner.Service;

import budgetplaner.budgetplaner.Modell.Stock;
import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.StockRepository;
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

    public void deleteStockForUser(Long id, User user) {
        stockRepository.deleteByIdAndUser(id, user);
    }

    public void clearPortfolioForUser(User user) {
        stockRepository.deleteByUser(user);
    }
}