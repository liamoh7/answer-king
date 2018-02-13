package answer.king.service.mapper;

import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper implements Mapper<ItemDto, Item> {

    private final DozerBeanMapper mapper;

    @Autowired
    public ItemMapper(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ItemDto mapToDto(Item entity) {
        return mapper.map(entity, ItemDto.class);
    }

    @Override
    public List<ItemDto> mapToDto(List<Item> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item mapToEntity(ItemDto dto) {
        return mapper.map(dto, Item.class);
    }

    @Override
    public List<Item> mapToEntity(List<ItemDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
