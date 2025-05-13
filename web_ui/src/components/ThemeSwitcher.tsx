"use client"

import * as React from "react"
import { Moon, Sun, Laptop } from "lucide-react"
import { useTheme } from "next-themes"

export function ThemeSwitcher() {
    const { theme, setTheme, resolvedTheme } = useTheme()
    const [mounted, setMounted] = React.useState(false)

    // After mounting, we can safely show the UI to avoid hydration mismatch
    React.useEffect(() => {
        setMounted(true)
    }, [])

    // Thêm logging để debug
    React.useEffect(() => {
        if (mounted) {
            console.log("Current theme:", theme)
            console.log("Resolved theme:", resolvedTheme)
        }
    }, [theme, resolvedTheme, mounted])

    if (!mounted) {
        // Return a placeholder with the same dimensions to avoid layout shift
        return <div className="flex items-center space-x-1 bg-transparent rounded-lg p-1 h-[34px] w-[102px]" />
    }

    return (
        <div className="flex items-center space-x-1 bg-white/90 dark:bg-gray-800/90 rounded-lg p-1 shadow-sm backdrop-blur-sm transition-colors duration-200">
            <button
                onClick={() => setTheme("light")}
                className={`p-1.5 rounded-md transition-all duration-200 ${theme === "light" || (theme === "system" && resolvedTheme === "light")
                    ? "bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-200 shadow-sm"
                    : "text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                    }`}
                title="Chế độ sáng"
                aria-label="Chế độ sáng"
            >
                <Sun size={16} className={(theme === "light" || (theme === "system" && resolvedTheme === "light")) ? "animate-pulse" : ""} />
            </button>
            <button
                onClick={() => setTheme("dark")}
                className={`p-1.5 rounded-md transition-all duration-200 ${theme === "dark" || (theme === "system" && resolvedTheme === "dark")
                    ? "bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-200 shadow-sm"
                    : "text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                    }`}
                title="Chế độ tối"
                aria-label="Chế độ tối"
            >
                <Moon size={16} className={(theme === "dark" || (theme === "system" && resolvedTheme === "dark")) ? "animate-pulse" : ""} />
            </button>
            <button
                onClick={() => setTheme("system")}
                className={`p-1.5 rounded-md transition-all duration-200 ${theme === "system"
                    ? "bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-200 shadow-sm"
                    : "text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                    }`}
                title="Theo hệ thống"
                aria-label="Theo hệ thống"
            >
                <Laptop size={16} className={theme === "system" ? "animate-pulse" : ""} />
            </button>
        </div>
    )
}
