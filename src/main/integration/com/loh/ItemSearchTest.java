package com.loh;

import answer.king.Application;
import answer.king.entity.Category;
import answer.king.entity.Item;
import answer.king.repo.CategoryRepository;
import answer.king.repo.ItemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
@Rollback
public class ItemSearchTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void searchForItemsWithNameSimilarToKeywordItemWithResults() throws Exception {
        addTestData();

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("q", "item");

        mockMvc.perform(post("/item/search").params(paramMap))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].price").value(new BigDecimal("25.99")))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].price").value(new BigDecimal("25.99")));
    }

    @Test
    @WithMockUser
    public void searchForItemsWithTermWithNoResults() throws Exception {
        addTestData();

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("q", "walker");

        mockMvc.perform(post("/item/search").params(paramMap))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    public void searchForItemsByCategoryId() throws Exception {
        addTestData();

        final Category expectedCategory = new Category("Drinks");
        expectedCategory.setId(2);

        mockMvc.perform(post("/item/category/2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Orange Juice"))
                .andExpect(jsonPath("$[0].price").value(new BigDecimal("25.99")))
                .andExpect(jsonPath("$[0].category").value(expectedCategory))
                .andExpect(jsonPath("$[1].name").value("Coca Cola"))
                .andExpect(jsonPath("$[1].price").value(new BigDecimal("25.99")))
                .andExpect(jsonPath("$[1].category").value(expectedCategory));
    }

    @After
    public void tearDown() {
        // tear down as rollback not working correctly
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private void addTestData() {
        final Category drinks = new Category("Drinks");
        final Category misc = new Category("Misc");

        final Item item1 = new Item("Item 1", new BigDecimal("25.99"), "desc", misc);
        final Item item2 = new Item("Item 2", new BigDecimal("25.99"), "desc", misc);
        final Item item3 = new Item("Orange Juice", new BigDecimal("25.99"), "desc", drinks);
        final Item item4 = new Item("Coca Cola", new BigDecimal("25.99"), "desc", drinks);

        itemRepository.save(Arrays.asList(item1, item2, item3, item4));
    }
}
