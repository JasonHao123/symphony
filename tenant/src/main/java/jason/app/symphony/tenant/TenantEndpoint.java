package jason.app.symphony.tenant;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a Tenant endpoint.
 */
@UriEndpoint(firstVersion = "0.0.1-SNAPSHOT", scheme = "tenant", title = "Tenant", syntax="tenant:name", 
             consumerClass = TenantConsumer.class, label = "custom")
public class TenantEndpoint extends DefaultEndpoint {
    /**
     * name
     */
    @UriPath @Metadata(required = "true")
    private String name;
    /**
     * option
     */
    @UriParam(defaultValue = "10")
    private int option = 10;
	private String remaining;

    public TenantEndpoint() {
    }

    public TenantEndpoint(String uri, String remaining, TenantComponent component) {
        super(uri, component);
        this.remaining = remaining;
    }

    public TenantEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new TenantProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new TenantConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOption() {
		return option;
	}

	public void setOption(int option) {
		this.option = option;
	}

    
}
