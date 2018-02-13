package service;

import answer.king.entity.Item;
import answer.king.repo.ItemRepository;
import answer.king.service.ItemService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository mockRepository;
    private ItemService itemService;

    @Before
    public void setUp() {
        itemService = new ItemService(mockRepository);
    }

    @Test
    public void testFindAllWithItems() {
        final List<Item> items = Arrays.asList(new Item(), new Item());
        when(mockRepository.findAll()).thenReturn(items);

        final List<Item> actualItems = itemService.getAll();

        assertEquals(items, actualItems);
        assertTrue(items.size() == actualItems.size());
        verify(mockRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void testFindAllEmptyList() {
        final List<Item> emptyList = new ArrayList<>();
        when(mockRepository.findAll()).thenReturn(new ArrayList<>());

        final List<Item> items = itemService.getAll();

        assertEquals(emptyList, items);
        assertTrue(emptyList.size() == items.size());
        verify(mockRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void testSaveItem() {
        final Item item = new Item("Item", BigDecimal.ONE, null);
        when(mockRepository.save(any(Item.class))).thenReturn(item);

        final Item actualItem = itemService.save(item);

        assertEquals(item, actualItem);
        verify(mockRepository, times(1)).save(item);
        verifyNoMoreInteractions(mockRepository);
    }

    @After
    public void tearDown() {

    }
}
