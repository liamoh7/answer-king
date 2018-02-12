package controller;

import answer.king.controller.OrderController;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @Mock
    private OrderService mockService;
    private OrderController orderController;

    @Before
    public void setUp() {
        orderController = new OrderController(mockService);
    }

    @Test
    public void testGetAllSuccess() {
        final List<Order> orders = Arrays.asList(new Order(), new Order());
        when(mockService.getAll()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAll();

        assertEquals(orders, response.getBody());
        assertTrue(response.getBody().size() == orders.size());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllNotFoundWhenNull() {
        when(mockService.getAll()).thenReturn(null);

        ResponseEntity<List<Order>> response = orderController.getAll();

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllEmptyReturnsSuccess() {
        List<Order> orders = new ArrayList<>();
        when(mockService.getAll()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAll();

        assertEquals(orders, response.getBody());
        assertTrue(response.getBody().size() == 0);
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddItemSuccessfully() {
        when(mockService.addItem(anyLong(), anyLong())).thenReturn(new Order());

        ResponseEntity<Order> response = orderController.addItem(anyLong(), anyLong());

        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddNullReturnsBadRequest() {
        when(mockService.addItem(anyLong(), anyLong())).thenReturn(null);

        ResponseEntity<Order> response = orderController.addItem(anyLong(), anyLong());

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testCreate() {
        final Order order = new Order();
        when(mockService.save(order)).thenReturn(order);

        ResponseEntity<Order> response = orderController.create();

        assertEquals(order, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).save(order);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testPayNullPaymentFails() {
        ResponseEntity<Receipt> response = orderController.pay(1L, null);

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verifyZeroInteractions(mockService);
    }

    @Test
    public void testPayNullReceiptFails() {
        when(mockService.pay(anyLong(), any())).thenReturn(null);

        ResponseEntity<Receipt> response = orderController.pay(1L, BigDecimal.ONE);

        assertEquals(response.getBody(), null);
        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testPaySuccess() {
        when(mockService.pay(anyLong(), any())).thenReturn(new Receipt());

        ResponseEntity<Receipt> response = orderController.pay(1L, BigDecimal.ONE);

        assertEquals(response.getBody(), new Receipt());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
        verifyNoMoreInteractions(mockService);
    }

    @After
    public void tearDown() {

    }
}
