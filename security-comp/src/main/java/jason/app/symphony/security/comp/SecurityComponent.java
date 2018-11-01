package jason.app.symphony.security.comp;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link SecurityEndpoint}.
 */
public class SecurityComponent extends DefaultComponent{

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new SecurityEndpoint(uri,remaining, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
	
}
