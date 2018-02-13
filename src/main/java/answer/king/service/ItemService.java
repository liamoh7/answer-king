package answer.king.service;

import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import answer.king.repo.ItemRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    private DozerBeanMapper mapper;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemDto get(long id) {
        return mapToDto(itemRepository.findOne(id));
    }

    public List<ItemDto> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemDto save(ItemDto item) {
        final Item entity = mapToEntity(item);
        return mapToDto(itemRepository.save(entity));
    }

    private ItemDto mapToDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    private Item mapToEntity(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }
}
