
package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month;
    private double limitAmount;
    private String category;
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Budget() {}

    // Getter
    public Long getId() { return id; }
    public String getMonth() { return month; }
    public double getLimitAmount() { return limitAmount; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public User getUser() { return user; }

    // Setter
    public void setMonth(String month) { this.month = month; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public void setCategory(String category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setUser(User user) { this.user = user; }
}
