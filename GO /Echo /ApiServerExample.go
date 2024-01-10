// main.go
package main

import (
	"encoding/json"
	"errors"
	"net/http"
	"net/http/httptest"
	"strconv"
	"strings"
	"testing"

	"github.com/labstack/echo/v4"
	"github.com/stretchr/testify/assert"
	"gorm.io/gorm"
)

// Model
type User struct {
	ID         uint   `gorm:"primaryKey" json:"id"`
	FirebaseUID string `json:"firebase_uid"`
	Name       string `json:"name"`
}

type Post struct {
	ID     uint   `gorm:"primaryKey" json:"id"`
	UserID uint   `json:"user_id"`
	User   User   `json:"user"`
	Title  string `json:"title"`
	Body   string `json:"body"`
}

// Middleware
func AuthMiddleware() echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			// Your authentication logic here
			user := &User{} // Replace this with actual authentication logic
			c.Set("user", user)
			return next(c)
		}
	}
}

// Database
type UserStore struct {
	db *gorm.DB
}

func (us *UserStore) AllPosts() ([]Post, error) {
	var p []Post
	err := us.db.Preload("User").Find(&p).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return p, nil
		}
		return nil, err
	}
	return p, nil
}

func (us *UserStore) UpdatePost(post *Post) error {
	return us.db.Model(post).Updates(post).Error
}

// Handler
type Handler struct {
	userStore *UserStore
}

type postsResponse struct {
	Posts []Post `json:"posts"`
}

func (h *Handler) getPosts(c echo.Context) error {
	posts, err := h.userStore.AllPosts()
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, postsResponse{Posts: posts})
}

func (h *Handler) updatePost(c echo.Context) error {
	// Your update logic here
	return c.JSON(http.StatusOK, "Post updated successfully")
}

// Test
func TestGetPosts(t *testing.T) {
	setup()
	req := httptest.NewRequest(echo.GET, "/api/posts", nil)
	rec := httptest.NewRecorder()

	c := e.NewContext(req, rec)
	assert.NoError(t, h.getPosts(c))

	if assert.Equal(t, http.StatusOK, rec.Code) {
		var res postsResponse
		err := json.Unmarshal(rec.Body.Bytes(), &res)
		assert.NoError(t, err)
		assert.Equal(t, 2, len(res.Posts))
	}
}

// Main
func main() {
	e := echo.New()

	userStore := &UserStore{db: /* initialize your GORM database instance */}
	handler := &Handler{userStore: userStore}

	// Middleware
	e.Use(AuthMiddleware())

	// Routes
	api := e.Group("/api")
	api.GET("/posts", handler.getPosts)
	api.PATCH("/posts/:id", handler.updatePost)

	// Start server
	e.Start(":8080")
}

// Other functions and structs (e.g., setup, AuthMiddleware) are assumed to be defined elsewhere in your code.
