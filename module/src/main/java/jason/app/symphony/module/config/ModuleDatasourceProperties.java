package jason.app.symphony.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.module.datasource")
public class ModuleDatasourceProperties {

	private String url;

	private String username;

	private String password;
	
	private String resourceName;
	
	private String driver;
	
	private String type;
	
	private String hibernateProperties;
	
	private String persistenceUnitName;
	
	private boolean pinGlobalTxToPhysicalConnection;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public boolean isPinGlobalTxToPhysicalConnection() {
		return pinGlobalTxToPhysicalConnection;
	}

	public void setPinGlobalTxToPhysicalConnection(boolean pinGlobalTxToPhysicalConnection) {
		this.pinGlobalTxToPhysicalConnection = pinGlobalTxToPhysicalConnection;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHibernateProperties() {
		return hibernateProperties;
	}

	public void setHibernateProperties(String hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public void setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}	
	
	

}
