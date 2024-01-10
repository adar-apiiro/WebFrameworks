package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func main() {
	router := gin.Default()

	// Endpoint for getting a list of albums
	router.GET("/albums", getAlbums)

	// Endpoint for getting a specific album by ID
	router.GET("/albums/:id", getAlbumByID)

	// Endpoint for creating a new album
	router.POST("/albums", createAlbum)

	// Endpoint for updating an existing album by ID
	router.PUT("/albums/:id", updateAlbum)

	// Endpoint for deleting an album by ID
	router.DELETE("/albums/:id", deleteAlbum)

	router.Run("localhost:8080")
}

// Handler for getting a list of albums
func getAlbums(c *gin.Context) {
	// Your implementation to retrieve and return a list of albums
	c.JSON(http.StatusOK, gin.H{"message": "Get all albums"})
}

// Handler for getting a specific album by ID
func getAlbumByID(c *gin.Context) {
	// Extract the ID parameter from the request
	id := c.Param("id")

	// Your implementation to retrieve and return the album by ID
	c.JSON(http.StatusOK, gin.H{"message": "Get album with ID " + id})
}

// Handler for creating a new album
func createAlbum(c *gin.Context) {
	// Your implementation to create a new album
	c.JSON(http.StatusCreated, gin.H{"message": "Create a new album"})
}

// Handler for updating an existing album by ID
func updateAlbum(c *gin.Context) {
	// Extract the ID parameter from the request
	id := c.Param("id")

	// Your implementation to update the album by ID
	c.JSON(http.StatusOK, gin.H{"message": "Update album with ID " + id})
}

// Handler for deleting an album by ID
func deleteAlbum(c *gin.Context) {
	// Extract the ID parameter from the request
	id := c.Param("id")

	// Your implementation to delete the album by ID
	c.JSON(http.StatusOK, gin.H{"message": "Delete album with ID " + id})
}
