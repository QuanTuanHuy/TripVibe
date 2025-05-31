import { PriceManager } from "@/components/calendar/PriceManager";
import { Header } from "@/components/common/Header";

export default function CalendarPage() {
    return (
        <div>
            <Header />
            <div className="flex-1 space-y-4 p-4 md:p-8 pt-6">
                <PriceManager />
            </div>
        </div>
    );
}
