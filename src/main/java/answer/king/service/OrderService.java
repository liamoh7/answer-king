package answer.king.service;

import answer.king.entity.Item;
import answer.king.entity.Order;
import answer.king.entity.Receipt;
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
        final Item item = itemRepository.findOne(itemId);
        Order order = orderRepository.findOne(id);

        if (order == null || item == null) {
            return null;
        }

        // map the relationship between item & order
        item.setOrder(order);
        order.getItems().add(item);

        // update the running total
        // item price shouldn't need to be validated as it has already been persisted, thus validated.
        order = addToRunningTotal(order, item.getPrice());

        // items are persisted through cascading on mapping
        return orderRepository.save(order);
    }

    public Receipt pay(long id, BigDecimal payment) {
        if (payment == null || payment.signum() == -1) {
            return null;
        }

        final Order order = orderRepository.findOne(id);

        // TODO: 13/02/2018 Look into handling better
        if (order == null || !order.getTotal().equals(payment)) return null;

        order.setPaid(true);
        final Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setOrder(order);
        return receipt;
    }

    private Order addToRunningTotal(Order order, BigDecimal amount) {
        if (order == null || amount == null) {
            // TODO: 13/02/2018 Handle
            return null;
        }

        final BigDecimal total = order.getTotal().add(amount);
        order.setTotal(total);
        return order;
    }
}
