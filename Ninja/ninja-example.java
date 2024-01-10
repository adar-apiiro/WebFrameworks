import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaMode;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController::index);
    }
  
}
