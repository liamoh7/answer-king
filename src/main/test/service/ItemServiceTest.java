package service;

import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import answer.king.error.InvalidPriceException;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.service.ItemService;
import answer.king.service.mapper.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository mockRepository;
    @Mock
    private Mapper<ItemDto, Item> mockMapper;
    private ItemService itemService;

    @Before
    public void setUp() {
        itemService = new ItemService(mockRepository, mockMapper);
    }

    @Test
    public void getAllItemsWhenItemsAreAvailableReturnsListOfItems() {
        final List<Item> entities = Arrays.asList(
                new Item("Test 1", BigDecimal.ZERO),
                new Item("Test 3", BigDecimal.ONE),
                new Item("Test 2", new BigDecimal("210.00")));

        final List<ItemDto> expectedDtos = Arrays.asList(
                new ItemDto("Test 1", BigDecimal.ZERO),
                new ItemDto("Test 2", BigDecimal.ZERO),
                new ItemDto("Test 3", BigDecimal.ZERO));

        when(mockRepository.findAll()).thenReturn(entities);
        when(mockMapper.mapToDto(anyList())).thenReturn(expectedDtos);

        final List<ItemDto> actualItems = itemService.getAll();

        assertEquals(expectedDtos, actualItems);
        verify(mockRepository, times(1)).findAll();
        verify(mockMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void getAllItemsWhenNoneAreAvailableReturnsEmptyList() {
        final List<Item> entities = Collections.emptyList();
        final List<ItemDto> expectedDtos = Collections.emptyList();

        when(mockRepository.findAll()).thenReturn(entities);
        when(mockMapper.mapToDto(anyList())).thenReturn(expectedDtos);

        final List<ItemDto> actualItems = itemService.getAll();

        // TODO: 13/02/2018 Current implementation is redundantly hitting mapper even with empty list
        assertEquals(expectedDtos, actualItems);
        verify(mockRepository, times(1)).findAll();
        verify(mockMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void savingValidItemReturnsItemMappedToDto() {
        final Item item = new Item("Test 1", BigDecimal.ONE);
        final ItemDto expectedDto = new ItemDto("Test 1", BigDecimal.ONE);

        when(mockMapper.mapToEntity(any(ItemDto.class))).thenReturn(item);
        when(mockRepository.save(any(Item.class))).thenReturn(item);
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(expectedDto);

        final ItemDto actualItem = itemService.save(expectedDto);

        assertEquals(expectedDto, actualItem);
        verify(mockMapper, times(1)).mapToEntity(expectedDto);
        verify(mockRepository, times(1)).save(item);
        verify(mockMapper, times(1)).mapToDto(item);
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void getItemWithValidIdReturnsItem() throws NotFoundException {
        final Item item = new Item("Test 1", BigDecimal.ZERO);
        final ItemDto expectedDto = new ItemDto("Test 1", BigDecimal.ZERO);

        when(mockRepository.findOne(anyLong())).thenReturn(item);
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(expectedDto);

        final ItemDto actualItem = itemService.getMapped(0L);

        assertEquals(expectedDto, actualItem);
        verify(mockRepository, times(1)).findOne(0L);
        verify(mockMapper, times(1)).mapToDto(any(Item.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test(expected = NotFoundException.class)
    public void updatingItemPriceWithInvalidIdThrowsException() throws NotFoundException, InvalidPriceException {
        when(mockRepository.findOne(anyLong())).thenReturn(null);

        itemService.updatePrice(0, BigDecimal.ONE);
    }

    @Test(expected = InvalidPriceException.class)
    public void updatingItemWithNullPriceThrowsException() throws NotFoundException, InvalidPriceException {
        when(mockRepository.findOne(anyLong())).thenReturn(new Item());

        itemService.updatePrice(0L, null);

        verify(mockRepository, times(1)).findOne(0L);
        verifyNoMoreInteractions(mockRepository);
    }

    @Test(expected = InvalidPriceException.class)
    public void updatingItemWithNegativePriceThrowsException() throws NotFoundException, InvalidPriceException {
        when(mockRepository.findOne(anyLong())).thenReturn(new Item());

        itemService.updatePrice(0L, new BigDecimal("-1.00"));

        verify(mockRepository, times(1)).findOne(0L);
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void updatingPriceWithValidInfoReturnsItemWithNewPrice() throws NotFoundException, InvalidPriceException {
        final ItemDto item = new ItemDto("Item 1", new BigDecimal("32.00"));

        when(mockRepository.findOne(anyLong())).thenReturn(new Item("Item 1", BigDecimal.TEN));
        when(mockRepository.save(any(Item.class))).thenReturn(new Item("Item 1", new BigDecimal("32.00")));
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(item);

        final ItemDto actualItem = itemService.updatePrice(0L, new BigDecimal("32.00"));

        assertEquals(item, actualItem);
        verify(mockRepository, times(1)).findOne(anyLong());
        verify(mockRepository, times(1)).save(any(Item.class));
        verify(mockMapper, times(1)).mapToDto(any(Item.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }
}
