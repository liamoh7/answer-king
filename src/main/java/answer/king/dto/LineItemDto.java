package answer.king.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class LineItemDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @NotNull
    @Digits(integer = 15, fraction = 2)
    private BigDecimal price;

    private int quantity;

    @JsonIgnore
    private OrderDto order;

    private ItemDto item;

    public LineItemDto(BigDecimal price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public LineItemDto(BigDecimal price, int quantity, OrderDto order) {
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }

    public LineItemDto(BigDecimal price, int quantity, OrderDto order, ItemDto item) {
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

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public ItemDto getItem() {
        return item;
    }

    public void setItem(ItemDto item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineItemDto)) return false;
        LineItemDto that = (LineItemDto) o;
        return id == that.id &&
                quantity == that.quantity &&
                Objects.equals(price, that.price) &&
                Objects.equals(order.getId(), that.order.getId()) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, quantity, order.getId(), item);
    }
}
