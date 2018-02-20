package answer.king.service;

import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import answer.king.error.InvalidPriceException;
import answer.king.error.InvalidSearchCriteriaException;
import answer.king.error.NotFoundException;
import answer.king.repo.ItemRepository;
import answer.king.service.mapper.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final Mapper<ItemDto, Item> itemMapper;
    private final ItemSearchRepository searchRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, Mapper<ItemDto, Item> itemMapper, ItemSearchRepository searchRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.searchRepository = searchRepository;
    }

    public Item get(long id) throws NotFoundException {
        final Item item = itemRepository.findOne(id);
        if (item == null) throw new NotFoundException();
        return item;
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

    public ItemDto updatePrice(long id, BigDecimal updatedPrice) throws NotFoundException, InvalidPriceException {
        Item item = get(id);

        if (updatedPrice == null || updatedPrice.signum() == -1) {
            throw new InvalidPriceException();
        }

        item.setPrice(updatedPrice);
        item = itemRepository.save(item);
        return itemMapper.mapToDto(item);
    }

    public List<ItemDto> search(String term) throws InvalidSearchCriteriaException {
        if (StringUtils.isEmpty(term)) throw new InvalidSearchCriteriaException();
        term = term.trim();

        final List<Item> items = searchRepository.searchWithName(term);

        return itemMapper.mapToDto(items);
    }
}
