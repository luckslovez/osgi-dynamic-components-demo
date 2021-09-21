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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=GET",
        "sling.servlet.paths=/bin/osgi/green/method"
})
public class RenderServletAnnotatedMethod extends SlingSafeMethodsServlet {

    Set<Renderer> renderers;

    @Reference(service = Renderer.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void bindRenderer(Renderer renderer) {
        renderers.add(renderer);
    }

    protected void unbindRenderer(Renderer renderer) {
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
