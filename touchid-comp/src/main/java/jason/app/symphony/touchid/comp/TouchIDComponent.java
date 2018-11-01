package jason.app.symphony.touchid.comp;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link TouchIDEndpoint}.
 */
public class TouchIDComponent extends DefaultComponent {
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new TouchIDEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
