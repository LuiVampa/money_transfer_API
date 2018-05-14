package ru.transfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Bucky on 14.05.2018.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyModel {

    @JsonProperty(required = true)
    @NotNull
    @Min(value = 0)
    private Long accountFrom;
    @JsonProperty(required = true)
    @NotNull
    @Min(value = 0)
    private Long accountTo;
    @JsonProperty(required = true)
    @NotNull
    @Min(value = 0)
    private Double amount;
    @JsonProperty(required = true)
    @NotNull
    private Currency currency;
}
