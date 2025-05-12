export function SimpleHeader() {
    return (
        <header className="w-full py-4 px-6 flex justify-center md:justify-start">
            <div className="max-w-7xl w-full flex items-center">
                <div className="flex items-center">
                    <div className="relative h-10 w-10 mr-2 rounded-lg overflow-hidden bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center shadow-sm">
                        <span className="text-white font-bold text-xl">T</span>
                    </div>
                    <span className="font-semibold text-lg text-gray-800 dark:text-white">TripVibe Manager</span>
                </div>
            </div>
        </header>
    )
}