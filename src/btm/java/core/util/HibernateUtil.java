package btm.java.core.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	public static final StandardServiceRegistry registry;
	public static final SessionFactory sf;

	static {
		registry = new StandardServiceRegistryBuilder().configure().build();
		sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
	}

	public static Session openSession() {
		return sf.openSession();
	}

}
