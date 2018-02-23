package answer.king.entity;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "T_ORDER")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private boolean paid;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL, CascadeType.PERSIST})
//    private List<LineItem> items = new ArrayList<>();
    private Map<Long, LineItem> items = new HashMap<>();

    public Order() {
    }

    public Order(boolean paid, BigDecimal total, Map<Long, LineItem> items) {
        this.paid = paid;
        this.total = total;
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Map<Long, LineItem> getItems() {
        if (items == null) items = new HashMap<>();
        return items;
    }

    public void setItems(Map<Long, LineItem> lineItems) {
        this.items = lineItems;
    }
}
