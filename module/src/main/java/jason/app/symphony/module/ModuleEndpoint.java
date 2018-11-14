package jason.app.symphony.module;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a Module endpoint.
 */
@UriEndpoint(firstVersion = "0.0.1-SNAPSHOT", scheme = "module", title = "Module", syntax="module:name", 
             consumerClass = ModuleConsumer.class, label = "custom")
public class ModuleEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String name;
    @UriParam(defaultValue = "10")
    private int option = 10;
	private String remaining;
	
    public ModuleEndpoint() {
    }

    public ModuleEndpoint(String uri, String remaining,ModuleComponent component) {
        super(uri, component);
        this.remaining = remaining;
    }

    public ModuleEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new ModuleProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new ModuleConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return option;
    }
    
	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}
}