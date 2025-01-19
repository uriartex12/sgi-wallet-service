package com.sgi.wallet.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(def = "{'id': 1, 'phone': 1, 'documentNumber': 1}", name = "id_phone_documentNumber_index", unique = true)
@Document( collection = "wallet")
public class Wallet {

    private String id;
    private String name;
    @NotNull(message = "Phone number is required")
    private String phone;
    private String imei;
    private String email;
    private String documentNumber;
    private String documentType;
    private String debitCardId;
    private CardDetails cardDetails;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal balance;
}
