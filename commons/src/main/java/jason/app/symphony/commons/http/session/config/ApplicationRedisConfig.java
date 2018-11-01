package jason.app.symphony.commons.http.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Configuration
@ConfigurationProperties("app.redis")
public class ApplicationRedisConfig {
	private String host;

	private int port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Bean
	public LettuceConnectionFactory globalConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

}
