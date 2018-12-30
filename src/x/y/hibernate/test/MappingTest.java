package x.y.hibernate.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import x.y.hibernate.domain.Customer;
import x.y.hibernate.domain.LinkMan;
import x.y.hibernate.domain.Role;
import x.y.hibernate.domain.User;
import x.y.hibernate.utils.HibernateUtil;

import java.util.Arrays;
import java.util.List;

public class MappingTest {
    @Test
    public void test1(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        //创建两个客户
        Customer customer1=new Customer();
        customer1.setCust_name("abc");
        Customer customer2=new Customer();
        customer2.setCust_name("xyz");

        //创建三个联系人
        LinkMan linkMan1=new LinkMan();
        linkMan1.setLkm_name("zhangsan");
        LinkMan linkMan2=new LinkMan();
        linkMan2.setLkm_name("lisi");
        LinkMan linkMan3=new LinkMan();
        linkMan3.setLkm_name("wangwu");

        //设置关系
        linkMan1.setCustomer(customer1);
        linkMan2.setCustomer(customer1);
        linkMan3.setCustomer(customer2);
        customer1.getLinkMans().add(linkMan1);
        customer1.getLinkMans().add(linkMan2);
        customer2.getLinkMans().add(linkMan3);

        //保存数据
        session.save(customer1);
        session.save(customer2);
        session.save(linkMan1);
        session.save(linkMan2);
        session.save(linkMan3);

        transaction.commit();

        session.close();
    }

    /**
     * 级联保存或跟新
     * 保存客户级联联系人
     * 操作主体是客户，需要在Customer.bhm.xml配置
     * <set name="linkMans" cascade="save-update">
     */
    @Test
    public void test2(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        Customer customer=new Customer();
        customer.setCust_name("def");

        LinkMan linkMan=new LinkMan();
        linkMan.setLkm_name("赵六");

        customer.getLinkMans().add(linkMan);
        linkMan.setCustomer(customer);

        session.save(customer);

        transaction.commit();
        session.close();
    }

    /**
     * 级联保存或跟新
     * 保存客户级联联系人
     * 操作主体是联系人，需要在LinkMan.bhm.xml配置
     * <many-to-one name="customer" cascade="save-update" class="x.y.hibernate.domain.Customer" column="lkm_cust_id"/>
     */
    @Test
    public void test3(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        Customer customer=new Customer();
        customer.setCust_name("def");

        LinkMan linkMan=new LinkMan();
        linkMan.setLkm_name("赵六");

        customer.getLinkMans().add(linkMan);
        linkMan.setCustomer(customer);

        session.save(linkMan);

        transaction.commit();
        session.close();
    }

    /**
     * 级联删除,如果没有设置级联，也能删除，只不过将多的一方的外键设置为null，再删除
     */
    @Test
    public void test4(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        Customer customer=session.get(Customer.class,3l);

        session.delete(customer);

        transaction.commit();
        session.close();
    }

    @Test
    public void test5(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        User user1=new User();
        user1.setUser_name("小红");
        User user2=new User();
        user2.setUser_name("小明");

        Role role1=new Role();
        role1.setRole_name("研发部");
        Role role2=new Role();
        role2.setRole_name("市场部");
        Role role3=new Role();
        role3.setRole_name("公关部");

        user1.getRoles().add(role1);
        user1.getRoles().add(role2);
        user2.getRoles().add(role2);
        user2.getRoles().add(role3);

        role1.getUsers().add(user1);
        role2.getUsers().add(user1);
        role2.getUsers().add(user2);
        role3.getUsers().add(user2);

        //保存操作：多对对建立了双向的关系必须有一方放弃外键的维护权（否则往中间表插入了两次）
        session.save(user1);
        session.save(user2);
        session.save(role1);
        session.save(role2);
        session.save(role3);

        transaction.commit();
        session.close();
    }

    /**
     * HQL多表查询
     */
    @Test
    public void test6(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        List<Object[]> list=session.createQuery("from Customer c inner join c.linkMans").list();
        for (Object[] objects:list){
            System.out.println(Arrays.toString(objects));
        }

        /**
         * 迫切内连接，就是在inner join 后添加一个 fetch关键字，通知 hibernate将LinkMans的数据封装到customer中
         * List<Customer> list=session.createQuery("select distinct c from Customer c inner join fetch c.linkMans").list();
         */

        transaction.commit();
        session.close();
    }
}
