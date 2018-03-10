package answer.king.dto;

import java.util.Objects;

public class CartItemDto {

    private long itemId;
    private int quantity;

    public CartItemDto() {
    }

    public CartItemDto(long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDto that = (CartItemDto) o;
        return itemId == that.itemId &&
                quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, quantity);
    }

    @Override
    public String toString() {
        return "CartItemDto{" +
                "itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }
}
