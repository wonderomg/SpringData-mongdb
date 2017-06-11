import com.lewis.mongo.mongoDao.PersonMongoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by liu on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring-context.xml", "classpath:spring-mongo.xml"})
public class MongoTemplateTest {
    @Resource
    private PersonMongoImpl personMongo;

    @Test
    public void testMongoTemplate() {

        //personMongo.insertPerson(new Person("Lewis",24,new Address("广州","天河",20)));
        personMongo.removePerson("name3");
        personMongo.updatePerson();
        System.out.println(personMongo.findAll());
        System.out.println(personMongo.findForRequery("Lewis"));
    }
}