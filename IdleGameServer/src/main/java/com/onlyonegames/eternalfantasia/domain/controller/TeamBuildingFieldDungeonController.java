package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.AutoTeamBuildRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.TeamBuildingRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.TeamSwitchRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.TeamBuildingFieldDungeonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TeamBuildingFieldDungeonController {
    private final TeamBuildingFieldDungeonService teamBuildingFieldDungeonService;

    @PostMapping("/api/TeamBuilding/FieldDungeon/AddTeam")
    public ResponseDTO<Map<String, Object>> addTeam(@RequestBody TeamBuildingRequestDto teamBuildingRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = teamBuildingFieldDungeonService.AddTeam(userId, teamBuildingRequestDto.getIndex(), teamBuildingRequestDto.getCharacterId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/TeamBuilding/FieldDungeon/RemoveTeam")
    public ResponseDTO<Map<String, Object>> removeTeam(@RequestBody TeamBuildingRequestDto teamBuildingRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = teamBuildingFieldDungeonService.RemoveTeam(userId, teamBuildingRequestDto.getIndex(), teamBuildingRequestDto.getCharacterId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/TeamBuilding/FieldDungeon/SwitchTeam")
    public ResponseDTO<Map<String, Object>> switchTeam(@RequestBody TeamSwitchRequestDto teamSwitchRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = teamBuildingFieldDungeonService.SwitchTeam(userId, teamSwitchRequestDto.getChangeA().getIndex(), teamSwitchRequestDto.getChangeA().getCharacterId(), teamSwitchRequestDto.getChangeB().getIndex(), teamSwitchRequestDto.getChangeB().getCharacterId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/TeamBuilding/FieldDungeon/AutoTeam")
    public ResponseDTO<Map<String, Object>> teamAutoSet(@RequestBody AutoTeamBuildRequestDto autoTeamBuildRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = teamBuildingFieldDungeonService.TeamAutoSet(userId, autoTeamBuildRequestDto.getTeamIds(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
