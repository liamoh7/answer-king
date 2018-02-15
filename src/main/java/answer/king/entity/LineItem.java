package answer.king.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "T_LINE_ITEM")
public class LineItem {

    @Id
    @GeneratedValue
    private long id;
    private BigDecimal price;
    private int quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @OneToOne
    private Item item;

    public LineItem() {
    }

    public LineItem(BigDecimal price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public LineItem(BigDecimal price, int quantity, Order order) {
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }

    public LineItem(BigDecimal price, int quantity, Order order, Item item) {
        this.price = price;
        this.quantity = quantity;
        this.order = order;
        this.item = item;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineItem)) return false;
        LineItem lineItem = (LineItem) o;
        return id == lineItem.id &&
                quantity == lineItem.quantity &&
                Objects.equals(price, lineItem.price) &&
                Objects.equals(order, lineItem.order) &&
                Objects.equals(item, lineItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, quantity, order, item);
    }
}
