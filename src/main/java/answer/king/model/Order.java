package answer.king.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "T_ORDER")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Boolean paid = false;

	@OneToMany(mappedBy = "order", cascade = { CascadeType.ALL, CascadeType.PERSIST })
	private List<Item> items;

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
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Order)) return false;
		Order order = (Order) o;
		return Objects.equals(id, order.id) &&
				Objects.equals(paid, order.paid) &&
				Objects.equals(items, order.items);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, paid, items);
	}
}
