package answer.king.service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.LineItem;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.error.OrderAlreadyPaidException;
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
    private final ItemService itemService;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ItemService itemService,
                        OrderMapper orderMapper, PaymentService paymentService) {
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
        final LineItem lineItem = new LineItem();
        lineItem.setItem(item);
        lineItem.setOrder(order);
        lineItem.setQuantity(quantity);
        lineItem.setPrice(item.getPrice());

        order.getItems().add(lineItem);

        final BigDecimal totalItemPrice = item.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotal(order.getTotal().add(totalItemPrice));

        // persist and map to dto
        order = orderRepository.save(order);
        return orderMapper.mapToDto(order);
    }

    public ReceiptDto pay(long id, BigDecimal payment) throws InvalidPaymentException, NotFoundException, OrderAlreadyPaidException {
        if (payment == null || payment.signum() == -1) {
            throw new InvalidPaymentException();
        }

        final Order order = get(id);

        if (order.getItems().isEmpty()) throw new InvalidPaymentException();

        ReceiptDto receiptDto =  paymentService.pay(payment, order);
        System.out.println("Payment: " + receiptDto.getOrder().isPaid());

        return receiptDto;
    }
}
