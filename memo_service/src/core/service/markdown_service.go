package service

import (
	"bytes"
	"context"
	"memo_service/src/config"
	"memo_service/src/ui/helper"

	"github.com/yuin/goldmark"
	"go.uber.org/zap"
)

type IMarkdownService interface {
	ConvertToHTML(ctx context.Context, markdownStr string) (string, error)
	ConvertToMinifiedHTML(ctx context.Context, markdownStr string) (string, error)
	ConvertToPrettyHTML(ctx context.Context, markdownStr string) (string, error)
	ConvertToHTMLWithStyle(ctx context.Context, markdownStr string, style string) (string, error)
	GetSupportedLanguages() []string
	GetAvailableStyles() []string
}

type MarkdownService struct {
	markdown goldmark.Markdown
	logger   *zap.Logger
}

func (m *MarkdownService) ConvertToHTML(ctx context.Context, markdownStr string) (string, error) {
	var buf bytes.Buffer
	if err := m.markdown.Convert([]byte(markdownStr), &buf); err != nil {
		m.logger.Error("Failed to convert markdown to HTML", zap.Error(err))
		return "", err
	}

	// Clean the HTML output to remove excessive newlines
	cleanHTML := helper.CleanHTML(buf.String())
	return cleanHTML, nil
}

func (m *MarkdownService) ConvertToMinifiedHTML(ctx context.Context, markdownStr string) (string, error) {
	var buf bytes.Buffer
	if err := m.markdown.Convert([]byte(markdownStr), &buf); err != nil {
		m.logger.Error("Failed to convert markdown to HTML", zap.Error(err))
		return "", err
	}

	// Minify the HTML output - remove all unnecessary whitespace
	minifiedHTML := helper.MinifyHTML(buf.String())
	return minifiedHTML, nil
}

func (m *MarkdownService) ConvertToPrettyHTML(ctx context.Context, markdownStr string) (string, error) {
	var buf bytes.Buffer
	if err := m.markdown.Convert([]byte(markdownStr), &buf); err != nil {
		m.logger.Error("Failed to convert markdown to HTML", zap.Error(err))
		return "", err
	}

	// Pretty format the HTML output - for debugging/development
	prettyHTML := helper.PrettyHTML(buf.String())
	return prettyHTML, nil
}

func (m *MarkdownService) ConvertToHTMLWithStyle(ctx context.Context, markdownStr string, style string) (string, error) {
	md := config.NewMarkdownWithStyle(style)
	var buf bytes.Buffer
	if err := md.Convert([]byte(markdownStr), &buf); err != nil {
		m.logger.Error("Failed to convert markdown to HTML with style", zap.String("style", style), zap.Error(err))
		return "", err
	}

	// Clean the HTML output
	cleanHTML := helper.CleanHTML(buf.String())
	return cleanHTML, nil
}

func (m *MarkdownService) GetSupportedLanguages() []string {
	return config.GetSupportedLanguages()
}

func (m *MarkdownService) GetAvailableStyles() []string {
	return config.GetAvailableStyles()
}

func NewMarkdownService(markdown goldmark.Markdown, logger *zap.Logger) IMarkdownService {
	return &MarkdownService{
		markdown: markdown,
		logger:   logger,
	}
}
