package answer.king.entity;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_ORDER")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean paid = false;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal change = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL, CascadeType.PERSIST})
    private List<Item> items = new ArrayList<>();

    public Order() {
    }

    public Order(Boolean paid, BigDecimal total, List<Item> items) {
        this.paid = paid;
        this.total = total;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public List<Item> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }
}
