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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order addItem(Long id, Long itemId) throws NullPointerException {
        Order order = orderRepository.findOne(id);
        Item item = itemRepository.findOne(itemId);

        if (order == null || item == null) {
            throw new NullPointerException();
        }

        item.setOrder(order);
        order.getItems().add(item);

        return orderRepository.save(order);
    }

    public Receipt pay(Long id, BigDecimal payment) {
        Order order = orderRepository.findOne(id);

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setOrder(order);
        return receipt;
    }
}
