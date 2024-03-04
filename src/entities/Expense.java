package entities;

public class Expense {
    //Gastos: monto, descripcion, categoria y fecha

    private double amount;
    private String description;
    private ExpenseCategory category;
    private String date;

    public Expense() {
    }

    public Expense(double amount, String description, ExpenseCategory category, String date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", date='" + date + '\'' +
                '}';
    }
}
