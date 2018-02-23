package answer.king.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    private boolean paid;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal total = BigDecimal.ZERO;

//    private List<LineItemDto> items;
    private Map<Long, LineItemDto> items;

    public OrderDto() {
    }

    public OrderDto(boolean paid, BigDecimal total, Map<Long, LineItemDto> items) {
        this.paid = paid;
        this.total = total;
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Map<Long, LineItemDto> getItems() {
        if (items == null) items = new HashMap<>();
        return items;
    }

    public void setItems(Map<Long, LineItemDto> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDto)) return false;
        OrderDto orderDto = (OrderDto) o;
        return id == orderDto.id &&
                paid == orderDto.paid &&
                Objects.equals(total, orderDto.total) &&
                Objects.equals(items, orderDto.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paid, total, items);
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", paid=" + paid +
                ", total=" + total +
                ", items=" + items +
                '}';
    }
}
