package xyz.pinitos.demo.osgi;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class HtmlRenderer implements Renderer {
    @Override
    public String contentType() {
        return "text/html";
    }

    @Override
    public String render() {
        return "<h1>Well</h1><h2>that's</h2><h3>dummy</h3><h4>.</h4>";
    }

    @Activate
    public void activate() {
    }
}
