package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Item;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save(){

        Item item = new Item("book");
        itemRepository.save(item);
        item.setName("S");
        Optional<Item> findItem = itemRepository.findById(item.getId());
        System.out.println("item -> "+findItem.get());

    }

}