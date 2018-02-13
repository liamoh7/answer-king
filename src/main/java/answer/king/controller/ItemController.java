package answer.king.controller;

import answer.king.entity.Item;
import answer.king.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<List<Item>> getAll() {
        final List<Item> items = itemService.getAll();
        if (items == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(items);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Item> create(@RequestBody Item item, UriComponentsBuilder builder) {
        final Item itemResponse = itemService.save(item);
        if (itemResponse == null) return ResponseEntity.badRequest().build();

        final UriComponents uriComponents = builder.path(ITEM_DETAIL_PATH).buildAndExpand(item.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Item> get(@PathVariable(value = "id") long id) {
        final Item item = itemService.get(id);
        if (item == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(item);
    }
}
