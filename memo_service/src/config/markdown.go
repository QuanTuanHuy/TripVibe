package config

import (
	"github.com/alecthomas/chroma/v2/formatters/html"
	"github.com/yuin/goldmark"
	highlighting "github.com/yuin/goldmark-highlighting/v2"
	"github.com/yuin/goldmark/extension"
	"github.com/yuin/goldmark/parser"
	htmlrenderer "github.com/yuin/goldmark/renderer/html"
)

func NewMarkdown() goldmark.Markdown {
	return goldmark.New(
		goldmark.WithExtensions(
			extension.GFM,
			extension.Table,
			extension.Strikethrough,
			extension.Footnote,
			extension.TaskList,
			extension.DefinitionList,
			extension.Linkify,
			highlighting.NewHighlighting(
				highlighting.WithStyle("github"),
				highlighting.WithFormatOptions(
					html.WithLineNumbers(true),
					html.WithClasses(true),
				),
			),
		),
		goldmark.WithParserOptions(
			parser.WithAutoHeadingID(),
			parser.WithAttribute(),
		),
		goldmark.WithRendererOptions(
			htmlrenderer.WithUnsafe(),
			htmlrenderer.WithXHTML(),
		),
	)
}

// NewMarkdownWithStyle tạo markdown với custom style
func NewMarkdownWithStyle(styleName string) goldmark.Markdown {
	return goldmark.New(
		goldmark.WithExtensions(
			extension.GFM,
			extension.Table,
			extension.Strikethrough,
			extension.Footnote,
			extension.TaskList,
			extension.DefinitionList,
			extension.Linkify,
			highlighting.NewHighlighting(
				highlighting.WithStyle(styleName),
				highlighting.WithFormatOptions(
					html.WithLineNumbers(true),
					html.WithClasses(true),
				),
			),
		),
		goldmark.WithParserOptions(
			parser.WithAutoHeadingID(),
			parser.WithAttribute(),
		),
		goldmark.WithRendererOptions(
			htmlrenderer.WithUnsafe(),
			htmlrenderer.WithXHTML(),
		),
	)
}

// GetAvailableStyles trả về danh sách styles có sẵn
func GetAvailableStyles() []string {
	return []string{
		"github",
		"monokai",
		"dracula",
		"vs",
		"xcode",
		"atom-one-dark",
		"atom-one-light",
		"solarized-dark",
		"solarized-light",
		"nord",
		"gruvbox",
		"material",
	}
}

// GetSupportedLanguages trả về danh sách ngôn ngữ được hỗ trợ syntax highlighting
func GetSupportedLanguages() []string {
	return []string{
		// Popular languages
		"go", "golang",
		"java",
		"javascript", "js",
		"typescript", "ts",
		"python", "py",
		"c", "cpp", "c++",
		"csharp", "cs",
		"php",
		"ruby", "rb",
		"rust", "rs",
		"kotlin", "kt",
		"swift",
		"scala",
		"dart",
		"r",
		"julia",
		"haskell", "hs",
		"elixir", "ex",
		"erlang", "erl",
		"clojure", "clj",
		"fsharp", "fs",
		"ocaml", "ml",
		"lua",
		"perl", "pl",
		"bash", "shell", "sh",
		"powershell", "ps1",
		"sql",
		"graphql", "gql",
		"yaml", "yml",
		"json",
		"xml",
		"html",
		"css",
		"scss", "sass",
		"less",
		"makefile", "make",
		"dockerfile", "docker",
		"nginx",
		"apache",
		"toml",
		"ini",
		"properties",
		"terraform", "tf",
		"hcl",
		"protobuf", "proto",
		"thrift",
		"avro",
		"latex", "tex",
		"markdown", "md",
		"diff",
		"patch",
		"log",
		"plaintext", "text",
	}
}

// NewMarkdownWithAdvancedHighlighting tạo markdown với advanced syntax highlighting
func NewMarkdownWithAdvancedHighlighting(styleName string, withLineNumbers bool, withClasses bool) goldmark.Markdown {
	formatOptions := make([]html.Option, 0)

	if withLineNumbers {
		formatOptions = append(formatOptions, html.WithLineNumbers(true))
	}

	if withClasses {
		formatOptions = append(formatOptions, html.WithClasses(true))
	} else {
		formatOptions = append(formatOptions, html.WithClasses(false))
	}

	return goldmark.New(
		goldmark.WithExtensions(
			extension.GFM,
			extension.Table,
			extension.Strikethrough,
			extension.Footnote,
			extension.TaskList,
			extension.DefinitionList,
			extension.Linkify,
			highlighting.NewHighlighting(
				highlighting.WithStyle(styleName),
				highlighting.WithFormatOptions(formatOptions...),
			),
		),
		goldmark.WithParserOptions(
			parser.WithAutoHeadingID(),
			parser.WithAttribute(),
		),
		goldmark.WithRendererOptions(
			htmlrenderer.WithUnsafe(),
			htmlrenderer.WithXHTML(),
		),
	)
}

// NewMarkdownSafe tạo markdown an toàn (không cho phép raw HTML)
func NewMarkdownSafe() goldmark.Markdown {
	return goldmark.New(
		goldmark.WithExtensions(
			extension.GFM,
			extension.Table,
			extension.Strikethrough,
			extension.Footnote,
			extension.TaskList,
			extension.DefinitionList,
			extension.Linkify,
			highlighting.NewHighlighting(
				highlighting.WithStyle("github"),
				highlighting.WithFormatOptions(
					html.WithLineNumbers(true),
					html.WithClasses(true),
				),
			),
		),
		goldmark.WithParserOptions(
			parser.WithAutoHeadingID(),
			parser.WithAttribute(),
		),
		goldmark.WithRendererOptions(
			htmlrenderer.WithXHTML(),
			// Không có WithUnsafe() để bảo mật
		),
	)
}
