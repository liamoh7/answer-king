package answer.king.service.mapper;

import answer.king.dto.OrderDto;
import answer.king.entity.Order;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper implements Mapper<OrderDto, Order> {

    private final DozerBeanMapper mapper;

    @Autowired
    public OrderMapper(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public OrderDto mapToDto(Order entity) {
        return mapper.map(entity, OrderDto.class);
    }

    @Override
    public List<OrderDto> mapToDto(List<Order> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Order mapToEntity(OrderDto dto) {
        return mapper.map(dto, Order.class);
    }

    @Override
    public List<Order> mapToEntity(List<OrderDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
