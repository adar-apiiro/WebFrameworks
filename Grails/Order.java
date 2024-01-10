import grails.rest.RestfulController;
import grails.web.Action;
import grails.web.mapping.responses.Responders;

public class CustomBookController extends RestfulController<Book> {

    // Import specific classes for response formats
    static final Responders.Json jsonResponder = Responders.Json;
    static final Responders.Xml xmlResponder = Responders.Xml;

    public CustomBookController() {
        super(Book.class);
    }

    @Override
    @Action(detail = "Custom query for Book resource")
    protected Book queryForResource(Serializable id) {
        return Book.where {
            it.id == id && author.id == params.authorId
        }.find();
    }
}
