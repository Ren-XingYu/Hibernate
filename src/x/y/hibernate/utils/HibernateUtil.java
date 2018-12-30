package x.y.hibernate.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final Configuration cfg;
    private static final SessionFactory sf;

    static {
        cfg=new Configuration().configure();
        sf=cfg.buildSessionFactory();
    }

    public static Session getSession(){
        return sf.openSession();
    }

    /**
     * 获取线程绑定的Session(通过ThreadLocal实现)
     * 需要在核心配置文件中配置
     * <property name="hibernate.current_session_context_class">thread</property>
     * @return
     */
    public static Session getCurrentSession(){
        return sf.getCurrentSession();
    }
}
