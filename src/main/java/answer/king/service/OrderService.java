package answer.king.service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.LineItem;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.service.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;
    private final ReceiptService receiptService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository,
                        OrderMapper orderMapper, ReceiptService receiptService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.receiptService = receiptService;
    }

    public Order get(long id) throws NotFoundException {
        final Order order = orderRepository.findOne(id);
        if (order == null) throw new NotFoundException();
        return order;
    }

    public OrderDto getMapped(long id) throws NotFoundException {
        return orderMapper.mapToDto(get(id));
    }

    public List<OrderDto> getAll() {
        return orderMapper.mapToDto(orderRepository.findAll());
    }

    public OrderDto save(OrderDto order) {
        final Order entity = orderRepository.save(orderMapper.mapToEntity(order));
        return orderMapper.mapToDto(entity);
    }

    public OrderDto addItem(long orderId, long itemId) throws NotFoundException {
        final Order order = get(orderId);
        final Item item = itemRepository.findOne(itemId);

        if (order == null || item == null) throw new NotFoundException();
        return addItemToOrder(order, item);
    }

    private OrderDto addItemToOrder(Order order, Item item) {
        final LineItem lineItem = new LineItem();
        lineItem.setItem(item);
        lineItem.setPrice(item.getPrice());
        lineItem.setQuantity(1);
        lineItem.setOrder(order);

        order.getItems().add(lineItem);
        order.setTotal(order.getTotal().add(item.getPrice()));

        // persist and map to dto
        order = orderRepository.save(order);
        return orderMapper.mapToDto(order);
    }

    public ReceiptDto pay(long id, BigDecimal payment) throws InvalidPaymentException, NotFoundException {
        if (payment == null || payment.signum() == -1) {
            throw new InvalidPaymentException();
        }

        final Order order = get(id);

        order.setPaid(true);
        return receiptService.create(order, payment);
    }
}
