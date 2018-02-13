package controller;

import answer.king.controller.ItemController;
import answer.king.model.Item;
import answer.king.service.ItemService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    // TODO: 13/02/2018 Fix Tests

    @Mock
    private ItemService mockService;
    private ItemController itemController;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UriComponentsBuilder mockUriBuilder;
    private Validator validator;

    @Before
    public void setUp() {
        itemController = new ItemController(mockService);
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        when(mockUriBuilder.path("/item/{id}").buildAndExpand(anyLong())).thenReturn(any(UriComponents.class));
    }

//    @Test
//    public void testGetAllSuccess() {
//        final List<Item> items = Arrays.asList(new Item(), new Item());
//        when(mockService.getAll()).thenReturn(items);
//
//        final ResponseEntity<List<Item>> response = itemController.getAll();
//
//        assertEquals(items, response.getBody());
//        assertTrue(response.getStatusCode() == HttpStatus.OK);
//        verify(mockService, times(1)).getAll();
//        verifyNoMoreInteractions(mockService);
//    }
//
//    @Test
//    public void testGetAllNotFoundWhenNull() {
//        when(mockService.getAll()).thenReturn(null);
//
//        final ResponseEntity<List<Item>> response = itemController.getAll();
//
//        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
//        verify(mockService, times(1)).getAll();
//        verifyNoMoreInteractions(mockService);
//    }
//
//    @Test
//    public void testGetAllEmptyReturnsSuccess() {
//        final List<Item> items = new ArrayList<>();
//        when(mockService.getAll()).thenReturn(items);
//
//        final ResponseEntity<List<Item>> response = itemController.getAll();
//
//        assertEquals(items, response.getBody());
//        assertTrue(response.getStatusCode() == HttpStatus.OK);
//        verify(mockService, times(1)).getAll();
//        verifyNoMoreInteractions(mockService);
//    }
//
//
//    @Test
//    public void testCreateValidData() {
//        final Item item = new Item("Test Item", BigDecimal.ONE, new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final ResponseEntity<Item> response = itemController.create(item, mockUriBuilder);
//        System.out.println(response.getStatusCode());
////        validateWithNoViolations(response.getBody());
////        assertEquals(item, response.getBody());
////        assertTrue(response.getStatusCode() == HttpStatus.OK);
////        verify(mockService, times(1)).save(item);
////        verifyNoMoreInteractions(mockService);
//    }
//
//    @Test
//    public void testInvalidNameWithNull() {
//        final Item item = new Item(null, BigDecimal.ONE, new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final ResponseEntity<Item> response = itemController.create(item, mockUriBuilder);
//
//        validateWithViolations(response.getBody());
//        assertEquals(item, response.getBody());
//        assertTrue(response.getStatusCode() == HttpStatus.OK);
//        verify(mockService, times(1)).save(item);
//        verifyNoMoreInteractions(mockService);
//
//    }

//    @Test
//    public void testInvalidNameWithInvalidLength() {
//        final Item item = new Item("", BigDecimal.ONE, new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithViolations(actualItem);
//    }
//
//    @Test
//    public void testInvalidPriceWithNull() {
//        final Item item = new Item("Item 1", null, new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithViolations(actualItem);
//    }
//
//    @Test
//    public void testInvalidPriceTooManyDigits() {
//        final Item item = new Item("Item 1", new BigDecimal("9999999999999999"), new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithViolations(actualItem);
//    }
//
//    @Test
//    public void testInvalidPriceTooManyDecimals() {
//        final Item item = new Item("Item 1", new BigDecimal("10.000"), new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithViolations(actualItem);
//    }
//
//    @Test
//    public void testPriceWithNoDecimals() {
//        final Item item = new Item("Item 1", new BigDecimal("10"), new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithNoViolations(actualItem);
//    }
//
//    @Test
//    public void testPriceWithDecimals() {
//        final Item item = new Item("Item 1", new BigDecimal("10.00"), null);
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithNoViolations(actualItem);
//    }
//
//    @Test
//    public void testNullOrder() {
//        final Item item = new Item("Item 1", new BigDecimal("10.00"), new Order());
//        when(mockService.save(item)).thenReturn(item);
//
//        final Item actualItem = itemController.create(item);
//        validateWithNoViolations(actualItem);
//    }

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
