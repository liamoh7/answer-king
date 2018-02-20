package answer.king.service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.entity.Receipt;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.repo.ReceiptRepository;
import answer.king.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptRepository repository;
    private final Mapper<ReceiptDto, Receipt> mapper;
    private final Mapper<OrderDto, Order> orderMapper;

    @Autowired
    public ReceiptService(ReceiptRepository repository, Mapper<ReceiptDto, Receipt> mapper, Mapper<OrderDto, Order> orderMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.orderMapper = orderMapper;
    }

    public List<ReceiptDto> getAll() {
        return mapper.mapToDto(repository.findAll());
    }

    public Receipt get(long id) throws NotFoundException {
        final Receipt receipt = repository.findOne(id);
        if (receipt == null) throw new NotFoundException();
        return receipt;
    }

    public ReceiptDto getMapped(long id) throws NotFoundException {
        return mapper.mapToDto(get(id));
    }

    public ReceiptDto create(Order order, BigDecimal paymentAmount) throws InvalidPaymentException, NotFoundException {
        if (order == null) throw new NotFoundException();
        if (paymentAmount == null) throw new InvalidPaymentException();

        final BigDecimal change = calculateChange(paymentAmount, order.getTotal());

        System.out.println(order.isPaid());

        Receipt receipt = new Receipt();
        receipt.setOrder(order);
        receipt.setPayment(paymentAmount);
        receipt.setChange(change);

        receipt = repository.save(receipt);
        System.out.println("Receipt: " + receipt.getOrder().isPaid());
        return mapper.mapToDto(receipt);
    }

    private BigDecimal calculateChange(BigDecimal paymentAmount, BigDecimal orderTotal) {
        final BigDecimal change = paymentAmount.subtract(orderTotal);
        return change.signum() != -1 ? change : BigDecimal.ZERO;
    }
}
