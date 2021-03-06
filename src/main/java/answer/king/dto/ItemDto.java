package answer.king.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
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
    @Min(value = 0)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    private String description;

    private CategoryDto category;

    public ItemDto() {
    }

    public ItemDto(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public ItemDto(String name, BigDecimal price, String description, CategoryDto category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return id == itemDto.id &&
                Objects.equals(name, itemDto.name) &&
                Objects.equals(price, itemDto.price) &&
                Objects.equals(description, itemDto.description) &&
                Objects.equals(category, itemDto.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, description, category);
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", category=" + category +
                '}';
    }
}
