package controller;

import answer.king.controller.OrderController;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
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
    public void testGetValidId() throws NotFoundException {
        final OrderDto expectedDto = new OrderDto();
        when(mockService.getMapped(anyLong())).thenReturn(new OrderDto());

        final ResponseEntity<OrderDto> response = orderController.get(0L);

        assertEquals(expectedDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).getMapped(anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllSuccess() {
        final List<OrderDto> expectedOrders = Arrays.asList(new OrderDto(), new OrderDto());
        when(mockService.getAll()).thenReturn(Arrays.asList(new OrderDto(), new OrderDto()));

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertEquals(expectedOrders, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllEmptyReturnsSuccess() {
        final List<OrderDto> expectedOrders = new ArrayList<>();
        when(mockService.getAll()).thenReturn(new ArrayList<>());

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertEquals(expectedOrders, response.getBody());
        assertTrue(response.getBody().size() == 0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddItemSuccessfully() throws NotFoundException {
        final ResponseEntity<OrderDto> response = orderController.addItem(0L, 0L, 0);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/item/0"), response.getHeaders().getLocation());
        verify(mockService, times(1)).addItem(anyLong(), anyLong(), anyInt());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testCreate() {
        final OrderDto expectedOrder = new OrderDto();
        when(mockService.save(expectedOrder)).thenReturn(new OrderDto());

        final ResponseEntity<OrderDto> response = orderController.create();

        assertEquals(expectedOrder, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).save(expectedOrder);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testPaySuccess() throws InvalidPaymentException, NotFoundException {
        when(mockService.pay(anyLong(), any())).thenReturn(new ReceiptDto());

        final ResponseEntity<ReceiptDto> response = orderController.pay(1L, BigDecimal.ONE);

        assertEquals(new ReceiptDto(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
        verifyNoMoreInteractions(mockService);
    }
}
