package answer.king.service;

import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import answer.king.repo.ItemRepository;
import answer.king.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final Mapper<ItemDto, Item> itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, Mapper<ItemDto, Item> itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDto get(long id) {
        return itemMapper.mapToDto(itemRepository.findOne(id));
    }

    public List<ItemDto> getAll() {
        return itemMapper.mapToDto(itemRepository.findAll());
    }

    public ItemDto save(ItemDto item) {
        final Item entity = itemMapper.mapToEntity(item);
        return itemMapper.mapToDto(itemRepository.save(entity));
    }
}
