package examples.simple;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SimpleAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SimpleActionForm simpleForm = (SimpleActionForm) form;

        // Business logic or processing based on form data...
        String message = "Hello, " + simpleForm.getName() + "! Your email is: " + simpleForm.getEmail();
        simpleForm.setMessage(message);

        return mapping.findForward("success");
    }
}
