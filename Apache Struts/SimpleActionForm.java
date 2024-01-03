package examples.simple;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionForm;

public class SimpleActionForm extends ActionForm {

    private String name;
    private String email;

    // Getters and setters for 'name' and 'email'

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        // Validate 'name'
        if (name == null || name.trim().isEmpty()) {
            errors.add("name", new ActionMessage("error.name.required"));
        }

        // Validate 'email'
        if (email == null || email.trim().isEmpty()) {
            errors.add("email", new ActionMessage("error.email.required"));
        }

        return errors;
    }
}
