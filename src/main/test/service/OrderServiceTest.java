package service;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.service.OrderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private ItemRepository mockItemRepository;
    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService(mockOrderRepository, mockItemRepository);
    }

    @Test
    public void testGetAllEmptyList() {
        when(mockOrderRepository.findAll()).thenReturn(new ArrayList<>());

        final List<Order> actualOrders = orderService.getAll();

        assertEquals(new ArrayList<Order>(), actualOrders);
        assertTrue(actualOrders.size() == 0);
        verify(mockOrderRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockOrderRepository);

        verifyZeroInteractions(mockItemRepository);
    }

    @Test
    public void testGetAllWithItemsInList() {
        final List<Order> orders = Arrays.asList(new Order(), new Order());
        when(mockOrderRepository.findAll()).thenReturn(orders);

        final List<Order> actualOrders = orderService.getAll();

        assertEquals(orders, actualOrders);
        assertTrue(actualOrders.size() == orders.size());
        verify(mockOrderRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockOrderRepository);

        verifyZeroInteractions(mockItemRepository);
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
    public void testAddItemRelationshipsCorrectlySet() {
        final Item item = new Item("Item 1", BigDecimal.ONE, null);
        final Order order = new Order();

        when(mockItemRepository.findOne(0L)).thenReturn(item);
        when(mockOrderRepository.findOne(0L)).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);

        final Order actualOrder = orderService.addItem(0L, 0L);

        assertTrue(actualOrder.getItems().size() == 1);
        assertEquals(item, actualOrder.getItems().get(0));
        assertEquals(order, item.getOrder());
        verify(mockItemRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockItemRepository);
        verifyNoMoreInteractions(mockItemRepository);
    }

    @Test
    public void testAddItemRunningTotalUpdated() {
        final Item item = new Item("Item 1", BigDecimal.TEN, null);
        final Order order = new Order();
        final BigDecimal expectedTotal = BigDecimal.TEN;

        when(mockItemRepository.findOne(0L)).thenReturn(item);
        when(mockOrderRepository.findOne(0L)).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);

        final Order actualOrder = orderService.addItem(0L, 0L);

        assertEquals(expectedTotal, actualOrder.getTotal());
    }

    @Test
    public void testAddItemWithInvalidOrder() {
        final Item item = new Item("Item 1", BigDecimal.TEN, null);

        when(mockItemRepository.findOne(0L)).thenReturn(item);
        when(mockOrderRepository.findOne(0L)).thenReturn(null);

        final Order actualOrder = orderService.addItem(0L, 0L);
        assertNull(actualOrder);
    }

    @Test
    public void testAddItemRunningTotalNullItemPrice() {
        final Item item = new Item("Item 1", null, new Order());

        when(mockItemRepository.findOne(0L)).thenReturn(item);
        when(mockOrderRepository.findOne(0L)).thenReturn(new Order());

        final Order actualOrder = orderService.addItem(0L, 0L);
        assertNull(actualOrder);
    }

    @Test
    public void testSave() {
        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order());

        final Order actualOrder = orderService.save(new Order());

        assertEquals(new Order(), actualOrder);
        verify(mockOrderRepository, times(1)).save(any(Order.class));
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);
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
        final Order order = new Order();
        order.setTotal(BigDecimal.TEN);

        final BigDecimal paymentAmount = BigDecimal.TEN;

        final Receipt expectedReceipt = new Receipt();
        expectedReceipt.setOrder(order);
        expectedReceipt.setPayment(paymentAmount);

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);

        final Receipt actualReceipt = orderService.pay(0L, paymentAmount);

        assertEquals(expectedReceipt, actualReceipt);
        assertTrue(order.getPaid());
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);
    }

    @Test
    public void testPaymentInvalidAmount() {
        final Order order = new Order();
        final BigDecimal paymentAmount = BigDecimal.ONE;

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);

        final Receipt actualReceipt = orderService.pay(0L, paymentAmount);

        assertNull(actualReceipt);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);
    }

    @Test
    public void testPaymentNullAmount() {
        final Order order = new Order();
        final BigDecimal payment = null;

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);

        final Receipt actualReceipt = orderService.pay(0L, payment);
        assertNull(actualReceipt);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemRepository);

    }

    @Test
    public void testPayNullPayment() {
        assertNull(orderService.pay(0L, null));
    }

    @After
    public void tearDown() {

    }
}