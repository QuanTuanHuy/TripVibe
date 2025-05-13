export default function ThemeDemoLoading() {
    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <div className="h-8 w-48 bg-muted rounded-md animate-pulse mb-2"></div>
                    <div className="h-4 w-96 bg-muted rounded-md animate-pulse"></div>
                </div>
                <div className="h-8 w-32 bg-muted rounded-md animate-pulse"></div>
            </div>

            <div className="flex border-b mb-6">
                <div className="h-10 w-24 bg-muted rounded-md animate-pulse mr-2"></div>
                <div className="h-10 w-24 bg-muted rounded-md animate-pulse mr-2"></div>
                <div className="h-10 w-24 bg-muted rounded-md animate-pulse"></div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {Array.from({ length: 3 }).map((_, i) => (
                    <div key={i} className="space-y-6">
                        <div className="h-6 w-40 bg-muted rounded-md animate-pulse mb-4"></div>

                        <div className="h-64 bg-muted rounded-lg animate-pulse"></div>

                        <div className="h-48 bg-muted rounded-lg animate-pulse"></div>
                    </div>
                ))}
            </div>
        </div>
    )
}
