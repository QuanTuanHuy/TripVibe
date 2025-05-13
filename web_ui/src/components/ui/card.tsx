"use client"

import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const cardVariants = cva(
    "rounded-lg border bg-card text-card-foreground shadow-sm transition-all duration-200",
    {
        variants: {
            variant: {
                default: "",
                outline: "border border-primary/20 bg-background shadow-none hover:border-primary/30 hover:shadow-sm",
                elevated: "shadow-md dark:shadow-primary/5 hover:shadow-lg dark:hover:shadow-primary/10",
                transparent: "border-none bg-transparent shadow-none",
            },
            size: {
                default: "p-6",
                sm: "p-4",
                lg: "p-8",
                xl: "p-10",
            }
        },
        defaultVariants: {
            variant: "default",
            size: "default"
        }
    }
)

export interface CardProps
    extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof cardVariants> { }

function Card({ className, variant, size, ...props }: CardProps) {
    return (
        <div className={cn(cardVariants({ variant, size }), className)} {...props} />
    )
}

function CardHeader({
    className,
    ...props
}: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div
            className={cn("flex flex-col space-y-1.5 pb-4", className)}
            {...props}
        />
    )
}

function CardTitle({
    className,
    ...props
}: React.HTMLAttributes<HTMLHeadingElement>) {
    return (
        <h3
            className={cn(
                "text-2xl font-semibold leading-none tracking-tight",
                className
            )}
            {...props}
        />
    )
}

function CardDescription({
    className,
    ...props
}: React.HTMLAttributes<HTMLParagraphElement>) {
    return (
        <p
            className={cn("text-sm text-muted-foreground", className)}
            {...props}
        />
    )
}

function CardContent({
    className,
    ...props
}: React.HTMLAttributes<HTMLDivElement>) {
    return <div className={cn("", className)} {...props} />
}

function CardFooter({
    className,
    ...props
}: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div
            className={cn("flex items-center pt-4", className)}
            {...props}
        />
    )
}

export { Card, CardHeader, CardFooter, CardTitle, CardDescription, CardContent }
