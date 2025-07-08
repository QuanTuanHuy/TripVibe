package controller

import (
	"memo_service/src/core/common"
	"memo_service/src/core/service"
	"memo_service/src/ui/helper"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type MarkdownController struct {
	markdownService service.IMarkdownService
	logger          *zap.Logger
}

func (m *MarkdownController) ConvertToHTML(c *gin.Context) {
	var request struct {
		Markdown string `json:"markdown" binding:"required"`
		Format   string `json:"format,omitempty"` // "clean", "minified", "pretty"
		Style    string `json:"style,omitempty"`  // "github", "monokai", "dracula", etc.
	}
	if err := c.ShouldBindJSON(&request); err != nil {
		m.logger.Error("Failed to bind markdown string", zap.Error(err))
		helper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	var html string
	var err error

	// Default format is "clean"
	if request.Format == "" {
		request.Format = "clean"
	}

	// If style is specified, use style-specific conversion
	if request.Style != "" {
		html, err = m.markdownService.ConvertToHTMLWithStyle(c, request.Markdown, request.Style)
	} else {
		// Use format-specific conversion
		switch request.Format {
		case "minified":
			html, err = m.markdownService.ConvertToMinifiedHTML(c, request.Markdown)
		case "pretty":
			html, err = m.markdownService.ConvertToPrettyHTML(c, request.Markdown)
		default: // "clean"
			html, err = m.markdownService.ConvertToHTML(c, request.Markdown)
		}
	}

	if err != nil {
		m.logger.Error("Failed to convert markdown to HTML", zap.Error(err))
		helper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	response := map[string]interface{}{
		"html":   html,
		"format": request.Format,
	}

	if request.Style != "" {
		response["style"] = request.Style
	}

	helper.SuccessfulHandle(c, response)
}

func (m *MarkdownController) GetSupportedLanguages(c *gin.Context) {
	languages := m.markdownService.GetSupportedLanguages()

	response := map[string]interface{}{
		"languages": languages,
		"count":     len(languages),
	}

	helper.SuccessfulHandle(c, response)
}

func (m *MarkdownController) GetAvailableStyles(c *gin.Context) {
	styles := m.markdownService.GetAvailableStyles()

	response := map[string]interface{}{
		"styles": styles,
		"count":  len(styles),
	}

	helper.SuccessfulHandle(c, response)
}

func NewMarkdownController(markdownService service.IMarkdownService, logger *zap.Logger) *MarkdownController {
	return &MarkdownController{
		markdownService: markdownService,
		logger:          logger,
	}
}
