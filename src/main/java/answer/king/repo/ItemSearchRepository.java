package answer.king.repo;

import answer.king.entity.Item;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class ItemSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Item> searchWithName(String term) {
        final FullTextEntityManager fm = getTextEntityManager();

        final QueryBuilder qb = fm.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();
        final Query query = qb.keyword().fuzzy().withEditDistanceUpTo(1).onField("name").matching(term).createQuery();

        // noinspection unchecked
        return fm.createFullTextQuery(query, Item.class).getResultList();
    }

    public List<Item> searchByCategoryName(String categoryName) {
        final FullTextEntityManager fm = getTextEntityManager();
        final QueryBuilder qb = fm.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();

        final Query query = qb.keyword().onField("category.name").matching(categoryName).createQuery();

        // noinspection unchecked
        return fm.createFullTextQuery(query, Item.class).getResultList();
    }

    public List<Item> searchByCategoryId(long categoryId) {
        final FullTextEntityManager fm = getTextEntityManager();
        final QueryBuilder qb = fm.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();

        final Query query = qb.keyword().onField("category.id").matching(categoryId).createQuery();

        // noinspection unchecked
        return fm.createFullTextQuery(query, Item.class).getResultList();
    }


    private FullTextEntityManager getTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }
}
