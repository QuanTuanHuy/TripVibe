package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.core.service.ICurrencyService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/currencies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyController {
    ICurrencyService currencyService;

    @GetMapping
    public ResponseEntity<Resource<List<CurrencyEntity>>> getAllCurrencies() {
        return ResponseEntity.ok(new Resource<>(currencyService.getCurrencies()));
    }

    @PostMapping
    public ResponseEntity<Resource<CurrencyEntity>> createCurrency(@RequestBody CreateCurrencyDto request) {
        return ResponseEntity.ok(new Resource<>(currencyService.createCurrency(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrencyById(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}