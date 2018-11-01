package org.springframework.boot.autoconfigure.session;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.CustomRedisHttpSessionConfiguration;

@Configuration
public class CustomRedisSessionConfiguration {

	@Bean
	public SessionProperties sessionProperties(ObjectProvider<ServerProperties> serverProperties) {
		return new SessionProperties(serverProperties);
	}
	
	@Configuration
	public static class CustomSpringBootRedisHttpSessionConfiguration
			extends CustomRedisHttpSessionConfiguration {

		private SessionProperties sessionProperties;

		@Autowired
		public void customize(SessionProperties sessionProperties) {
			this.sessionProperties = sessionProperties;
			Integer timeout = this.sessionProperties.getTimeout();
			if (timeout != null) {
				setMaxInactiveIntervalInSeconds(timeout);
			}
			SessionProperties.Redis redis = this.sessionProperties.getRedis();
			setRedisNamespace(redis.getNamespace());
			setRedisFlushMode(redis.getFlushMode());
		}

	}

}
