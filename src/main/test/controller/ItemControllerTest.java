package controller;

import answer.king.controller.ItemController;
import answer.king.dto.ItemDto;
import answer.king.dto.OrderDto;
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
    public void testGet() throws NotFoundException {
        final ItemDto expectedItem = new ItemDto("Test 1", BigDecimal.ZERO, new OrderDto());
        when(mockService.getMapped(anyLong())).thenReturn(expectedItem);

        final ResponseEntity<ItemDto> response = itemController.get(0L);

        assertEquals(expectedItem, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getMapped(anyLong());
        verifyNoMoreInteractions(mockService);
    }

    // If exception thrown within service, null could never be an issue, so test shouldn't be required
//    @Test(expected = NotFoundException.class)
//    public void testGetInvalidId() throws NotFoundException {
//        when(mockService.get(anyLong())).thenReturn(null);
//
//        final ResponseEntity<ItemDto> response = itemController.get(-1L);
//
//        assertNull(response.getBody());
//        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
//        verify(mockService, times(1)).getMapped(anyLong());
//        verifyNoMoreInteractions(mockService);
//    }

    @Test
    public void testGetAllSuccess() {
        final List<ItemDto> items = Arrays.asList(new ItemDto(), new ItemDto());
        when(mockService.getAll()).thenReturn(items);

        final ResponseEntity<List<ItemDto>> response = itemController.getAll();

        assertEquals(items, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockService, times(1)).getAll();
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testGetAllEmptyReturnsSuccess() {
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
    public void testCreateValidData() {
        // TODO: 13/02/2018 Test for URI
        final ItemDto item = new ItemDto(0, "Test Item", BigDecimal.ONE, null);
        when(mockService.save(any(ItemDto.class))).thenReturn(item);

        final ResponseEntity<ItemDto> response = itemController.create(new ItemDto());

        validateWithNoViolations(item);
        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        assertEquals(response.getHeaders().getLocation(), URI.create("/item/0"));
        verify(mockService, times(1)).save(any(ItemDto.class));
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testCreateReturnsNull() {
        when(mockService.save(any(ItemDto.class))).thenReturn(null);

        final ResponseEntity<ItemDto> response = itemController.create(new ItemDto());

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        verify(mockService, times(1)).save(any(ItemDto.class));
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void testInvalidNameWithNull() {
        final ItemDto item = new ItemDto(null, BigDecimal.ONE, null);
        validateWithViolation(item);
    }

    @Test
    public void testInvalidNameWithInvalidLength() {
        // TODO: 13/02/2018 Fix null order
        final ItemDto item = new ItemDto("", BigDecimal.ONE, null);
        validateWithViolation(item);
    }

    @Test
    public void testInvalidPriceWithNull() {
        final ItemDto item = new ItemDto("Item 1", null, null);
        validateWithViolation(item);
    }

    @Test
    public void testInvalidPriceTooManyDigits() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("9999999999999999"), null);
        validateWithViolation(item);
    }

    @Test
    public void testInvalidPriceTooManyDecimals() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.000"), null);
        validateWithViolation(item);
    }

    @Test
    public void testPriceWithNoDecimals() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10"), null);
        validateWithNoViolations(item);
    }

    @Test
    public void testPriceWithDecimals() {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.00"), null);
        validateWithNoViolations(item);
    }

    @Test
    public void testNullOrder() {
        // TODO: 13/02/2018 Eventually when orders are fixed this will not duplicate
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("10.00"), null);
        when(mockService.save(item)).thenReturn(item);

        final ResponseEntity<ItemDto> response = itemController.create(item);

        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        verify(mockService, times(1)).save(any(ItemDto.class));
        verifyNoMoreInteractions(mockService);
        validateWithNoViolations(item);
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
