package controller;

import answer.king.controller.ItemController;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @Mock
    private ItemService mockService;
    private ItemController itemController;
    private Validator validator;

    @Before
    public void setUp() {
        itemController = new ItemController(mockService);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
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
    public void itemCreateSucceedsWithValidItemData() {
        final ItemDto item = new ItemDto("Test Item", BigDecimal.ONE);
        when(mockService.save(any(ItemDto.class))).thenReturn(item);

        final ResponseEntity<ItemDto> response = itemController.create(new ItemDto());

        validateWithNoViolations(item);
        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        assertEquals(response.getHeaders().getLocation(), URI.create("/item/0"));
        verify(mockService, times(1)).save(any(ItemDto.class));
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void itemCreateReturningNullReturnsBadRequestResponse() {
        when(mockService.save(any(ItemDto.class))).thenReturn(null);

        final ResponseEntity<ItemDto> response = itemController.create(new ItemDto());

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).save(any(ItemDto.class));
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

    private void validateWithViolation(ItemDto item) {
        validateWithViolations(item, 1);
    }

    private void validateWithNoViolations(ItemDto item) {
        final Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    private void validateWithViolations(ItemDto item, int violationCount) {
        final Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() == violationCount);
    }
}
