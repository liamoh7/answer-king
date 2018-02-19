package service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.error.OrderAlreadyPaidException;
import answer.king.service.PaymentService;
import answer.king.service.ReceiptService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock
    private ReceiptService mockReceiptService;
    private PaymentService paymentService;

    @Before
    public void setUp() {
        paymentService = new PaymentService(mockReceiptService);
    }

    @Test(expected = NotFoundException.class)
    public void payingWhenTheOrderIsNullThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        paymentService.pay(BigDecimal.TEN, null);
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingWhenAmountIsNullThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        paymentService.pay(null, new Order());
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingWhenAmountIsLessThanOrderTotalThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        paymentService.pay(BigDecimal.ONE, new Order(false, BigDecimal.TEN, null));
    }

    @Test(expected = OrderAlreadyPaidException.class)
    public void payingWhenOrderAlreadyPaidThrowsException() throws InvalidPaymentException, NotFoundException, OrderAlreadyPaidException {
        paymentService.pay(BigDecimal.TEN, new Order(true, BigDecimal.TEN, null));
    }

    @Test
    public void whenPayAmountIsEqualToOrderTotalPaymentIsAllowed() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        // just testing to ensure the payment provided is valid, nothing else
        final Order order = new Order(false, BigDecimal.TEN, null);
        final BigDecimal paymentAmount = new BigDecimal("100.00");

        when(mockReceiptService.create(any(Order.class), any(BigDecimal.class)))
                .thenReturn(new ReceiptDto(paymentAmount, new OrderDto(true, BigDecimal.TEN, null)));

        final ReceiptDto receipt = paymentService.pay(BigDecimal.TEN, order);

        assertTrue(receipt.getOrder().isPaid());
    }

    @Test
    public void whenPayAmountIsGreaterThanOrderTotalPaymentIsAllowed() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        final Order order = new Order(false, new BigDecimal("5.00"), null);
        final BigDecimal paymentAmount = new BigDecimal("5.01");
        final OrderDto expectedOrder = new OrderDto(true, new BigDecimal("5.00"), null);
        final ReceiptDto expectedReceipt = new ReceiptDto(paymentAmount, expectedOrder, new BigDecimal("0.01"));

        when(mockReceiptService.create(order, paymentAmount)).thenReturn(expectedReceipt);

        final ReceiptDto actualReceipt = paymentService.pay(paymentAmount, order);

        assertTrue(actualReceipt.getOrder().isPaid());
    }
}
