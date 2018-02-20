package answer.king.service;

import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.error.OrderAlreadyPaidException;
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

    public ReceiptDto pay(BigDecimal paymentAmount, Order order) throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        if (order == null) throw new NotFoundException();

        // validate payments
        if (paymentAmount == null || paymentAmount.compareTo(order.getTotal()) < 0) {
            throw new InvalidPaymentException();
        }

        if (order.isPaid()) throw new OrderAlreadyPaidException();
        order.setPaid(true);

        ReceiptDto receiptDto = receiptService.create(order, paymentAmount);
        System.out.println("Receipt DTO: " + receiptDto.getOrder().isPaid());
        return receiptDto;
    }
}
