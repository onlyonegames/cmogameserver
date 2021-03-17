package com.onlyonegames.eternalfantasia.domain.service.Dungeon;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaPlayLogForBattleRecordDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayLogForBattleRecord;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaPlayLogForBattleRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@AllArgsConstructor
public class MyArenaPlayLogForBattleRecordService {
    private final MyArenaPlayLogForBattleRecordRepository myArenaPlayLogForBattleRecordRepository;

    public Map<String, Object> GetLog(Long userId, Map<String, Object> map) {
        List<MyArenaPlayLogForBattleRecord> myArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(userId, sortByBattleEndTime());
        for(MyArenaPlayLogForBattleRecord myArenaPlayLogForBattleRecord : myArenaPlayLogForBattleRecordsList) {
            myArenaPlayLogForBattleRecord.CheckedLog();
        }
        List<MyArenaPlayLogForBattleRecordDto> myArenaPlayLogForBattleRecordDtoList = new ArrayList<>();
        for(MyArenaPlayLogForBattleRecord temp : myArenaPlayLogForBattleRecordsList){
            MyArenaPlayLogForBattleRecordDto myArenaPlayLogForBattleRecordDto = new MyArenaPlayLogForBattleRecordDto();
            myArenaPlayLogForBattleRecordDto.InitFromDbData(temp);
            myArenaPlayLogForBattleRecordDtoList.add(myArenaPlayLogForBattleRecordDto);
        }
        map.put("myArenaPlayLogForBattleRecordsList", myArenaPlayLogForBattleRecordDtoList);
        return map;
    }

    private Sort sortByBattleEndTime() {
        return Sort.by(Sort.Direction.DESC, "battleEndTime");
    }
}
