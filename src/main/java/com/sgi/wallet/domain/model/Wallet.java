package com.sgi.wallet.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Wallet {

    private String id;
    private String dni;
    private String phone;
    private String imei;
    private String email;
    private String associatedCardId;
    private BigDecimal balance;


}
