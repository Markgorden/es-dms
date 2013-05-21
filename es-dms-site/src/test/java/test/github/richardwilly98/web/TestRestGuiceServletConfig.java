package test.github.richardwilly98.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.web.ShiroWebModule;

import test.github.richardwilly98.rest.TestEsJerseyServletModule;
import com.github.richardwilly98.shiro.EsConfigModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class TestRestGuiceServletConfig extends GuiceServletContextListener {

	Logger log = Logger.getLogger(this.getClass());

	private ServletContext servletContext;

	@Override
	protected Injector getInjector() {
		String securityFilterPath = "/api/*";
		return Guice.createInjector(
				new TestEsJerseyServletModule(securityFilterPath), 
				new EsConfigModule(servletContext, securityFilterPath), 
				ShiroWebModule.guiceFilterModule());
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

}
