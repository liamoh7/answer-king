package answer.king.repo;

import answer.king.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

//    @Query("select i from Item i where i.category.id = :id")
//    List<Item> findAllByCategoryId(@Param(value = "id") long categoryId);

    List<Item> findAllByCategoryId(long categoryId);
}