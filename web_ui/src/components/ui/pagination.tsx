"use client"

import * as React from "react"
import Link from "next/link"
import { ChevronLeft, ChevronRight, MoreHorizontal } from "lucide-react"
import { cn } from "@/lib/utils"
import { buttonVariants } from "@/components/ui/button"

interface PaginationProps extends React.ComponentProps<"nav"> {
    total: number
    perPage: number
    currentPage: number
    onPageChange: (page: number) => void
}

export function Pagination({
    total,
    perPage,
    currentPage,
    onPageChange,
    className,
    ...props
}: PaginationProps) {
    const pageCount = Math.ceil(total / perPage);

    // Don't render pagination if only one page
    if (pageCount <= 1) return null;

    return (
        <nav
            role="navigation"
            aria-label="pagination"
            className={cn("mx-auto flex w-full justify-center", className)}
            {...props}
        >
            <ul className="flex flex-row items-center gap-1">
                <PaginationItem>
                    <PaginationPrevious
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage > 1) {
                                onPageChange(currentPage - 1);
                            }
                        }}
                        aria-disabled={currentPage === 1}
                        className={cn(currentPage === 1 && "pointer-events-none opacity-50")}
                    />
                </PaginationItem>

                {getPagesToShow(currentPage, pageCount).map((page, i) => (
                    page === "ellipsis" ? (
                        <PaginationItem key={`ellipsis-${i}`}>
                            <PaginationEllipsis />
                        </PaginationItem>
                    ) : (
                        <PaginationItem key={page as number}>
                            <PaginationLink
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    onPageChange(page as number);
                                }}
                                isActive={page === currentPage}
                            >
                                {page}
                            </PaginationLink>
                        </PaginationItem>
                    )
                ))}

                <PaginationItem>
                    <PaginationNext
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage < pageCount) {
                                onPageChange(currentPage + 1);
                            }
                        }}
                        aria-disabled={currentPage === pageCount}
                        className={cn(currentPage === pageCount && "pointer-events-none opacity-50")}
                    />
                </PaginationItem>
            </ul>
        </nav>
    )
}

function getPagesToShow(currentPage: number, pageCount: number) {
    // Always show first and last page
    // Show ellipsis if needed
    // Show 2 pages around current page
    const pages: (number | "ellipsis")[] = [];

    // Always add page 1
    pages.push(1);

    if (currentPage > 3) {
        pages.push("ellipsis");
    }

    // Pages around current
    for (let i = Math.max(2, currentPage - 1); i <= Math.min(pageCount - 1, currentPage + 1); i++) {
        pages.push(i);
    }

    if (currentPage < pageCount - 2) {
        pages.push("ellipsis");
    }

    // Always add last page if more than 1 page
    if (pageCount > 1) {
        pages.push(pageCount);
    }

    return pages;
}

const PaginationItem = React.forwardRef<
    HTMLLIElement,
    React.ComponentProps<"li">
>(({ className, ...props }, ref) => (
    <li ref={ref} className={cn("", className)} {...props} />
))
PaginationItem.displayName = "PaginationItem"

const PaginationLink = ({
    className,
    isActive,
    ...props
}: React.ComponentProps<typeof Link> & {
    isActive?: boolean
}) => (
    <Link
        aria-current={isActive ? "page" : undefined}
        className={cn(
            buttonVariants({
                variant: isActive ? "outline" : "ghost",
                size: "icon",
            }),
            isActive && "bg-primary/10 hover:bg-primary/15",
            className
        )}
        {...props}
    />
)
PaginationLink.displayName = "PaginationLink"

const PaginationPrevious = ({
    className,
    ...props
}: React.ComponentProps<typeof PaginationLink>) => (
    <PaginationLink
        aria-label="Go to previous page"
        className={cn("gap-1 pl-2.5", className)}
        {...props}
    >
        <ChevronLeft className="h-4 w-4" />
        <span className="sr-only">Previous</span>
    </PaginationLink>
)
PaginationPrevious.displayName = "PaginationPrevious"

const PaginationNext = ({
    className,
    ...props
}: React.ComponentProps<typeof PaginationLink>) => (
    <PaginationLink
        aria-label="Go to next page"
        className={cn("gap-1 pr-2.5", className)}
        {...props}
    >
        <span className="sr-only">Next</span>
        <ChevronRight className="h-4 w-4" />
    </PaginationLink>
)
PaginationNext.displayName = "PaginationNext"

const PaginationEllipsis = ({
    className,
    ...props
}: React.ComponentProps<"span">) => (
    <span
        aria-hidden
        className={cn("flex h-9 w-9 items-center justify-center", className)}
        {...props}
    >
        <MoreHorizontal className="h-4 w-4" />
        <span className="sr-only">More pages</span>
    </span>
)
PaginationEllipsis.displayName = "PaginationEllipsis"
