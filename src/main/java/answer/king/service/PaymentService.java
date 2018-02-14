package answer.king.service;

import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentService {

    private final ReceiptService receiptService;

    public PaymentService(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    public ReceiptDto pay(BigDecimal paymentAmount, Order order) throws NotFoundException, InvalidPaymentException {
        if (order == null) throw new NotFoundException();

        // validate payments
        if (paymentAmount == null || paymentAmount.compareTo(order.getTotal()) < 0) {
            throw new InvalidPaymentException();
        }

        order.setPaid(true);

        final BigDecimal change = calculateChange(paymentAmount, order.getTotal());
        order.setChange(change);

        return receiptService.create(order, paymentAmount);
    }

    private BigDecimal calculateChange(BigDecimal paymentAmount, BigDecimal orderTotal) {
        final BigDecimal change = paymentAmount.subtract(orderTotal);
        return change.signum() != -1 ? change : BigDecimal.ZERO;
    }
}
