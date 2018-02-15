package controller;

import answer.king.controller.ReceiptController;
import answer.king.dto.ReceiptDto;
import answer.king.error.NotFoundException;
import answer.king.service.ReceiptService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptControllerTest {

    @Mock
    private ReceiptService mockReceiptService;
    private ReceiptController receiptController;

    @Before
    public void setUp() {
        receiptController = new ReceiptController(mockReceiptService);
    }

    @Test
    public void testGet() throws NotFoundException {
        final ReceiptDto expectedReceipt = new ReceiptDto();
        when(mockReceiptService.getMapped(anyLong())).thenReturn(expectedReceipt);

        final ResponseEntity<ReceiptDto> response = receiptController.get(0L);

        assertEquals(expectedReceipt, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockReceiptService, times(1)).getMapped(anyLong());
        verifyNoMoreInteractions(mockReceiptService);
    }

    @Test
    public void testGetAll() {
        final List<ReceiptDto> receipts = Arrays.asList(new ReceiptDto(), new ReceiptDto());
        when(mockReceiptService.getAll()).thenReturn(receipts);

        final ResponseEntity<List<ReceiptDto>> response = receiptController.getAll();

        assertEquals(receipts, response.getBody());
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        verify(mockReceiptService, times(1)).getAll();
        verifyNoMoreInteractions(mockReceiptService);
    }

    @After
    public void tearDown() {

    }
}
