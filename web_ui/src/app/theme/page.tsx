"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ThemeSwitcher } from "@/components/ThemeSwitcher"

export default function ThemeDemoPage() {
    const [activeTab, setActiveTab] = useState<string>("components")

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-bold mb-2">Theme System</h1>
                    <p className="text-muted-foreground">
                        A showcase of TripVibe&apos;s theming capabilities with light, dark, and system themes.
                    </p>
                </div>
                <ThemeSwitcher />
            </div>

            <div className="flex border-b mb-6">
                <button
                    onClick={() => setActiveTab("components")}
                    className={`px-4 py-2 ${activeTab === "components"
                        ? "border-b-2 border-primary font-medium"
                        : "text-muted-foreground hover:text-foreground"
                        }`}
                >
                    Components
                </button>
                <button
                    onClick={() => setActiveTab("colors")}
                    className={`px-4 py-2 ${activeTab === "colors"
                        ? "border-b-2 border-primary font-medium"
                        : "text-muted-foreground hover:text-foreground"
                        }`}
                >
                    Colors
                </button>
                <button
                    onClick={() => setActiveTab("typography")}
                    className={`px-4 py-2 ${activeTab === "typography"
                        ? "border-b-2 border-primary font-medium"
                        : "text-muted-foreground hover:text-foreground"
                        }`}
                >
                    Typography
                </button>
            </div>

            {activeTab === "components" && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {/* Cards */}
                    <div className="space-y-6">
                        <h2 className="text-2xl font-semibold mb-4">Cards</h2>

                        <Card>
                            <CardHeader>
                                <CardTitle>Default Card</CardTitle>
                                <CardDescription>This is a default card style</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <p>Cards automatically adapt to dark and light themes.</p>
                            </CardContent>
                            <CardFooter className="flex justify-between">
                                <Button variant="outline" size="sm">Cancel</Button>
                                <Button size="sm">Submit</Button>
                            </CardFooter>
                        </Card>

                        <Card variant="outline">
                            <CardHeader>
                                <CardTitle>Outline Card</CardTitle>
                                <CardDescription>A softer card variant with a border</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <p>This card has a different style but still respects the theme.</p>
                            </CardContent>
                        </Card>

                        <Card variant="elevated">
                            <CardHeader>
                                <CardTitle>Elevated Card</CardTitle>
                                <CardDescription>A card with more prominent shadow</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <p>The shadow adapts based on the current theme.</p>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Buttons */}
                    <div className="space-y-6">
                        <h2 className="text-2xl font-semibold mb-4">Buttons</h2>

                        <div className="grid grid-cols-2 gap-4">
                            <Button>Default</Button>
                            <Button variant="secondary">Secondary</Button>
                            <Button variant="outline">Outline</Button>
                            <Button variant="ghost">Ghost</Button>
                            <Button variant="link">Link</Button>
                            <Button variant="destructive">Destructive</Button>
                            <Button variant="booking">Booking</Button>
                            <Button disabled>Disabled</Button>
                        </div>

                        <div>
                            <h3 className="text-lg font-medium mb-2">Button Sizes</h3>
                            <div className="flex flex-wrap items-center gap-2">
                                <Button size="sm">Small</Button>
                                <Button size="default">Default</Button>
                                <Button size="lg">Large</Button>
                                <Button size="xl">Extra Large</Button>
                            </div>
                        </div>
                    </div>

                    {/* Badges */}
                    <div className="space-y-6">
                        <h2 className="text-2xl font-semibold mb-4">Badges</h2>

                        <div className="flex flex-wrap gap-2">
                            <Badge>Default</Badge>
                            <Badge variant="secondary">Secondary</Badge>
                            <Badge variant="outline">Outline</Badge>
                            <Badge variant="destructive">Destructive</Badge>
                            <Badge variant="success">Success</Badge>
                            <Badge variant="warning">Warning</Badge>
                            <Badge variant="info">Info</Badge>
                        </div>

                        <Card>
                            <CardHeader>
                                <CardTitle>Feature Status</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ul className="space-y-3">
                                    <li className="flex items-center justify-between">
                                        <span>Theme System</span>
                                        <Badge variant="success">Complete</Badge>
                                    </li>
                                    <li className="flex items-center justify-between">
                                        <span>Booking Management</span>
                                        <Badge variant="info">In Progress</Badge>
                                    </li>
                                    <li className="flex items-center justify-between">
                                        <span>Payment Integration</span>
                                        <Badge variant="warning">Planned</Badge>
                                    </li>
                                </ul>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            )}

            {activeTab === "colors" && (
                <div className="space-y-8">
                    <div>
                        <h2 className="text-2xl font-semibold mb-4">Theme Colors</h2>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            <div className="p-4 bg-background border rounded-lg">
                                <div className="font-medium">Background</div>
                                <div className="text-xs text-muted-foreground">bg-background</div>
                            </div>
                            <div className="p-4 bg-foreground text-background rounded-lg">
                                <div className="font-medium">Foreground</div>
                                <div className="text-xs opacity-80">text-foreground</div>
                            </div>
                            <div className="p-4 bg-card border rounded-lg">
                                <div className="font-medium">Card</div>
                                <div className="text-xs text-muted-foreground">bg-card</div>
                            </div>
                            <div className="p-4 bg-card-foreground text-background rounded-lg">
                                <div className="font-medium">Card Foreground</div>
                                <div className="text-xs opacity-80">text-card-foreground</div>
                            </div>
                            <div className="p-4 bg-primary text-primary-foreground rounded-lg">
                                <div className="font-medium">Primary</div>
                                <div className="text-xs opacity-80">bg-primary</div>
                            </div>
                            <div className="p-4 bg-secondary text-secondary-foreground rounded-lg">
                                <div className="font-medium">Secondary</div>
                                <div className="text-xs opacity-80">bg-secondary</div>
                            </div>
                            <div className="p-4 bg-muted text-muted-foreground rounded-lg">
                                <div className="font-medium">Muted</div>
                                <div className="text-xs opacity-80">bg-muted</div>
                            </div>
                            <div className="p-4 bg-accent text-accent-foreground rounded-lg">
                                <div className="font-medium">Accent</div>
                                <div className="text-xs opacity-80">bg-accent</div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === "typography" && (
                <div className="space-y-8">
                    <div>
                        <h2 className="text-2xl font-semibold mb-4">Typography</h2>
                        <div className="space-y-6">
                            <div className="space-y-2">
                                <h1 className="text-5xl font-bold">Heading 1</h1>
                                <p className="text-xs text-muted-foreground">text-5xl font-bold</p>
                            </div>
                            <div className="space-y-2">
                                <h2 className="text-4xl font-bold">Heading 2</h2>
                                <p className="text-xs text-muted-foreground">text-4xl font-bold</p>
                            </div>
                            <div className="space-y-2">
                                <h3 className="text-3xl font-bold">Heading 3</h3>
                                <p className="text-xs text-muted-foreground">text-3xl font-bold</p>
                            </div>
                            <div className="space-y-2">
                                <h4 className="text-2xl font-bold">Heading 4</h4>
                                <p className="text-xs text-muted-foreground">text-2xl font-bold</p>
                            </div>
                            <div className="space-y-2">
                                <h5 className="text-xl font-bold">Heading 5</h5>
                                <p className="text-xs text-muted-foreground">text-xl font-bold</p>
                            </div>
                            <div className="space-y-2">
                                <p className="text-base">Base paragraph text for the application. This is a paragraph of text that shows the default font size and line height.</p>
                                <p className="text-xs text-muted-foreground">text-base</p>
                            </div>
                            <div className="space-y-2">
                                <p className="text-sm">Small text for less important information or secondary content.</p>
                                <p className="text-xs text-muted-foreground">text-sm</p>
                            </div>
                            <div className="space-y-2">
                                <p className="text-xs">Extra small text for captions, labels, and other minor elements.</p>
                                <p className="text-xs text-muted-foreground">text-xs</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}
