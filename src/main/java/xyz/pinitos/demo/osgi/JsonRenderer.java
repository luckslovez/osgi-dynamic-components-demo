package xyz.pinitos.demo.osgi;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class JsonRenderer implements Renderer {
    @Override
    public String contentType() {
        return "text/json";
    }

    @Override
    public String render() {
        return "{ well: that's dummy }";
    }

    @Activate
    public void activate() {
    }
}
