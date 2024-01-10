package com.example;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class ApiPage extends WebPage {

    public ApiPage(final PageParameters parameters) {
        StringValue apiParam = parameters.get("apiParam");
        if (!apiParam.isEmpty()) {
            String responseData = "Hello, API! Received parameter: " + apiParam.toString();
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new JsonResponseHandler(responseData));
        } else {
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new JsonResponseHandler("Hello, API!"));
        }
    }
}
