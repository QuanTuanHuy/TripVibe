package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.service.ICurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyInitializer implements CommandLineRunner {
    private final ICurrencyService currencyService;

    @Override
    public void run(String... args) {
        log.info("Initializing currency data...");
        initializeCurrencies();
        log.info("Currency data initialization completed.");
    }

    private void initializeCurrencies() {
        List<CreateCurrencyDto> currencies = Arrays.asList(
                // Tiền tệ chính của Việt Nam
                new CreateCurrencyDto("Vietnamese Dong", "VND", "₫"),

                // Các tiền tệ quốc tế phổ biến nhất
                new CreateCurrencyDto("US Dollar", "USD", "$"),
                new CreateCurrencyDto("Euro", "EUR", "€"),
                new CreateCurrencyDto("British Pound", "GBP", "£"),

                // Tiền tệ khu vực Đông Nam Á
                new CreateCurrencyDto("Thai Baht", "THB", "฿"),
                new CreateCurrencyDto("Singapore Dollar", "SGD", "S$"),
                new CreateCurrencyDto("Malaysian Ringgit", "MYR", "RM"),
                new CreateCurrencyDto("Indonesian Rupiah", "IDR", "Rp"),
                new CreateCurrencyDto("Philippine Peso", "PHP", "₱"),

                // Tiền tệ châu Á phổ biến
                new CreateCurrencyDto("Chinese Yuan", "CNY", "¥"),
                new CreateCurrencyDto("Japanese Yen", "JPY", "¥"),
                new CreateCurrencyDto("South Korean Won", "KRW", "₩"),
                new CreateCurrencyDto("Hong Kong Dollar", "HKD", "HK$"),
                new CreateCurrencyDto("Taiwan Dollar", "TWD", "NT$"),
                new CreateCurrencyDto("Indian Rupee", "INR", "₹"),

                // Tiền tệ châu Âu
                new CreateCurrencyDto("Swiss Franc", "CHF", "CHF"),
                new CreateCurrencyDto("Norwegian Krone", "NOK", "kr"),
                new CreateCurrencyDto("Swedish Krona", "SEK", "kr"),
                new CreateCurrencyDto("Danish Krone", "DKK", "kr"),
                new CreateCurrencyDto("Polish Zloty", "PLN", "zł"),
                new CreateCurrencyDto("Czech Koruna", "CZK", "Kč"),
                new CreateCurrencyDto("Hungarian Forint", "HUF", "Ft"),
                new CreateCurrencyDto("Russian Ruble", "RUB", "₽"),

                // Tiền tệ châu Mỹ
                new CreateCurrencyDto("Canadian Dollar", "CAD", "C$"),
                new CreateCurrencyDto("Mexican Peso", "MXN", "MX$"),
                new CreateCurrencyDto("Brazilian Real", "BRL", "R$"),
                new CreateCurrencyDto("Argentine Peso", "ARS", "AR$"),

                // Tiền tệ Trung Đông và châu Phi
                new CreateCurrencyDto("United Arab Emirates Dirham", "AED", "د.إ"),
                new CreateCurrencyDto("Saudi Riyal", "SAR", "﷼"),
                new CreateCurrencyDto("Israeli Shekel", "ILS", "₪"),
                new CreateCurrencyDto("Turkish Lira", "TRY", "₺"),
                new CreateCurrencyDto("South African Rand", "ZAR", "R"),
                new CreateCurrencyDto("Egyptian Pound", "EGP", "E£"),

                // Tiền tệ châu Úc và Thái Bình Dương
                new CreateCurrencyDto("Australian Dollar", "AUD", "A$"),
                new CreateCurrencyDto("New Zealand Dollar", "NZD", "NZ$"),

                // Tiền tệ khác thường xuất hiện trên booking.com
                new CreateCurrencyDto("Icelandic Krona", "ISK", "kr"),
                new CreateCurrencyDto("Croatian Kuna", "HRK", "kn"),
                new CreateCurrencyDto("Bulgarian Lev", "BGN", "лв"),
                new CreateCurrencyDto("Romanian Leu", "RON", "lei"),
                new CreateCurrencyDto("Ukrainian Hryvnia", "UAH", "₴"),
                new CreateCurrencyDto("Kazakhstani Tenge", "KZT", "₸"),

                // Tiền tệ bổ sung cho khách du lịch quốc tế
                new CreateCurrencyDto("Georgian Lari", "GEL", "₾"),
                new CreateCurrencyDto("Moroccan Dirham", "MAD", "DH"),
                new CreateCurrencyDto("Tunisian Dinar", "TND", "د.ت"),
                new CreateCurrencyDto("Jordanian Dinar", "JOD", "د.ا"),
                new CreateCurrencyDto("Lebanese Pound", "LBP", "ل.ل"),
                new CreateCurrencyDto("Qatar Riyal", "QAR", "﷼"),
                new CreateCurrencyDto("Kuwaiti Dinar", "KWD", "د.ك"),
                new CreateCurrencyDto("Bahraini Dinar", "BHD", ".د.ب"),
                new CreateCurrencyDto("Oman Rial", "OMR", "﷼"));

        try {
            currencyService.createIfNotExists(currencies);
            log.info("Successfully initialized {} currencies", currencies.size());
        } catch (Exception e) {
            log.error("Failed to initialize currencies. Error: {}", e.getMessage(), e);
        }
    }
}
