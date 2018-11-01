package jason.app.symphony.audit.comp;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link AuditEndpoint}.
 */
public class AuditComponent extends DefaultComponent {
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new AuditEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
