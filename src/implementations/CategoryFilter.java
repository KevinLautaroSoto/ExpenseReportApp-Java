package implementations;

import entities.Expense;
import interfaces.Filtrable;

public class CategoryFilter implements Filtrable {
    private String categoryFilter;

    public CategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    @Override
    public Boolean cumpleFiltro(Expense expense) {
        return expense.getCategory().getName().equalsIgnoreCase(categoryFilter);
    }
}
