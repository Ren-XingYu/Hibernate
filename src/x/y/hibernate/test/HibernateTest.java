package x.y.hibernate.test;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.junit.Test;
import x.y.hibernate.domain.Customer;
import x.y.hibernate.utils.HibernateUtil;

import java.io.Serializable;
import java.util.List;

public class HibernateTest {
    @Test
    public void test1(){
        //1、加载核心配置文件
        Configuration configuration=new Configuration().configure();
        //configuration.addResource("x/y/hibernate/domain/Customer.hbm.xml");//手动加载配置文件
        //2、创建一个SessionFactory对象;类似于JDBC中的连接池
        SessionFactory factory=configuration.buildSessionFactory();
        //3、通过SessionFactory回去Session对象,类似于JDBC中的Connection对象
        Session session=factory.openSession();
        //4、手动开启事务
        Transaction transaction=session.beginTransaction();
        //5、编写代码
        Customer customer=new Customer();
        customer.setCust_name("abc");
        session.save(customer);
        //6、事务提交
        transaction.commit();
        //7、释放资源
        session.close();
        factory.close();
    }

    @Test
    public void test2(){
        //session不是线程安全的，要定义成局部的
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        Customer customer=new Customer();
        customer.setCust_name("xyz");
        session.save(customer);
        transaction.commit();
        session.close();
    }

    @Test
    public void test3(){
        //session不是线程安全的，要定义成局部的
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        Customer customer=session.get(Customer.class,1l);//get执行到这句就会发送sql语句，返回真实对象，没有返回null
        //Customer customer=session.load(Customer.class,1l);
        System.out.println(customer);//load输出的时候才会发送sql语句，懒加载，返回代理对象(javassist技术)，没有返回异常
        transaction.commit();
        session.close();
    }

    @Test
    public void test4(){
        //session不是线程安全的，要定义成局部的
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        /**
         * 不推荐
        Customer customer=new Customer();
        customer.setCust_id(1l);
        customer.setCust_name("fgh");
        session.update(customer);*/

        /**
         * 先查询后修改
         */
        Customer customer=session.get(Customer.class,1l);
        customer.setCust_name("def");
        session.update(customer);
        transaction.commit();
        session.close();
    }

    @Test
    public void test5(){
        //session不是线程安全的，要定义成局部的
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        /**
         * 不推荐
         Customer customer=new Customer();
         customer.setCust_id(1l);
         session.delete(customer);*/

        /**
         * 先查询后删除(级联删除必须先查询再删除)
         */
        Customer customer=session.get(Customer.class,1l);
        session.delete(customer);
        transaction.commit();
        session.close();
    }

    @Test
    public void test6(){
        //session不是线程安全的，要定义成局部的
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        Query query=session.createQuery("from Customer");
        //SQLQuery query=session.createSQLQuery("select * from cst_customer");
        //query.addEntity(Customer.class);
        List<Customer> lists=query.list();
        for (Customer customer:lists){
            System.out.println(customer);
        }
        transaction.commit();
        session.close();
    }

    @Test
    public void test7(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        Customer customer=new Customer();//瞬时态
        customer.setCust_name("hahaha");

        Serializable id=session.save(customer);//持久态

        session.get(Customer.class,id);

        transaction.commit();
        session.close();

        System.out.println("Customer_Name:"+customer.getCust_name());//session已经关闭，托管态
    }

    /**
     * Query查询，接受HQl
     */
    @Test
    public void test8(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        String hql="from Customer where cust_name like ?0";
        Query query=session.createQuery(hql);
        query.setParameter(0,"a%");
        //分页
        //query.setFirstResult(0);//从那一条记录开始
        //query.setMaxResults(5);//每页几条数据
        List<Customer> list=query.list();
        for (Customer customer:list){
            System.out.println(customer);
        }
        transaction.commit();
        session.close();
    }

    /**
     * Criteria查询(QBC：Query By Criteria)
     */
    @Test
    public void test9(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        Criteria criteria=session.createCriteria(Customer.class);
        criteria.add(Restrictions.like("cust_name","a%"));
        //分页查询
        //criteria.setFirstResult(0);
        //criteria.setMaxResults(5);
        List<Customer> list=criteria.list();
        for (Customer customer:list){
            System.out.println(customer);
        }
        transaction.commit();
        session.close();
    }

    @Test
    public void test10(){
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        transaction.commit();
        session.close();
    }
}
