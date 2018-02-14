package controller;

import answer.king.controller.OrderController;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
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
    public void testGetValidId() {
        final OrderDto expectedDto = new OrderDto();
        when(mockService.get(anyLong())).thenReturn(new OrderDto());

        final ResponseEntity<OrderDto> response = orderController.get(0L);

        assertEquals(expectedDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).get(anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetInvalidId() {
        when(mockService.get(anyLong())).thenReturn(null);

        final ResponseEntity<OrderDto> response = orderController.get(-1L);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mockService, times(1)).get(anyLong());
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
    public void testGetAllNotFoundWhenNull() {
        when(mockService.getAll()).thenReturn(null);

        final ResponseEntity<List<OrderDto>> response = orderController.getAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
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
    public void testAddItemSuccessfully() {
        when(mockService.addItem(0L, 0L)).thenReturn(new OrderDto());

        final ResponseEntity<OrderDto> response = orderController.addItem(0L, 0L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/item/0"), response.getHeaders().getLocation());
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testAddNullReturnsBadRequest() {
        when(mockService.addItem(anyLong(), anyLong())).thenReturn(null);

        final ResponseEntity<OrderDto> response = orderController.addItem(0L, 0L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockService, times(1)).addItem(anyLong(), anyLong());
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
    public void testPayNullPaymentFails() {
        final ResponseEntity<ReceiptDto> response = orderController.pay(1L, null);

        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyZeroInteractions(mockService);
    }

    @Test
    public void testPayNullReceiptFails() {
        when(mockService.pay(anyLong(), any())).thenReturn(null);

        final ResponseEntity<ReceiptDto> response = orderController.pay(1L, BigDecimal.ONE);

        assertEquals(response.getBody(), null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testPaySuccess() {
        when(mockService.pay(anyLong(), any())).thenReturn(new ReceiptDto());

        final ResponseEntity<ReceiptDto> response = orderController.pay(1L, BigDecimal.ONE);

        assertEquals(new ReceiptDto(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockService, times(1)).pay(1L, BigDecimal.ONE);
        verifyNoMoreInteractions(mockService);
    }

    @After
    public void tearDown() {

    }
}
