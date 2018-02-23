package service;

import answer.king.dto.ItemDto;
import answer.king.dto.LineItemDto;
import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Item;
import answer.king.entity.LineItem;
import answer.king.entity.Order;
import answer.king.error.InvalidCriteriaException;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.error.OrderAlreadyPaidException;
import answer.king.repo.OrderRepository;
import answer.king.service.ItemService;
import answer.king.service.OrderService;
import answer.king.service.PaymentService;
import answer.king.service.mapper.OrderMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private ItemService mockItemService;
    @Mock
    private OrderMapper mockOrderMapper;
    @Mock
    private PaymentService mockPaymentService;

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService(mockOrderRepository, mockItemService, mockOrderMapper, mockPaymentService);
    }

    @Test
    public void getAllOrdersWhenNoneAvailableReturnsAnEmptyList() {
        when(mockOrderRepository.findAll()).thenReturn(new ArrayList<>());

        final List<OrderDto> actualOrders = orderService.getAll();

        assertEquals(new ArrayList<OrderDto>(), actualOrders);
        verify(mockOrderRepository, times(1)).findAll();
        verify(mockOrderMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockOrderMapper);
    }

    @Test
    public void getAllOrdersWhenAvailableReturnsSuccessfully() {
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
    public void retrievalOfInvalidItemIdsWhenAddingToOrdersThrowsException() throws NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order());
        when(mockItemService.get(anyLong())).thenThrow(NotFoundException.class);

        orderService.addItem(1L, 1L, 1);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockItemService, times(1)).get(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemService);
    }

    @Test(expected = NotFoundException.class)
    public void unavailableOrderWhenAddingItemToItThrowsException() throws NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.addItem(1L, 1L, 1);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
    }

    @Test(expected = OrderAlreadyPaidException.class)
    public void whenAddingItemToAnAlreadyPaidOrderThrowException() throws NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(true, BigDecimal.TEN, null));

        orderService.addItem(0, 0, 1);
    }

    @Test
    public void addingItemToOrderSuccessfully() throws NotFoundException, OrderAlreadyPaidException {
        final Order order = new Order(false, new BigDecimal("100.00"), null);
        final Item item = new Item("Test Item", new BigDecimal("100.00"));
        item.setId(0L);

        final LineItem lineItem = new LineItem(new BigDecimal("100.00"), 1, order, item);
        order.getItems().put(item.getId(), lineItem);

        final OrderDto orderDto = new OrderDto(false, new BigDecimal("100.00"), null);
        final ItemDto itemDto = new ItemDto("Test Item", new BigDecimal("100.00"));
        final LineItemDto lineItemDto = new LineItemDto(new BigDecimal("100.00"), 1, orderDto, itemDto);
        itemDto.setId(0L);
        orderDto.getItems().put(item.getId(), lineItemDto);

        when(mockItemService.get(anyLong())).thenReturn(item);
        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(orderDto);

        final OrderDto actualOrder = orderService.addItem(0, 0, 1);

        assertFalse(actualOrder.getItems().isEmpty());
        assertEquals(lineItemDto, actualOrder.getItems().get(0L));
        verifyAddItem();
    }

    @Test
    public void addingItemWhenQuantityLessThanOneIsSpecfiedDefaultsToOne() throws NotFoundException, OrderAlreadyPaidException {
        final Order order = new Order(false, new BigDecimal("100.00"), null);
        final Item item = new Item("Test Item", new BigDecimal("100.00"));
        item.setId(0L);
        final LineItem lineItem = new LineItem(new BigDecimal("100.00"), 1, order, item);
        order.getItems().put(item.getId(), lineItem);

        final OrderDto orderDto = new OrderDto(false, new BigDecimal("100.00"), null);
        final ItemDto itemDto = new ItemDto("Test Item", new BigDecimal("100.00"));
        itemDto.setId(0L);
        final LineItemDto lineItemDto = new LineItemDto(new BigDecimal("100.00"), 1, orderDto, itemDto);
        orderDto.getItems().put(item.getId(), lineItemDto);

        when(mockItemService.get(anyLong())).thenReturn(item);
        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(orderDto);

        final OrderDto actualOrder = orderService.addItem(0, 0, 0);

        assertFalse(actualOrder.getItems().isEmpty());
        assertEquals(lineItemDto, actualOrder.getItems().get(0L));
        verifyAddItem();
    }

    @Test(expected = NotFoundException.class)
    public void addingItemToAnInvalidOrderThrowsException() throws NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(0L)).thenReturn(null);

        orderService.addItem(0L, 0L, 1);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockItemService);
    }

    @Test
    public void validOrderSavedSuccessfully() {
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
    public void gettingMappedOrderWithInvalidOrderIDThrowsException() throws NotFoundException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.getMapped(0L);
    }

    @Test
    public void gettingMappedOrderWithValidIdReturnsValidOrder() throws NotFoundException {
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
    public void payingForOrderWhenTheOrderIdInvalidThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.pay(0, BigDecimal.TEN);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(mockOrderRepository);
        verifyZeroInteractions(mockPaymentService);
    }

    @Test
    public void payingForOrderWithValidInformationReturnsReceipt() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        final ReceiptDto receiptDto = new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonMap(0L, new LineItemDto())));

        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN, Collections.singletonMap(0L, new LineItem())));
        when(mockPaymentService.pay(any(BigDecimal.class), any(Order.class)))
                .thenReturn(new ReceiptDto(BigDecimal.TEN, new OrderDto(true, BigDecimal.TEN, Collections.singletonMap(0L, new LineItemDto()))));


        final ReceiptDto actualReceipt = orderService.pay(0, BigDecimal.TEN);

        assertEquals(receiptDto, actualReceipt);
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockPaymentService, times(1)).pay(any(BigDecimal.class), any(Order.class));
        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockPaymentService);
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingForOrderWithNullPaymentAmountThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        orderService.pay(0, null);

        verifyZeroInteractions(mockOrderRepository);
        verifyZeroInteractions(mockPaymentService);
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingForOrderWithNegativePaymentAmountThrowsException() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        orderService.pay(0, new BigDecimal("-1.00"));

        verifyZeroInteractions(mockOrderRepository);
        verifyZeroInteractions(mockPaymentService);
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingForOrderWithNoItemsShouldResultInNoPaymentOccurring() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.ZERO, new HashMap<>()));

        orderService.pay(0, BigDecimal.TEN);
    }

    @Test(expected = InvalidPaymentException.class)
    public void payingForOrderWithNullItemsCollectionShouldResultInNoPaymentOccurring() throws NotFoundException, InvalidPaymentException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.ZERO, null));

        orderService.pay(0, BigDecimal.TEN);
    }

    @Test(expected = NotFoundException.class)
    public void whenRemovingItemFromInvalidOrderThrowException() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(null);

        orderService.removeItemFromOrder(0, 0, 0);
    }

    @Test(expected = OrderAlreadyPaidException.class)
    public void whenRemovingItemFromAnAlreadyPaidOrderThrowException() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(true, BigDecimal.TEN, null));

        orderService.removeItemFromOrder(0, 0, 0);
    }

    @Test(expected = InvalidCriteriaException.class)
    public void whenRemovingItemWithInvalidItemIdInvalidThrowException() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        final Order order = mock(Order.class);

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
        when(order.getItems()).thenReturn(Collections.emptyMap());

        orderService.removeItemFromOrder(0, 0, 0);
    }

    @Test(expected = InvalidCriteriaException.class)
    public void whenRemovingItemWithQuantityEqualToZeroThrowException() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN,
                Collections.singletonMap(0L, new LineItem())));

        orderService.removeItemFromOrder(0, 0, 0);
    }

    @Test(expected = InvalidCriteriaException.class)
    public void whenRemovingItemWithQuantityLessThanZeroThrowException() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        when(mockOrderRepository.findOne(anyLong())).thenReturn(new Order(false, BigDecimal.TEN,
                Collections.singletonMap(0L, new LineItem())));

        orderService.removeItemFromOrder(0, 0, -1);
    }

    @Test
    public void whenRemovingItemFromOrderWithQuantityGreaterThanActualQuantityAllItemsAreRemoved() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        final Map<Long, LineItem> map = new HashMap<>();
        map.put(0L, new LineItem(new BigDecimal("15.00"), 2));

        final Order order = new Order(false, new BigDecimal("30.00"), map);

        final ArgumentCaptor<Order> orderSave = ArgumentCaptor.forClass(Order.class);

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(null);

        orderService.removeItemFromOrder(0, 0, 6);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository).save(orderSave.capture());
        final Order captured = orderSave.getValue();

        verify(mockOrderMapper, times(1)).mapToDto(captured);

        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemService);
        verifyNoMoreInteractions(mockOrderMapper);

        assertEquals(0, captured.getItems().size());
        assertEquals(new BigDecimal("0.00"), order.getTotal());
    }

    @Test
    public void whenRemovingItemFromOrderWithQuantityLessThanLineItemQuantityItemsAreRemoved() throws InvalidCriteriaException, NotFoundException, OrderAlreadyPaidException {
        final Map<Long, LineItem> map = new HashMap<>();
        map.put(0L, new LineItem(new BigDecimal("15.00"), 5));

        final Order order = new Order(false, new BigDecimal("75.00"), map);

        final ArgumentCaptor<Order> orderSave = ArgumentCaptor.forClass(Order.class);

        when(mockOrderRepository.findOne(anyLong())).thenReturn(order);
        when(mockOrderRepository.save(any(Order.class))).thenReturn(order);
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(null);

        orderService.removeItemFromOrder(0, 0, 3);

        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository).save(orderSave.capture());
        final Order captured = orderSave.getValue();

        verify(mockOrderMapper, times(1)).mapToDto(captured);

        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemService);
        verifyNoMoreInteractions(mockOrderMapper);

        assertEquals(2, captured.getItems().get(0L).getQuantity());
        assertEquals(new BigDecimal("30.00"), order.getTotal());
        assertEquals(1, captured.getItems().size());
    }

    private void verifyAddItem() throws NotFoundException {
        verify(mockItemService, times(1)).get(anyLong());
        verify(mockOrderRepository, times(1)).findOne(anyLong());
        verify(mockOrderRepository, times(1)).save(any(Order.class));
        verify(mockOrderMapper, times(1)).mapToDto(any(Order.class));

        verifyNoMoreInteractions(mockOrderRepository);
        verifyNoMoreInteractions(mockItemService);
        verifyNoMoreInteractions(mockOrderMapper);
    }
}