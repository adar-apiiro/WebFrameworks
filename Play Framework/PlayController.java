package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

  public Result index() {
    return ok("It works!");
  }
}
