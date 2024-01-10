package com.example;

import grails.web.Action;
import grails.web.Controller;
import groovy.transform.CompileStatic;

@Controller
@CompileStatic
class MyApiController {

    // Import specific classes for response formats
    static final grails.web.mapping.responses.Responders.Json jsonResponder = grails.web.mapping.responses.Responders.Json;
    static final grails.web.mapping.responses.Responders.Xml xmlResponder = grails.web.mapping.responses.Responders.Xml;

    // List action (GET /myApi)
    @Action(detail = "List items")
    def list() {
        // Your API logic for listing items goes here
        return "Listing items from My API!";
    }

    // Show action (GET /myApi/{id})
    @Action(detail = "Show an item")
    def show(Long id) {
        // Your API logic for showing an item goes here
        return "Showing item with ID: ${id} from My API!";
    }

    // Save action (POST /myApi)
    @Action(detail = "Create an item")
    def save() {
        // Your API logic for saving an item goes here
        return "Creating an item in My API!";
    }

    // Update action (PUT /myApi/{id})
    @Action(detail = "Update an item")
    def update(Long id) {
        // Your API logic for updating an item goes here
        return "Updating item with ID: ${id} in My API!";
    }

    // Delete action (DELETE /myApi/{id})
    @Action(detail = "Delete an item")
    def delete(Long id) {
        // Your API logic for deleting an item goes here
        return "Deleting item with ID: ${id} from My API!";
    }

    // External method for specific framework logic
    def customFrameworkMethod() {
        // Your framework-specific logic goes here
        return "Executing custom method from My API!";
    }

    // Call CRUD methods and external method
    def callApiMethods() {
        def listResult = list()
        def showResult = show(1) // Assuming ID 1 for demonstration
        def saveResult = save()
        def updateResult = update(2) // Assuming ID 2 for demonstration
        def deleteResult = delete(3) // Assuming ID 3 for demonstration
        def customMethodResult = customFrameworkMethod()

        // Print results (you can handle these results as needed)
        println("List Result: $listResult")
        println("Show Result: $showResult")
        println("Save Result: $saveResult")
        println("Update Result: $updateResult")
        println("Delete Result: $deleteResult")
        println("Custom Method Result: $customMethodResult")
    }
}
