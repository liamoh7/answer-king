package answer.king.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class ItemDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @NotNull
    @Length(min = 1)
    private String name;

    @NotNull
    @Digits(integer = 15, fraction = 2)
    private BigDecimal price;

    @JsonIgnore
    private OrderDto order;

    public ItemDto() {
    }

    public ItemDto(String name, BigDecimal price, OrderDto order) {
        this.name = name;
        this.price = price;
        this.order = order;
    }

    public ItemDto(long id, String name, BigDecimal price, OrderDto order) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return id == itemDto.id &&
                Objects.equals(name, itemDto.name) &&
                Objects.equals(price, itemDto.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
}
