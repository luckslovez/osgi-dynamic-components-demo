package xyz.pinitos.demo.osgi;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=GET",
        "sling.servlet.paths=/bin/osgi/green/field"
})
public class RenderServletAnnotatedField extends SlingSafeMethodsServlet {

    @Reference(service = Renderer.class, cardinality = ReferenceCardinality.MULTIPLE, bind = "bindRenderer", unbind = "unbindRenderer", policy = ReferencePolicy.DYNAMIC, fieldOption = FieldOption.UPDATE)
    Set<Renderer> renderers;

    protected void bindRenderer(Renderer renderer, Map<String, Object> props) {
        renderers.add(renderer);
    }

    protected void unbindRenderer(Renderer renderer, Map<String, Object> props) {
        renderers.remove(renderer);
    }

    @Activate
    protected void activate() {
        if (renderers == null) {
            renderers = new HashSet<>();
        }
    }

    @Deactivate
    protected void deactivate() {
        renderers.clear();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Optional.of(request.getRequestPathInfo())
                .map(RequestPathInfo::getExtension)
                .map(this::getRendition)
                .ifPresent(rendition -> {
                    try {
                        response.getWriter().print(rendition);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    private String getRendition(String extension) {
        return renderers.stream()
                .filter(renderer -> renderer.contentType().contains(extension))
                .map(Renderer::render)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No service able to render " + extension));
    }
}
