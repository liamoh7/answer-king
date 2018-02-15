package answer.king.service.mapper;

import answer.king.dto.LineItemDto;
import answer.king.entity.LineItem;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LineItemMapper implements Mapper<LineItemDto, LineItem> {

    private final DozerBeanMapper mapper;

    @Autowired
    public LineItemMapper(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public LineItemDto mapToDto(LineItem entity) {
        return mapper.map(entity, LineItemDto.class);
    }

    @Override
    public List<LineItemDto> mapToDto(List<LineItem> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LineItem mapToEntity(LineItemDto dto) {
        return mapper.map(dto, LineItem.class);
    }

    @Override
    public List<LineItem> mapToEntity(List<LineItemDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
