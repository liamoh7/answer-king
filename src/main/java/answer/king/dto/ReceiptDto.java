package answer.king.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class ReceiptDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotNull
    @Min(value = 0)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal payment = BigDecimal.ZERO;

    @NotNull
    private OrderDto order;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal change = BigDecimal.ZERO;

    public ReceiptDto() {
    }

    public ReceiptDto(BigDecimal payment, OrderDto order) {
        this.payment = payment;
        this.order = order;
    }

    public ReceiptDto(BigDecimal payment, OrderDto order, BigDecimal change) {
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

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
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
        if (!(o instanceof ReceiptDto)) return false;
        ReceiptDto that = (ReceiptDto) o;
        return id == that.id &&
                Objects.equals(payment, that.payment) &&
                Objects.equals(order, that.order) &&
                Objects.equals(change, that.change);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, payment, order, change);
    }
}
