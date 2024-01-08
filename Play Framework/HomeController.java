package controllers;

import play.mvc.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.Request;

import views.html.home;
import views.html.user;

import java.text.DateFormat;
import java.util.Date;

public class HomeController extends Controller {

    /**
     * Simply selects the home view to render by returning its name.
     */
    public Result home() {
        System.out.println("Home Page Requested");
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, request().locale());

        String formattedDate = dateFormat.format(date);

        return ok(home.render(formattedDate));
    }

    public Result user() {
        System.out.println("User Page Requested");
        User user = new User(); // Assuming you have a User model class
        user.setUserName(request().body().asFormUrlEncoded().get("userName")[0]);

        return ok(user.render(user));
    }

    public Result redirectToHome() {
        flash("info", "Redirected to Home Page"); // Flash message example
        return redirect(routes.HomeController.home());
    }
}
