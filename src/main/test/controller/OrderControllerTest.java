package controller;

import answer.king.controller.OrderController;
import answer.king.dto.OrderDto;
import answer.king.service.OrderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
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
        final List<OrderDto> orders = Arrays.asList(new OrderDto(), new OrderDto());
        when(mockService.getAll()).thenReturn(orders);

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertEquals(orders, response.getBody());
        assertTrue(response.getBody().size() == orders.size());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllNotFoundWhenNull() {
        when(mockService.getAll()).thenReturn(null);

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        assertNull(response.getBody());
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllEmptyReturnsSuccess() {
        final List<OrderDto> orders = new ArrayList<>();
        when(mockService.getAll()).thenReturn(orders);

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertEquals(orders, response.getBody());
        assertTrue(response.getBody().size() == 0);
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddItemSuccessfully() {
        when(mockService.addItem(0L, 0L)).thenReturn(new OrderDto());

        final ResponseEntity<OrderDto> response = orderController.addItem(0L, 0L);

        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        assertEquals(URI.create("/item/0"), response.getHeaders().getLocation());
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddNullReturnsBadRequest() {
        when(mockService.addItem(anyLong(), anyLong())).thenReturn(null);

        final ResponseEntity<OrderDto> response = orderController.addItem(0L, 0L);

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testCreate() {
        final OrderDto order = new OrderDto();
        when(mockService.save(order)).thenReturn(order);

        final ResponseEntity<OrderDto> response = orderController.create();

        assertEquals(order, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).save(order);
        verifyNoMoreInteractions(mockService);
    }
//
//    @Test
//    public void testPayNullPaymentFails() {
//        ResponseEntity<Receipt> response = orderController.pay(1L, null);
//
//        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
//        verifyZeroInteractions(mockService);
//    }
//
//    @Test
//    public void testPayNullReceiptFails() {
//        when(mockService.pay(anyLong(), any())).thenReturn(null);
//
//        ResponseEntity<Receipt> response = orderController.pay(1L, BigDecimal.ONE);
//
//        assertEquals(response.getBody(), null);
//        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
//        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
//        verifyNoMoreInteractions(mockService);
//    }
//
//    @Test
//    public void testPaySuccess() {
//        when(mockService.pay(anyLong(), any())).thenReturn(new Receipt());
//
//        ResponseEntity<Receipt> response = orderController.pay(1L, BigDecimal.ONE);
//
//        assertEquals(response.getBody(), new Receipt());
//        assertTrue(response.getStatusCode() == HttpStatus.OK);
//        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
//        verifyNoMoreInteractions(mockService);
//    }

    @After
    public void tearDown() {

    }
}
