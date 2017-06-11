import com.lewis.mongo.dao.PersonDao;
import com.lewis.mongo.entity.Address;
import com.lewis.mongo.entity.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring-context.xml", "classpath:spring-mongo.xml"})
public class PersonDaoTest {

    @Resource
    private PersonDao personDao;

    /*先往数据库中插入10个person*/
    @Test
    public void testMongo() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            persons.add(new Person("name" + i, i, new Address("广州市", "天河区", i)));
        }
        personDao.save(persons);
    }

    @Test
    public void findMongo() {
        System.out.println(personDao.findByAge(2, 8, "name6"));
    }
}
