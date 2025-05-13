"use client"

import * as React from "react"
import { ThemeProvider as NextThemesProvider } from "next-themes"

export function ThemeProvider({ children }: { children: React.ReactNode }) {
    const [mounted, setMounted] = React.useState(false)

    // After hydration, we can show the ThemeProvider content
    React.useEffect(() => {
        setMounted(true)
    }, [])

    if (!mounted) {
        // You can render a skeleton or loader here
        return <>{children}</>
    }

    return (
        <NextThemesProvider
            attribute="class"
            defaultTheme="system"
            enableSystem
            disableTransitionOnChange
            forcedTheme={undefined}
        >
            {children}
        </NextThemesProvider>
    )
}
