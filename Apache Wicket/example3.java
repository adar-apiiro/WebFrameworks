// HelloApiController.java
package com.example;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.StringResource;

public class HelloApiController extends WebPage {

    public HelloApiController(final PageParameters parameters) {
        super(parameters);
    }

    public static class RestResource extends StringResource {
        @Override
        protected String getString() {
            // Here you can generate your JSON response
            return "{\"message\":\"Hello, API!\"}";
        }
    }

    public static class HelloApiApplication extends WebApplication {

        @Override
        public Class<? extends WebPage> getHomePage() {
            return HelloApiController.class;
        }

        @Override
        public void init() {
            super.init();
            mountResource("/api/hello", new RestResource());
        }
    }

    public static void main(String[] args) {
        org.apache.wicket.protocol.http.WicketServlet wicketServlet = new org.apache.wicket.protocol.http.WicketServlet();
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
        org.eclipse.jetty.servlet.ServletContextHandler contextHandler = new org.eclipse.jetty.servlet.ServletContextHandler();
        contextHandler.addServlet(new org.eclipse.jetty.servlet.ServletHolder(wicketServlet), "/*");
        contextHandler.setInitParameter("applicationClassName", HelloApiApplication.class.getName());
        server.setHandler(contextHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
