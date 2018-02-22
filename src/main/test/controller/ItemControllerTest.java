package controller;

import answer.king.controller.ItemController;
import answer.king.dto.CategoryDto;
import answer.king.dto.CreatableItemDto;
import answer.king.dto.ItemDto;
import answer.king.error.InvalidPriceException;
import answer.king.error.NotFoundException;
import answer.king.service.ItemService;
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
import static util.ValidatorUtil.validateWithNoViolations;
import static util.ValidatorUtil.validateWithViolation;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @Mock
    private ItemService mockService;
    private ItemController itemController;

    @Before
    public void setUp() {
        itemController = new ItemController(mockService);
    }

    @Test
    public void getItemReturnsItemWithOkResponse() throws NotFoundException {
        final ItemDto expectedItem = new ItemDto("Test 1", BigDecimal.ZERO);
        when(mockService.getMapped(anyLong())).thenReturn(expectedItem);

        final ResponseEntity<ItemDto> response = itemController.get(0L);

        assertEquals(expectedItem, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getMapped(anyLong());
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getAllItemsReturnsListOfItemsSuccessfully() {
        final List<ItemDto> items = Arrays.asList(new ItemDto(), new ItemDto());
        when(mockService.getAll()).thenReturn(items);

        final ResponseEntity<List<ItemDto>> response = itemController.getAll();

        assertEquals(items, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void getAllItemsReturnsEmptyList() {
        final List<ItemDto> items = new ArrayList<>();
        when(mockService.getAll()).thenReturn(items);

        final ResponseEntity<List<ItemDto>> response = itemController.getAll();

        assertEquals(items, response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void itemCreateSucceedsWithValidItemData() throws NotFoundException {
        final CreatableItemDto creatableItemDto = new CreatableItemDto();
        creatableItemDto.setName("Test 1");
        creatableItemDto.setDescription("Description");
        creatableItemDto.setPrice(BigDecimal.ONE);
        creatableItemDto.setCategoryId(0);

        final ItemDto item = new ItemDto();
        item.setName("Test 1");
        item.setDescription("Description");
        item.setPrice(BigDecimal.ONE);
        item.setCategory(new CategoryDto("Fruit & Veg"));

        when(mockService.create(any(CreatableItemDto.class))).thenReturn(item);

        final ResponseEntity<ItemDto> response = itemController.create(creatableItemDto);

        validateWithNoViolations(creatableItemDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getHeaders().getLocation(), URI.create("/item/0"));
        verify(mockService, times(1)).create(any(CreatableItemDto.class));
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void itemCreateReturningNullReturnsBadRequestResponse() throws NotFoundException {
        when(mockService.create(any(CreatableItemDto.class))).thenReturn(null);

        final ResponseEntity<ItemDto> response = itemController.create(new CreatableItemDto());

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).create(any(CreatableItemDto.class));
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void nameInvalidWhenNull() {
        final ItemDto item = new ItemDto(null, BigDecimal.ONE);
        validateWithViolation(item);
    }

    @Test
    public void nameInvalidWhenLengthIsEqualToZero() {
        final ItemDto item = new ItemDto("", BigDecimal.ONE);
        validateWithViolation(item);
    }

    @Test
    public void priceInvalidWhenNull() {
        final ItemDto item = new ItemDto("Item 1", null);
        validateWithViolation(item);
    }

    @Test
    public void priceValidWhenIntegerDigitsLessThanLimit() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("1"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceValidWhenIntegerDigitsMatchesLimit() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("1234567890"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceInvalidWhenIntegerDigitsExceedsLimit() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("12345678901"));
        validateWithViolation(item);
    }

    @Test
    public void priceValidWithZeroDecimals() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceValidWithOneDecimal() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.0"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceValidWithTwo() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.00"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceInvalidWithMoreThanTwoDecimals() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.000"));
        validateWithViolation(item);
    }

    @Test
    public void priceIsInvalidWithNegativeValue() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("-10.00"));
        validateWithViolation(item);
    }

    @Test
    public void priceValidAtOnePence() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("0.01"));
        validateWithNoViolations(item);
    }

    @Test
    public void priceValidAtZero() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("0.01"));
        validateWithNoViolations(item);
    }

    @Test
    public void updatePriceOfItemReturnsSuccessfulOkResponse() throws NotFoundException, InvalidPriceException {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("32.85"));

        when(mockService.updatePrice(anyLong(), any())).thenReturn(item);

        final ResponseEntity<ItemDto> response = itemController.updatePrice(0L, new BigDecimal("32.85"));

        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).updatePrice(anyLong(), any());
        verifyNoMoreInteractions(mockService);
    }
}
