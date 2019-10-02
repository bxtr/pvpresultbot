package org.bxtr.PvpBot;

import org.bxtr.PvpBot.model.Player;
import org.bxtr.PvpBot.repository.PlayerCrudRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DatabaseTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PlayerCrudRepository playerCrudRepository;

    @Test
    public void createUserTest() {
        Player player = new Player().setName("bxtr2121");
        testEntityManager.persist(player);
        playerCrudRepository.save(player);
    }
}
