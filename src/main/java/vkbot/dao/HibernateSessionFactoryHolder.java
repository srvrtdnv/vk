package vkbot.dao;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import vkbot.Flight;

public class HibernateSessionFactoryHolder {
	private static SessionFactory factory;
	
	public static SessionFactory getFactory() {
		if (factory == null) {
			synchronized (HibernateSessionFactoryHolder.class) {
				if (factory == null) {
					Configuration conf = new Configuration().configure();
					conf.addAnnotatedClass(Flight.class);
					StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(conf.getProperties());
					factory = conf.buildSessionFactory(builder.build());
				}
			}
		}
		return factory;
	}

	
}
