package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthDateConverter implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(YearMonth attribute) {
        if (attribute != null) {
            return attribute.atEndOfMonth();
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate dbData) {
        if (dbData != null) {
            return YearMonth.from(dbData);
        }
        return null;
    }
} 