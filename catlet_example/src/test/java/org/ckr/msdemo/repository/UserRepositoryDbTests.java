package org.ckr.msdemo.repository;


import org.ckr.msdemo.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class UserRepositoryDbTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();

        user.setUserName("abc");

        userRepository.save(user);

        List<User> userList = userRepository.findAll();

        assertThat(userList.size()).isEqualTo(1);
        assertThat(userList.get(0).getUserName()).isEqualTo("abc");

    }
}
