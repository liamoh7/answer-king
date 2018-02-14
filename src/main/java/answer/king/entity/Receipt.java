package answer.king.entity;

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

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receipt)) return false;
        Receipt receipt = (Receipt) o;
        return id == receipt.id &&
                Objects.equals(payment, receipt.payment) &&
                Objects.equals(order, receipt.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payment, order);
    }
}
