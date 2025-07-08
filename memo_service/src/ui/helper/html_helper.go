package helper

import (
	"regexp"
	"strings"
)

// CleanHTML removes excessive newlines and whitespace from HTML
func CleanHTML(html string) string {
	// Remove excessive newlines
	html = regexp.MustCompile(`\n{3,}`).ReplaceAllString(html, "\n\n")

	// Remove newlines between tags
	html = regexp.MustCompile(`>\s*\n\s*<`).ReplaceAllString(html, "><")

	// Remove trailing newlines
	html = strings.TrimSpace(html)

	// Remove leading/trailing whitespace from each line
	lines := strings.Split(html, "\n")
	cleanLines := make([]string, 0, len(lines))

	for _, line := range lines {
		trimmed := strings.TrimSpace(line)
		if trimmed != "" {
			cleanLines = append(cleanLines, trimmed)
		}
	}

	return strings.Join(cleanLines, "\n")
}

// MinifyHTML removes all unnecessary whitespace and newlines for compact output
func MinifyHTML(html string) string {
	// Remove all newlines and excessive whitespace
	html = regexp.MustCompile(`\s+`).ReplaceAllString(html, " ")

	// Remove space between tags
	html = regexp.MustCompile(`>\s*<`).ReplaceAllString(html, "><")

	// Remove leading/trailing whitespace
	html = strings.TrimSpace(html)

	return html
}

// PrettyHTML formats HTML with proper indentation (for debugging)
func PrettyHTML(html string) string {
	// This is a simple formatter - for production use a proper HTML formatter
	html = strings.TrimSpace(html)

	// Add newlines after closing tags
	html = regexp.MustCompile(`(<\/[^>]+>)([^<]*)`).ReplaceAllString(html, "$1\n$2")

	// Add newlines before opening tags
	html = regexp.MustCompile(`([^>])(<[^\/][^>]*>)`).ReplaceAllString(html, "$1\n$2")

	// Clean up excessive newlines
	html = regexp.MustCompile(`\n{3,}`).ReplaceAllString(html, "\n\n")

	return strings.TrimSpace(html)
}
