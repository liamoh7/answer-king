package answer.king.controller;

import answer.king.dto.OrderDto;
import answer.king.dto.ReceiptDto;
import answer.king.error.InvalidPaymentException;
import answer.king.error.NotFoundException;
import answer.king.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<OrderDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<OrderDto> create() {
        return ResponseEntity.ok(orderService.save(new OrderDto()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderDto> get(@PathVariable(value = "id") long id) throws NotFoundException {
        return ResponseEntity.ok(orderService.getMapped(id));
    }

    @RequestMapping(value = "/{id}/addItem/{itemId}", method = RequestMethod.PUT)
    public ResponseEntity<OrderDto> addItem(@PathVariable("id") long id, @PathVariable("itemId") long itemId, @RequestBody int quantity) throws NotFoundException {
        orderService.addItem(id, itemId, quantity);
        return ResponseEntity.created(URI.create("/item/" + itemId)).build();
    }

    @RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
    public ResponseEntity<ReceiptDto> pay(@PathVariable("id") long id, @RequestBody BigDecimal payment) throws InvalidPaymentException, NotFoundException {
        return ResponseEntity.ok(orderService.pay(id, payment));
    }
}
