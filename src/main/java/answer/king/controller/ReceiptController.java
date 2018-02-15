package answer.king.controller;

import answer.king.dto.ReceiptDto;
import answer.king.error.NotFoundException;
import answer.king.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ReceiptDto> get(@PathVariable(value = "id") long id) throws NotFoundException {
        return ResponseEntity.ok(receiptService.getMapped(id));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReceiptDto>> getAll() {
        return ResponseEntity.ok(receiptService.getAll());
    }
}
