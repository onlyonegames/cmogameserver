package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ExchangeItemEventTable;
import lombok.Data;

import java.util.List;

@Data
public class FieldExchangeItemEventResponseDto {
    public List<MyFieldExchangeItemEventDto.MaxExchange> maxExchangeList;
    public List<ExchangeItemEventTable> exchangeItemEventTableList;

    public void SetFieldExchangeItemEventReponseDto(List<MyFieldExchangeItemEventDto.MaxExchange> maxExchangeList, List<ExchangeItemEventTable> exchangeItemEventTableList) {
        this.maxExchangeList = maxExchangeList;
        this.exchangeItemEventTableList = exchangeItemEventTableList;
    }
}
