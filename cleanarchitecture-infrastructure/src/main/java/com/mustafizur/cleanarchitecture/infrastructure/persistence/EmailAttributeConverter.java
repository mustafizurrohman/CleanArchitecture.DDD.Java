package com.mustafizur.cleanarchitecture.infrastructure.persistence;

import com.mustafizur.cleanarchitecture.domain.customer.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public final class EmailAttributeConverter implements AttributeConverter<Email, String> {
    @Override
    public String convertToDatabaseColumn(Email attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Email.of(dbData);
    }
}
