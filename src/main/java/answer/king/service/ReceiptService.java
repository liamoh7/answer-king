package answer.king.service;

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

import static answer.king.util.Models.throwNotFoundIfNull;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptRepository repository;
    private final Mapper<ReceiptDto, Receipt> mapper;

    @Autowired
    public ReceiptService(ReceiptRepository repository, Mapper<ReceiptDto, Receipt> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ReceiptDto> getAll() {
        return mapper.mapToDto(repository.findAll());
    }

    public Receipt get(long id) throws NotFoundException {
        final Receipt receipt = repository.findOne(id);
        return throwNotFoundIfNull(receipt);
    }

    public ReceiptDto getMapped(long id) throws NotFoundException {
        return mapper.mapToDto(get(id));
    }

    public ReceiptDto create(Order order, BigDecimal paymentAmount) throws InvalidPaymentException, NotFoundException {
        throwNotFoundIfNull(order);
        if (paymentAmount == null) throw new InvalidPaymentException();

        final BigDecimal change = calculateChange(paymentAmount, order.getTotal());

        Receipt receipt = new Receipt();
        receipt.setOrder(order);
        receipt.setPayment(paymentAmount);
        receipt.setChange(change);

        receipt = repository.save(receipt);
        return mapper.mapToDto(receipt);
    }

    private BigDecimal calculateChange(BigDecimal paymentAmount, BigDecimal orderTotal) {
        final BigDecimal change = paymentAmount.subtract(orderTotal);
        return change.signum() != -1 ? change : BigDecimal.ZERO;
    }
}
