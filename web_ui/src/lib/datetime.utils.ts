import { format, Locale } from "date-fns";
import { vi } from "date-fns/locale";

export function epochToDate(epoch: number): Date {
    return new Date(epoch * 1000);
}

export function formatDate(
    date: Date,
    formatString: string = 'dd/MM/yyyy',
    locale: Locale = vi
): string {
    return format(date, formatString, { locale });
}
