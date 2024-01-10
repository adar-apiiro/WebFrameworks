package com.example;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MyApiApplication extends WebApplication {

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public void init() {
        super.init();
        mountPage("/api/hello", HelloApiPage.class);
    }

    public static class HomePage extends WebPage {
        public HomePage(final PageParameters parameters) {
            super(parameters);
        }
    }

    public static class HelloApiPage extends WebPage {
        public HelloApiPage(final PageParameters parameters) {
            String apiParam = parameters.get("apiParam").toString();
            getResponse().write("Hello, API! Received parameter: " + apiParam);
        }
    }

    @Override
    public Session newSession(org.apache.wicket.Request request, org.apache.wicket.Response response) {
        return new WebSession(request);
    }

    public static void main(String[] args) {
        org.apache.wicket.protocol.http.WicketServlet wicketServlet = new org.apache.wicket.protocol.http.WicketServlet();
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
        org.eclipse.jetty.servlet.ServletContextHandler contextHandler = new org.eclipse.jetty.servlet.ServletContextHandler();
        contextHandler.addServlet(new org.eclipse.jetty.servlet.ServletHolder(wicketServlet), "/*");
        contextHandler.setInitParameter("applicationClassName", MyApiApplication.class.getName());
        server.setHandler(contextHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
