package jason.app.symphony.tenant;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link TenantEndpoint}.
 */
public class TenantComponent extends DefaultComponent {
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new TenantEndpoint(uri, remaining,this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
