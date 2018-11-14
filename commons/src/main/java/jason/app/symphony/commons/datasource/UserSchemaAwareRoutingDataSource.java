package jason.app.symphony.commons.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jason.app.symphony.commons.http.model.SymphonyUser;

public class UserSchemaAwareRoutingDataSource extends AbstractDataSource {
	private String url;
	private LoadingCache<String, DataSource> dataSources = createCache();
	private Map<String, String> props;

	public UserSchemaAwareRoutingDataSource(String url, Map<String, String> props) {
		this.url = url;
		this.props = props;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}

	private DataSource determineTargetDataSource() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String schema = "";
		if(auth!=null && auth.getPrincipal()!=null) {
			if(auth.getPrincipal() instanceof SymphonyUser) {
				schema = ((SymphonyUser)auth.getPrincipal()).getSchema();
			}else if(auth.getPrincipal() instanceof AnonymousAuthenticationToken) {
				schema = ((SymphonyUser)((AnonymousAuthenticationToken)auth.getPrincipal()).getPrincipal()).getSchema();
			}
		}
		try {
			return dataSources.get(schema);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private LoadingCache<String, DataSource> createCache() {
		return CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<String, DataSource>() {
					public DataSource load(String key) {
						return buildDataSourceForSchema(key);
					}
				});
	}

	private DataSource buildDataSourceForSchema(String schema) {
		// e.g. of property:
		// "jdbc:postgresql://localhost:5432/mydatabase?currentSchema="
		String url1 = this.url;
		if (schema != null) {
			url1 = this.url + schema;
		}
		return DataSourceBuilder.create().driverClassName(props.get("driver-class-name"))
				.username(props.get("username")).password(props.get("password")).url(url1).build();
	}
}
