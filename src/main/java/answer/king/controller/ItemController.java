package answer.king.controller;

import answer.king.dto.ItemDto;
import answer.king.error.NotFoundException;
import answer.king.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    private static final String ITEM_DETAIL_PATH = "/item/{id}";

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ItemDto>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto item) {
        final ItemDto itemResponse = itemService.save(item);
        if (itemResponse == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(URI.create("/item/" + itemResponse.getId())).build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ItemDto> get(@PathVariable(value = "id") long id) throws NotFoundException {
        return ResponseEntity.ok(itemService.getMapped(id));
    }
}
