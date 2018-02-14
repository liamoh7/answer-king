package answer.king.service;

import answer.king.dto.ItemDto;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.service.mapper.ItemMapper;
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
    private final ItemMapper itemMapper;
    private final ReceiptService receiptService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository,
                        OrderMapper orderMapper, ItemMapper itemMapper, ReceiptService receiptService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.receiptService = receiptService;
    }

    public OrderDto get(long id) {
        return orderMapper.mapToDto(orderRepository.getOne(id));
    }

    public List<OrderDto> getAll() {
        return orderMapper.mapToDto(orderRepository.findAll());
    }

    public OrderDto save(OrderDto order) {
        final Order entity = orderRepository.save(orderMapper.mapToEntity(order));
        return orderMapper.mapToDto(entity);
    }

    public OrderDto addItem(long id, long itemId) {
        final ItemDto item = itemMapper.mapToDto(itemRepository.findOne(itemId));
        OrderDto order = orderMapper.mapToDto(orderRepository.findOne(id));

        if (order == null || item == null) {
            return null;
        }

        // map the relationship between item & order
        item.setOrder(order);
        order.getItems().add(item);

        // update the running total
        // item price shouldn't need to be validated as it has already been persisted, thus validated.
        order = addToRunningTotal(order, item.getPrice());

        // Issue with where dto's boundary should end & entity's begins.
        final Order entity = orderMapper.mapToEntity(order);
        return orderMapper.mapToDto(orderRepository.save(entity));
    }

    public ReceiptDto pay(long id, BigDecimal payment) {
        if (payment == null || payment.signum() == -1) {
            return null;
        }

        final OrderDto order = orderMapper.mapToDto(orderRepository.findOne(id));

        // TODO: 13/02/2018 Look into handling better
        if (order == null || !order.getTotal().equals(payment)) return null;

        order.setPaid(true);
        return receiptService.create(order, payment);
    }

    private OrderDto addToRunningTotal(OrderDto order, BigDecimal amount) {
        if (order == null || amount == null) {
            // TODO: 13/02/2018 Handle
            return null;
        }

        final BigDecimal total = order.getTotal().add(amount);
        order.setTotal(total);
        return order;
    }
}
