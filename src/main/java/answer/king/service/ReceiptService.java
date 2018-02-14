package answer.king.service;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.entity.Order;
import answer.king.entity.Receipt;
import answer.king.repo.ReceiptRepository;
import answer.king.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptRepository repository;
    private final Mapper<ReceiptDto, Receipt> mapper;
    private final Mapper<OrderDto, Order> orderMapper;

    @Autowired
    public ReceiptService(ReceiptRepository repository, Mapper<ReceiptDto, Receipt> mapper, Mapper<OrderDto, Order> orderMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.orderMapper = orderMapper;
    }

    public List<ReceiptDto> getAll() {
        return mapper.mapToDto(repository.findAll());
    }

    public ReceiptDto get(long id) {
        return mapper.mapToDto(repository.findOne(id));
    }

    public ReceiptDto create(Order order, BigDecimal payment) {
        if (order == null || payment == null) return null;

        final Receipt receipt = new Receipt();
        receipt.setOrder(order);
        receipt.setPayment(payment);

        final Receipt entity = repository.save(receipt);
        return mapper.mapToDto(entity);
    }
}
