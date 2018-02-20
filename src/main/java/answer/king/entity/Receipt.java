package answer.king.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "T_RECEIPT")
public class Receipt {

    @Id
    @GeneratedValue
    private long id;
    private BigDecimal payment;

    @Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToOne
    private Order order;

    private BigDecimal change;

    public Receipt() {
    }

    public Receipt(BigDecimal payment, Order order) {
        this.payment = payment;
        this.order = order;
    }

    public Receipt(BigDecimal payment, Order order, BigDecimal change) {
        this.payment = payment;
        this.order = order;
        this.change = change;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receipt)) return false;
        Receipt receipt = (Receipt) o;
        return id == receipt.id &&
                Objects.equals(payment, receipt.payment) &&
                Objects.equals(order, receipt.order) &&
                Objects.equals(change, receipt.change);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payment, order, change);
    }
}
