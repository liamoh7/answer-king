package service;

import answer.king.dto.ItemDto;
import answer.king.dto.OrderDto;
import answer.king.entity.Item;
import answer.king.entity.Order;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.service.ItemService;
import answer.king.service.mapper.Mapper;
import org.junit.After;
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
    public void testFindAllWithItems() {
        final List<Item> entities = Arrays.asList(
                new Item("Test 1", BigDecimal.ZERO, new Order()),
                new Item("Test 3", BigDecimal.ONE, new Order()),
                new Item("Test 2", new BigDecimal("210.00"), new Order()));

        final List<ItemDto> expectedDtos = Arrays.asList(
                new ItemDto("Test 1", BigDecimal.ZERO, new OrderDto()),
                new ItemDto("Test 2", BigDecimal.ZERO, new OrderDto()),
                new ItemDto("Test 3", BigDecimal.ZERO, new OrderDto()));

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
    public void testFindAllEmptyList() {
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
    public void testSave() {
        final Item item = new Item("Test 1", BigDecimal.ONE, new Order());
        final ItemDto expectedDto = new ItemDto("Test 1", BigDecimal.ONE, new OrderDto());

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
    public void testGet() throws NotFoundException {
        final Item item = new Item("Test 1", BigDecimal.ZERO, new Order());
        final ItemDto expectedDto = new ItemDto("Test 1", BigDecimal.ZERO, new OrderDto());

        when(mockRepository.findOne(anyLong())).thenReturn(item);
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(expectedDto);

        final ItemDto actualItem = itemService.getMapped(0L);

        assertEquals(expectedDto, actualItem);
        verify(mockRepository, times(1)).findOne(0L);
        verify(mockMapper, times(1)).mapToDto(any(Item.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @After
    public void tearDown() {

    }
}
