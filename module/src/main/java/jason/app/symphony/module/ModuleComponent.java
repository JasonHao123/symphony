package jason.app.symphony.module;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link ModuleEndpoint}.
 */
public class ModuleComponent extends DefaultComponent {
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new ModuleEndpoint(uri, remaining,this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
