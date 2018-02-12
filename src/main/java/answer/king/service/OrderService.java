package answer.king.service;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order addItem(long id, long itemId) {
        final Order order = orderRepository.findOne(id);
        final Item item = itemRepository.findOne(itemId);

        if (order == null || item == null) {
            return null;
        }

        // map the relationship between item & order
        item.setOrder(order);
        order.getItems().add(item);

        // items are persisted through cascading on mapping
        return orderRepository.save(order);
    }

    public Receipt pay(long id, BigDecimal payment) {
        Order order = orderRepository.findOne(id);

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setOrder(order);
        return receipt;
    }
}
