package service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.entity.Receipt;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.repo.ReceiptRepository;
import answer.king.service.ReceiptService;
import answer.king.service.mapper.OrderMapper;
import answer.king.service.mapper.ReceiptMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptServiceTest {

    @Mock
    private ReceiptRepository mockRepository;
    @Mock
    private ReceiptMapper mockReceiptMapper;
    @Mock
    private OrderMapper mockOrderMapper;
    private ReceiptService receiptService;

    @Before
    public void setUp() {
        receiptService = new ReceiptService(mockRepository, mockReceiptMapper, mockOrderMapper);
    }

    @Test(expected = NotFoundException.class)
    public void testCreateWithInvalidOrderThrowsException() throws NotFoundException, InvalidPaymentException {
        receiptService.create(null, BigDecimal.ONE);
    }

    @Test(expected = InvalidPaymentException.class)
    public void testCreateWithInvalidPaymentThrowsException() throws NotFoundException, InvalidPaymentException {
        receiptService.create(new Order(), null);
    }

    public void testCreateRepositorySuccess() throws NotFoundException, InvalidPaymentException {
        final ReceiptDto expectedReceipt = new ReceiptDto();

        when(mockOrderMapper.mapToEntity(any(OrderDto.class))).thenReturn(new Order());
        when(mockRepository.save(any(Receipt.class))).thenReturn(new Receipt());
        when(mockReceiptMapper.mapToDto(any(Receipt.class))).thenReturn(new ReceiptDto());

        final ReceiptDto actualReceipt = receiptService.create(new Order(), BigDecimal.TEN);

        assertEquals(expectedReceipt, actualReceipt);
        verify(mockOrderMapper, times(1)).mapToEntity(any(OrderDto.class));
        verify(mockRepository, times(1)).save(any(Receipt.class));
        verify(mockReceiptMapper, times(1)).mapToDto(any(Receipt.class));
        verifyNoMoreInteractions(mockOrderMapper);
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockReceiptMapper);
    }

    @Test
    public void testGetAllWithItems() {
        final List<Receipt> receipts = Arrays.asList(
                new Receipt(BigDecimal.ONE, new Order()),
                new Receipt(BigDecimal.TEN, new Order()));

        final List<ReceiptDto> expectedReceipts = Arrays.asList(
                new ReceiptDto(BigDecimal.ONE, new OrderDto()),
                new ReceiptDto(BigDecimal.TEN, new OrderDto()));

        when(mockRepository.findAll()).thenReturn(receipts);
        when(mockReceiptMapper.mapToDto(anyList())).thenReturn(expectedReceipts);

        final List<ReceiptDto> actualItems = receiptService.getAll();

        assertEquals(expectedReceipts, actualItems);
        verify(mockRepository, times(1)).findAll();
        verify(mockReceiptMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockReceiptMapper);
    }

    @Test
    public void testGetAllEmptyList() {
        final List<Receipt> entities = Collections.emptyList();
        final List<ReceiptDto> expectedDtos = Collections.emptyList();

        when(mockRepository.findAll()).thenReturn(entities);
        when(mockReceiptMapper.mapToDto(anyList())).thenReturn(expectedDtos);

        final List<ReceiptDto> actualItems = receiptService.getAll();

        assertEquals(expectedDtos, actualItems);
        verify(mockRepository, times(1)).findAll();
        verify(mockReceiptMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockReceiptMapper);
    }

    @Test
    public void testGet() throws NotFoundException {
        final Receipt receipt = new Receipt();
        final ReceiptDto expectedDto = new ReceiptDto();

        when(mockRepository.findOne(anyLong())).thenReturn(receipt);
        when(mockReceiptMapper.mapToDto(any(Receipt.class))).thenReturn(expectedDto);

        final ReceiptDto actualItem = receiptService.getMapped(0L);

        assertEquals(expectedDto, actualItem);
        verify(mockRepository, times(1)).findOne(0L);
        verify(mockReceiptMapper, times(1)).mapToDto(any(Receipt.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockReceiptMapper);
    }

    @Test(expected = NotFoundException.class)
    public void testGetThrowsNotFoundInvalidID() throws NotFoundException {
        when(mockRepository.findOne(anyLong())).thenReturn(null);
        receiptService.get(0L);
    }

    @Test
    public void testCalculateZeroChange() throws NotFoundException, InvalidPaymentException {
        final BigDecimal paymentAmount = new BigDecimal("20.00");
        final BigDecimal orderTotal = new BigDecimal("20.00");

        final Order order = new Order(true, orderTotal, null);
        final OrderDto orderDto = new OrderDto(true, orderTotal, null);

        final BigDecimal expectedChange = BigDecimal.ZERO;

        when(mockRepository.save(any(Receipt.class))).thenReturn(new Receipt(paymentAmount, order, expectedChange));
        when(mockReceiptMapper.mapToDto(any(Receipt.class))).thenReturn(new ReceiptDto(paymentAmount, orderDto, BigDecimal.ZERO));

        final ReceiptDto actualReceipt = receiptService.create(order, paymentAmount);

        assertEquals(expectedChange, actualReceipt.getChange());
    }

    @Test
    public void testCalculateChange() throws NotFoundException, InvalidPaymentException {
        final BigDecimal paymentAmount = new BigDecimal("20.00");
        final BigDecimal orderTotal = new BigDecimal("30.99");

        final Order order = new Order(true, orderTotal, null);
        final OrderDto orderDto = new OrderDto(true, orderTotal, null);

        final BigDecimal expectedChange = new BigDecimal("9.99");

        when(mockRepository.save(any(Receipt.class))).thenReturn(new Receipt(paymentAmount, order, expectedChange));
        when(mockReceiptMapper.mapToDto(any(Receipt.class))).thenReturn(new ReceiptDto(paymentAmount, orderDto, new BigDecimal("9.99")));

        final ReceiptDto actualReceipt = receiptService.create(order, paymentAmount);

        assertEquals(expectedChange, actualReceipt.getChange());
    }
}
