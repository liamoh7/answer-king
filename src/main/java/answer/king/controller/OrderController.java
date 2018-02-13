package answer.king.controller;

import answer.king.dto.OrderDto;
import answer.king.entity.Receipt;
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
        final List<OrderDto> orders = orderService.getAll();
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(orders);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<OrderDto> create() {
        return ResponseEntity.ok(orderService.save(new OrderDto()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderDto> get(@PathVariable(value = "id") int id) {
        return null;
    }

    @RequestMapping(value = "/{id}/addItem/{itemId}", method = RequestMethod.PUT)
    public ResponseEntity<OrderDto> addItem(@PathVariable("id") long id, @PathVariable("itemId") long itemId) {
        final OrderDto order = orderService.addItem(id, itemId);
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.created(URI.create("/item/" + itemId)).build();
    }

    @RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
    public ResponseEntity<Receipt> pay(@PathVariable("id") long id, @RequestBody BigDecimal payment) {
        if (payment == null) return ResponseEntity.badRequest().build();

        final Receipt receipt = orderService.pay(id, payment);
        if (receipt == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(receipt);
    }
}
