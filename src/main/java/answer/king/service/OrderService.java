package answer.king.service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.LineItem;
import answer.king.entity.Order;
import answer.king.error.InvalidCriteriaException;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.error.OrderAlreadyPaidException;
import answer.king.repo.OrderRepository;
import answer.king.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final Mapper<OrderDto, Order> orderMapper;
    private final PaymentService paymentService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemService itemService,
                        Mapper<OrderDto, Order> orderMapper, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
        this.orderMapper = orderMapper;
        this.paymentService = paymentService;
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

    public OrderDto addItem(long orderId, long itemId, int quantity) throws NotFoundException, OrderAlreadyPaidException {
        final Order order = get(orderId);
        final Item item = itemService.get(itemId);

        if (order.isPaid()) throw new OrderAlreadyPaidException();
        if (quantity <= 0) quantity = 1;
        return addItemToOrder(order, item, quantity);
    }

    private OrderDto addItemToOrder(Order order, Item item, int quantity) {
        // check if lineitem exists for current item
        final LineItem lineItem = order.getItems().getOrDefault(item.getId(), new LineItem());

        lineItem.setItem(item);
        lineItem.setOrder(order);
        lineItem.setPrice(item.getPrice());

        // if new item, quantity = 0, otherwise existing quantity
        lineItem.setQuantity(lineItem.getQuantity() + quantity);

        order.getItems().put(item.getId(), lineItem);

        final BigDecimal totalItemPrice = item.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotal(order.getTotal().add(totalItemPrice));

        // persist and map to dto
        order = orderRepository.save(order);
        return orderMapper.mapToDto(order);
    }

    public OrderDto removeItemFromOrder(long orderId, long itemId, int quantity) throws NotFoundException, InvalidCriteriaException, OrderAlreadyPaidException {
        final Order order = get(orderId);
        final LineItem item = order.getItems().get(itemId);

        if (order.isPaid()) throw new OrderAlreadyPaidException();
        if (item == null || quantity <= 0) throw new InvalidCriteriaException();
        if (quantity > item.getQuantity()) {
            // remove lineitem completely as all quantity should be removed
            quantity = item.getQuantity();
            order.getItems().remove(itemId);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }

        // update order balance
        final BigDecimal total = order.getTotal().subtract(item.getPrice().multiply(new BigDecimal(quantity)));
        order.setTotal(total);

        return orderMapper.mapToDto(orderRepository.save(order));
    }

    public ReceiptDto pay(long id, BigDecimal payment) throws InvalidPaymentException, NotFoundException, OrderAlreadyPaidException {
        if (payment == null || payment.signum() == -1) {
            throw new InvalidPaymentException();
        }

        final Order order = get(id);

        if (order.getItems().isEmpty()) throw new InvalidPaymentException();

        return paymentService.pay(payment, order);
    }
}
