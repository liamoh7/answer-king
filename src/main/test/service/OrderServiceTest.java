package service;

import answer.king.dto.ItemDto;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.Order;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.service.OrderService;
import answer.king.service.ReceiptService;
import answer.king.service.mapper.ItemMapper;
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
import static org.junit.Assert.assertNull;
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
    private ItemMapper mockItemMapper;
    @Mock
    private ReceiptService mockReceiptService;

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService(mockOrderRepository, mockItemRepository,
                mockOrderMapper, mockItemMapper, mockReceiptService);
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
        verifyZeroInteractions(mockItemRepository);
        verifyZeroInteractions(mockItemMapper);
        verifyZeroInteractions(mockReceiptService);
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
        verifyZeroInteractions(mockItemRepository);
        verifyZeroInteractions(mockItemMapper);
        verifyZeroInteractions(mockReceiptService);
    }

    @Test
    public void testAddItemNullItemRetrievedFails() {
        when(mockItemRepository.findOne(anyLong())).thenReturn(null);

        assertNull(orderService.addItem(1L, 1L));
        verify(mockItemRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockItemRepository);
    }

    @Test
    public void testAddItemNullOrderRetrievedFails() {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        assertNull(orderService.addItem(1L, 1L));
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
    }

    @Test
    public void testAddItem() {
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

    @Test
    public void testAddItemWithInvalidOrder() {
        final Item item = new Item("Item 1", BigDecimal.TEN, null);

        when(mockItemRepository.findOne(0L)).thenReturn(item);
        when(mockOrderRepository.findOne(0L)).thenReturn(null);

        final OrderDto actualOrder = orderService.addItem(0L, 0L);
        assertNull(actualOrder);
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

    @Test
    public void testPaymentInvalidOrderId() {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        assertNull(orderService.pay(0L, BigDecimal.ONE));
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);
    }

    @Test
    public void testPaymentSuccess() {
        final ReceiptDto expectedReceipt = new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonList(
                new ItemDto("Item 1", BigDecimal.TEN))));

        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN,
                Collections.singletonList(new Item("Item 1", BigDecimal.TEN))));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(
                new OrderDto(false, BigDecimal.TEN, Collections.singletonList(
                        new ItemDto("Item 1", BigDecimal.TEN))));

        when(mockReceiptService.create(any(OrderDto.class), any(BigDecimal.class))).thenReturn(new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonList(
                new ItemDto("Item 1", BigDecimal.TEN)))));

        final ReceiptDto actualReceipt = orderService.pay(0L, BigDecimal.TEN);

        assertEquals(expectedReceipt, actualReceipt);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));
        verify(mockReceiptService, times(1)).create(any(OrderDto.class), any(BigDecimal.class));

        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

//    @Test
//    public void testPaymentInvalidAmount() {
//        final Order order = new Order();
//        final BigDecimal paymentAmount = BigDecimal.ONE;
//
//        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
//
//        final Receipt actualReceipt = orderService.pay(0L, paymentAmount);
//
//        assertNull(actualReceipt);
//        verify(mockOrderRepository, times(1)).findOne(anyLong());
//        verifyNoMoreInteractions(mockOrderRepository);
//        verifyZeroInteractions(mockItemRepository);
//    }

    @Test
    public void testPaymentNullAmount() {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order());

        final ReceiptDto actualReceipt = orderService.pay(0L, null);

        assertNull(actualReceipt);
        verifyZeroInteractions(mockOrderRepository);
    }

    @After
    public void tearDown() {

    }
}