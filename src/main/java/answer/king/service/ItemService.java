package answer.king.service;

import answer.king.dto.CreatableItemDto;
import answer.king.dto.ItemDto;
import answer.king.entity.Category;
import answer.king.entity.Item;
import answer.king.error.InvalidCriteriaException;
import answer.king.error.InvalidPriceException;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.repo.ItemSearchRepository;
import answer.king.service.mapper.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static answer.king.util.Models.throwNotFoundIfNull;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final Mapper<ItemDto, Item> itemMapper;
    private final ItemSearchRepository searchRepository;
    private final CategoryService categoryService;

    @Autowired
    public ItemService(ItemRepository itemRepository, Mapper<ItemDto, Item> itemMapper, ItemSearchRepository searchRepository, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.searchRepository = searchRepository;
        this.categoryService = categoryService;
    }

    public Item get(long id) throws NotFoundException {
        final Item item = itemRepository.findOne(id);
        return throwNotFoundIfNull(item);
    }

    public ItemDto getMapped(long id) throws NotFoundException {
        return itemMapper.mapToDto(get(id));
    }

    public List<ItemDto> getAll() {
        List<Item> items = itemRepository.findAll();
        if (items == null) items = new ArrayList<>();
        return itemMapper.mapToDto(items);
    }

    public ItemDto save(ItemDto item) {
        final Item entity = itemMapper.mapToEntity(item);
        return itemMapper.mapToDto(itemRepository.save(entity));
    }

    public ItemDto create(CreatableItemDto item) throws NotFoundException {
        final Category category = categoryService.get(item.getCategoryId());

        Item entity = new Item();
        entity.setName(item.getName());
        entity.setPrice(item.getPrice());
        entity.setDescription(item.getDescription());
        entity.setCategory(category);
        entity = itemRepository.save(entity);

        return itemMapper.mapToDto(entity);
    }

    public ItemDto updatePrice(long id, BigDecimal updatedPrice) throws NotFoundException, InvalidPriceException {
        Item item = get(id);

        if (updatedPrice == null || updatedPrice.signum() == -1) {
            throw new InvalidPriceException();
        }

        item.setPrice(updatedPrice);
        item = itemRepository.save(item);
        return itemMapper.mapToDto(item);
    }

    public List<ItemDto> searchByItemName(String term) throws InvalidCriteriaException {
        final List<Item> items = searchRepository.searchWithName(trimOrThrow(term));
        return itemMapper.mapToDto(items);
    }

    public List<ItemDto> searchByCategory(long categoryId) {
        final List<Item> items = itemRepository.findAllByCategoryId(categoryId);
        return itemMapper.mapToDto(items);
    }

    private String trimOrThrow(String str) throws InvalidCriteriaException {
        if (StringUtils.isEmpty(str)) throw new InvalidCriteriaException();
        return str.trim();
    }
}
