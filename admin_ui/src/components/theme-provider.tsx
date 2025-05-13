"use client"

import * as React from "react"
import { createContext, useContext, useEffect, useState } from "react"

type Theme = "dark" | "light" | "system"

type ThemeProviderProps = {
  children: React.ReactNode
  defaultTheme?: Theme
  storageKey?: string
  attribute?: string
  enableSystem?: boolean
  disableTransitionOnChange?: boolean
}

type ThemeProviderState = {
  theme: Theme
  setTheme: (theme: Theme) => void
}

const initialState: ThemeProviderState = {
  theme: "light",
  setTheme: () => null,
}

const ThemeProviderContext = createContext<ThemeProviderState>(initialState)

export function ThemeProvider({
  children,
  defaultTheme = "light",
  storageKey = "theme",
  attribute = "data-theme",
  enableSystem = false,
  disableTransitionOnChange = false,
  ...props
}: ThemeProviderProps) {
  const [theme, setTheme] = useState<Theme>(defaultTheme)

  // Initialize state from localStorage only on the client side
  useEffect(() => {
    const storedTheme = typeof window !== "undefined"
      ? (localStorage?.getItem(storageKey) as Theme)
      : null

    if (storedTheme) {
      setTheme(storedTheme)
    } else if (enableSystem) {
      setTheme("system")
    }
  }, [storageKey, enableSystem])

  // Handle system theme preference if enabled
  useEffect(() => {
    if (enableSystem && theme === "system" && typeof window !== "undefined") {
      const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
      const systemTheme = prefersDark ? "dark" : "light"

      const root = window.document.documentElement
      root.classList.remove("dark", "light")
      root.classList.add(systemTheme)

      if (attribute === "class") {
        root.classList.add(systemTheme)
      } else {
        root.setAttribute(attribute, systemTheme)
      }
    }
  }, [enableSystem, theme, attribute])

  useEffect(() => {
    if (typeof window === "undefined") return

    const root = window.document.documentElement

    root.classList.remove("dark", "light")

    if (theme === "system") {
      const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
      const systemTheme = prefersDark ? "dark" : "light"
      root.classList.add(systemTheme)

      if (attribute === "class") {
        root.classList.add(systemTheme)
      } else {
        root.setAttribute(attribute, systemTheme)
      }
    } else {
      root.classList.add(theme)

      if (attribute === "class") {
        root.classList.add(theme)
      } else {
        root.setAttribute(attribute, theme)
      }

      localStorage.setItem(storageKey, theme)
    }
  }, [theme, attribute, storageKey])

  const value = {
    theme,
    setTheme: (theme: Theme) => {
      setTheme(theme)
    },
  }

  return (
    <ThemeProviderContext.Provider {...props} value={value}>
      {children}
    </ThemeProviderContext.Provider>
  )
}

export const useTheme = () => {
  const context = useContext(ThemeProviderContext)

  if (context === undefined)
    throw new Error("useTheme must be used within a ThemeProvider")

  return context
}