package interfaces;

import entities.Expense;

@FunctionalInterface
public interface Filtrable {
    Boolean cumpleFiltro(Expense expense);
}
