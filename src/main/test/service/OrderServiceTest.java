package service;

import answer.king.dto.ItemDto;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.Order;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.service.OrderService;
import answer.king.service.ReceiptService;
import answer.king.service.mapper.OrderMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private OrderMapper mockOrderMapper;
    @Mock
    private ReceiptService mockReceiptService;

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService(mockOrderRepository, mockItemRepository,
                mockOrderMapper, mockReceiptService);
    }

    @Test
    public void testGetAllEmptyList() {
        when(mockOrderRepository.findAll()).thenReturn(new ArrayList<>());

        final List<OrderDto> actualOrders = orderService.getAll();

        assertEquals(new ArrayList<OrderDto>(), actualOrders);
        verify(mockOrderRepository, times(1)).findAll();
        verify(mockOrderMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

    @Test
    public void testGetAllWithItemsInList() {
        final List<OrderDto> expectedOrders = Arrays.asList(new OrderDto(), new OrderDto());
        when(mockOrderRepository.findAll()).thenReturn(Arrays.asList(new Order(), new Order()));
        when(mockOrderMapper.mapToDto(anyList())).thenReturn(Arrays.asList(new OrderDto(), new OrderDto()));

        final List<OrderDto> actualOrders = orderService.getAll();

        assertEquals(expectedOrders, actualOrders);
        verify(mockOrderRepository, times(1)).findAll();
        verify(mockOrderMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

    @Test(expected = NotFoundException.class)
    public void testAddItemNullItemRetrievedFails() throws NotFoundException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order());
        when(mockItemRepository.findOne(anyLong())).thenReturn(null);

        orderService.addItem(1L, 1L);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockItemRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testAddItemNullOrderRetrievedFails() throws NotFoundException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.addItem(1L, 1L);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
    }

    @Test
    public void testAddItem() throws NotFoundException {
        final OrderDto expectedOrder = new OrderDto(false, BigDecimal.ONE, null);
        final ItemDto expectedItemDto = new ItemDto("Item 1", BigDecimal.ONE, expectedOrder);
        expectedOrder.getItems().add(expectedItemDto);

        when(mockItemRepository.findOne(anyLong())).thenReturn(new Item("Item 1", BigDecimal.ONE));
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.ZERO, null));

        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order(false, BigDecimal.ONE,
                Collections.singletonList(new Item("Item 1", BigDecimal.ONE))));

        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(new OrderDto(false, BigDecimal.ONE,
                Collections.singletonList(new ItemDto("Item 1", BigDecimal.ONE, null))));

        final OrderDto actualOrder = orderService.addItem(0L, 0L);

        assertEquals(expectedOrder, actualOrder);
        verify(mockItemRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository, times(1)).save(any(Order.class));
        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));

        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

    @Test(expected = NotFoundException.class)
    public void testAddItemWithInvalidOrder() throws NotFoundException {
        final Item item = new Item("Item 1", BigDecimal.TEN, null);

        when(mockOrderRepository.findOne(0L)).thenReturn(null);

        orderService.addItem(0L, 0L);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);
    }

    @Test
    public void testSave() {
        final OrderDto expectedOrder = new OrderDto(false, BigDecimal.ZERO, null);

        when(mockOrderMapper.mapToEntity(any(OrderDto.class))).thenReturn(new Order(false, BigDecimal.ZERO, null));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order(false, BigDecimal.ZERO, null));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(new OrderDto(false, BigDecimal.ZERO, null));

        final OrderDto actualOrder = orderService.save(new OrderDto(false, BigDecimal.ZERO, null));

        assertEquals(expectedOrder, actualOrder);
        verify(mockOrderRepository, times(1)).save(any(Order.class));
        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));
        verify(mockOrderMapper, times(1)).mapToEntity(any(OrderDto.class));
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testGetMappedThrowsExceptionInvalid() throws NotFoundException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.getMapped(0L);
    }

    @Test
    public void testGetMappedMapsCorrectly() throws NotFoundException {
        final OrderDto expectedOrder = new OrderDto(false, BigDecimal.TEN, null);

        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN, null));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(new OrderDto(false, BigDecimal.TEN, null));

        final OrderDto actualOrder = orderService.getMapped(0L);

        assertEquals(expectedOrder, actualOrder);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

    @Test(expected = NotFoundException.class)
    public void testPaymentInvalidOrderId() throws NotFoundException, InvalidPaymentException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.pay(0, BigDecimal.TEN);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockReceiptService);
    }

//    @Test
//    public void testPaymentSuccess() throws NotFoundException, OrderService.InvalidPaymentException {
//        final ReceiptDto expectedReceipt = new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonList(
//                new ItemDto("Item 1", BigDecimal.TEN))));
//
//        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN,
//                Collections.singletonList(new Item("Item 1", BigDecimal.TEN))));
//        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(
//                new OrderDto(false, BigDecimal.TEN, Collections.singletonList(
//                        new ItemDto("Item 1", BigDecimal.TEN))));
//
//        when(mockReceiptService.create(any(Order.class), any(BigDecimal.class))).thenReturn(new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonList(
//                new ItemDto("Item 1", BigDecimal.TEN)))));
//
//        final ReceiptDto actualReceipt = orderService.pay(0L, BigDecimal.TEN);
//
//        assertEquals(expectedReceipt, actualReceipt);
//        verify(mockOrderRepository, times(1)).findOne(anyLong());
//        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));
//        verify(mockReceiptService, times(1)).create(any(Order.class), any(BigDecimal.class));
//
//        verifyNoMoreInteractions(mockOrderRepository);
//        verifyNoMoreInteractions(mockOrderMapper);
//    }


    @Test
    public void testPaySuccess() throws NotFoundException, InvalidPaymentException {
        final ReceiptDto expectedReceipt = new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, null));

        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN, null));
        when(mockReceiptService.create(any(Order.class), any(BigDecimal.class))).thenReturn(new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, null)));

        final ReceiptDto actualReceipt = orderService.pay(0, BigDecimal.TEN);

        assertEquals(expectedReceipt, actualReceipt);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockReceiptService, times(1)).create(any(Order.class), any(BigDecimal.class));
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockReceiptService);
    }

    @Test(expected = InvalidPaymentException.class)
    public void testPaymentNullAmount() throws NotFoundException, InvalidPaymentException {
        orderService.pay(0, null);

        verifyZeroInteractions(mockOrderRepository);
        verifyZeroInteractions(mockReceiptService);
    }

    @Test(expected = InvalidPaymentException.class)
    public void testPaymentNegativeAmount() throws NotFoundException, InvalidPaymentException {
        orderService.pay(0, new BigDecimal("-1.00"));

        verifyZeroInteractions(mockOrderRepository);
        verifyZeroInteractions(mockReceiptService);
    }

    @After
    public void tearDown() {

    }
}