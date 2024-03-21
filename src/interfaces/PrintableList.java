package interfaces;
import java.util.List;

@FunctionalInterface
public interface PrintableList <T> {
    void printList(List<T> list);
}
