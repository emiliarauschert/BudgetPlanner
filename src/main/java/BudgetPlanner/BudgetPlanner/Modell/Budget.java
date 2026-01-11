package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month;        // z.B. 2026-01
    private double limitAmount;

    private String category;     // z.B. FOOD, RENT, FUN

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Budget() {}

    // Getter & Setter
    public Long getId() { return id; }
    public String getMonth() { return month; }
    public double getLimitAmount() { return limitAmount; }
    public String getCategory() { return category; }
    public User getUser() { return user; }

    public void setMonth(String month) { this.month = month; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public void setCategory(String category) { this.category = category; }
    public void setUser(User user) { this.user = user; }
}


