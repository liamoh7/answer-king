package com.loh;

import answer.king.Application;
import answer.king.dto.ItemDto;
import answer.king.entity.Item;
import answer.king.repo.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class ItemSearchTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        addTestData();
    }

    @Test
    @Rollback
    public void searchForItemsWithNameSimilarToKeywordItemWithResults() {
        final ItemDto expectedItem1 = new ItemDto("Item 1", new BigDecimal("10.00"));
        final ItemDto expectedItem2 = new ItemDto("Item 2", new BigDecimal("10.00"));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", "item");

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final ResponseEntity<List<ItemDto>> actual = restTemplate.exchange("/item/search", HttpMethod.POST,
                request, new ParameterizedTypeReference<List<ItemDto>>() {
                });

        assertEquals(2, actual.getBody().size());
        assertEquals(expectedItem1, actual.getBody().get(0));
        assertEquals(expectedItem2, actual.getBody().get(1));
        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    @Rollback
    public void searchWithNoResultsExpected() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", "Walkers");

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final ResponseEntity<List<ItemDto>> actual = restTemplate.exchange("/item/search", HttpMethod.POST,
                request, new ParameterizedTypeReference<List<ItemDto>>() {
                });

        assertTrue(actual.getBody().isEmpty());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    private void addTestData() {
        final Item item1 = new Item("Item 1", BigDecimal.TEN);
        final Item item2 = new Item("Item 2", BigDecimal.TEN);
        final Item item3 = new Item("Orange Juice", BigDecimal.TEN);
        final Item item4 = new Item("Coca Cola", BigDecimal.TEN);

        itemRepository.save(Arrays.asList(item1, item2, item3, item4));
    }
}
