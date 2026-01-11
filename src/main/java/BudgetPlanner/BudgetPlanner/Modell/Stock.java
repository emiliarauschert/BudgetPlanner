package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private String name;

    private double quantity;

    private double buyPrice;

    private LocalDate buyDate;

    // Zuordnung zum User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public double getQuantity(){
        return quantity;
    }
    public void setQuantity(double quantity){
        this.quantity = quantity;
    }
    public double getBuyPrice(){
        return buyPrice;
    }
    public void setBuyPrice(double buyPrice){
        this.buyPrice = buyPrice;
    }
    public LocalDate getBuyDate(){
        return buyDate;
    }
    public void setBuyDate(LocalDate buyDate){
        this.buyDate = buyDate;
    }
    public User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public String getSymbol(){
        return symbol;
    }
    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

