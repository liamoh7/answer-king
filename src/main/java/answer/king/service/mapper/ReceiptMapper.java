package answer.king.service.mapper;

import answer.king.dto.ReceiptDto;
import answer.king.entity.Receipt;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReceiptMapper implements Mapper<ReceiptDto, Receipt> {

    private final DozerBeanMapper mapper;

    @Autowired
    public ReceiptMapper(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ReceiptDto mapToDto(Receipt entity) {
        return mapper.map(entity, ReceiptDto.class);
    }

    @Override
    public List<ReceiptDto> mapToDto(List<Receipt> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Receipt mapToEntity(ReceiptDto dto) {
        return mapper.map(dto, Receipt.class);
    }

    @Override
    public List<Receipt> mapToEntity(List<ReceiptDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
