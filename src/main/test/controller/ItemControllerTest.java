package controller;

import answer.king.controller.ItemController;
import answer.king.model.Item;
import answer.king.model.Order;
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
    public void testGetAllSuccess() {
        final List<Item> items = Arrays.asList(new Item(), new Item());
        when(mockService.getAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getAll();

        assertEquals(items, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllNotFoundWhenNull() {
        when(mockService.getAll()).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getAll();

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllEmptyReturnsSuccess() {
        List<Item> items = new ArrayList<>();
        when(mockService.getAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getAll();

        assertEquals(items, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }


    @Test
    public void testCreateValidData() {
        Item testItem = new Item("Test Item", BigDecimal.ONE, new Order());
        when(mockService.save(testItem)).thenReturn(testItem);

        Item actualItem = itemController.create(testItem);

        assertEquals(testItem, actualItem);
//        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).save(testItem);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testInvalidNameWithNull() {
        Item item = new Item(null, BigDecimal.ONE, new Order());
        itemController.create(item);
        validateWithViolations(item);
    }

    @Test
    public void testInvalidNameWithInvalidLength() {
        Item item = new Item("", BigDecimal.ONE, new Order());
        itemController.create(item);
        validateWithViolations(item);
    }

    @Test
    public void testInvalidPriceWithNull() {
        Item item = new Item("Item 1", null, new Order());
        itemController.create(item);
        validateWithViolations(item);
    }

    @Test
    public void testInvalidPriceTooManyDigits() {
        Item item = new Item("Item 1", new BigDecimal("9999999999999999"), new Order());
        itemController.create(item);
        validateWithViolations(item);
    }

    @Test
    public void testInvalidPriceTooManyDecimals() {
        Item item = new Item("Item 1", new BigDecimal("10.000"), new Order());
        itemController.create(item);
        validateWithViolations(item, 1);
    }

    @Test
    public void testPriceWithNoDecimals() {
        Item item = new Item("Item 1", new BigDecimal("10"), new Order());
        itemController.create(item);
        validateWithNoViolations(item);
    }

    @Test
    public void testPriceWithDecimals() {
        Item item = new Item("Item 1", new BigDecimal("10.00"), null);
        itemController.create(item);
        validateWithNoViolations(item);
    }

    @Test
    public void testNullOrder() {
        Item item = new Item("Item 1", new BigDecimal("10.00"), new Order());
        itemController.create(item);
        validateWithNoViolations(item);
    }

    private void validateWithViolations(Item item) {
        validateWithViolations(item, 1);
    }

    private void validateWithNoViolations(Item item) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    private void validateWithViolations(Item item, int violationCount) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() == violationCount);
    }
}
