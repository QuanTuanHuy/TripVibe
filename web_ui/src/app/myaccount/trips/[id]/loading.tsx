export default function BookingDetailLoading() {
    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <div className="h-16 bg-[#003b95] dark:bg-[#001f4d]"></div>

            <div className="container max-w-6xl mx-auto px-4 py-8">
                <div className="h-6 w-40 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse mb-6"></div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Main content column */}
                    <div className="lg:col-span-2">
                        <div className="mb-6">
                            <div className="flex justify-between items-start">
                                <div>
                                    <div className="h-8 w-64 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse mb-2"></div>
                                    <div className="h-4 w-48 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                </div>
                                <div className="h-6 w-24 bg-gray-200 dark:bg-gray-700 rounded-full animate-pulse"></div>
                            </div>
                        </div>

                        {/* Hotel Image */}
                        <div className="mb-6 h-[300px] bg-gray-200 dark:bg-gray-700 rounded-lg animate-pulse"></div>

                        {/* Stay Details */}
                        <div className="mb-6 bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                            <div className="border-b border-gray-200 dark:border-gray-700 px-6 py-4">
                                <div className="h-6 w-40 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                            </div>
                            <div className="p-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    {[1, 2].map((i) => (
                                        <div key={i}>
                                            <div className="h-4 w-20 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse mb-2"></div>
                                            <div className="h-6 w-32 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                        </div>
                                    ))}
                                </div>

                                <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    {[1, 2].map((i) => (
                                        <div key={i}>
                                            <div className="h-4 w-32 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse mb-2"></div>
                                            <div className="h-6 w-20 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>

                        {/* Payment Information */}
                        <div className="mb-6 bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                            <div className="border-b border-gray-200 dark:border-gray-700 px-6 py-4">
                                <div className="h-6 w-40 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                            </div>
                            <div className="p-6">
                                <div className="h-48 space-y-4">
                                    {[1, 2, 3].map((i) => (
                                        <div key={i} className="h-6 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Sidebar column */}
                    <div className="space-y-6">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                                <div className="border-b border-gray-200 dark:border-gray-700 px-6 py-4">
                                    <div className="h-6 w-32 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                </div>
                                <div className="p-6">
                                    <div className="space-y-4">
                                        {[1, 2].map((j) => (
                                            <div key={j} className="h-6 bg-gray-200 dark:bg-gray-700 rounded-md animate-pulse"></div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}
