import grails.rest.RestfulController
import grails.web.Action
import grails.web.mapping.responses.Responders
import groovy.transform.CompileStatic

@CompileStatic
class BookController extends RestfulController<Book> {

    // Import specific classes for response formats
    static responseFormats = [Responders.Json, Responders.Xml]

    BookController() {
        super(Book)
    }

    @Override
    @Action(detail = "Custom query for Book resource")
    protected Book queryForResource(Serializable id) {
        Book.where {
            id == id && author.id == params.authorId
        }.find()
    }
}
