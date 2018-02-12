package service;

import answer.king.model.Item;
import answer.king.model.Order;
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
    public void testRelationshipsCorrectlySet() {
        // TODO: 12/02/2018 Fix NPE with getItems(), solution maybe dtos 
        
        final Item item = new Item("Item 1", BigDecimal.ONE, null);
        final Order order = new Order();

        when(mockItemRepository.findOne(anyLong())).thenReturn(item);
        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);

        final Order actualOrder = orderService.addItem(1L, 1L);

        assertTrue(actualOrder.getItems().size() == 1);
        assertEquals(item, actualOrder.getItems().get(0));
        assertEquals(order, item.getOrder());
        verify(mockItemRepository.findOne(anyLong()), times(1));
        verify(mockOrderRepository.findOne(anyLong()), times(1));
        verifyNoMoreInteractions(mockItemRepository);
        verifyNoMoreInteractions(mockItemRepository);
    }

    @After
    public void tearDown() {

    }
}