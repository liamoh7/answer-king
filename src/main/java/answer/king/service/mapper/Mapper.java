package answer.king.service.mapper;

import java.util.List;

public interface Mapper<D, E> {

    D mapToDto(E entity);

    List<D> mapToDto(List<E> entities);

    E mapToEntity(D dto);

    List<E> mapToEntity(List<D> dtos);
}
