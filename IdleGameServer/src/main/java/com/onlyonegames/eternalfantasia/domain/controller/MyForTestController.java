package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CommandDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ContainerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ElementDto;
import com.onlyonegames.eternalfantasia.domain.service.MyForTestService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyForTestController
{
    private final MyForTestService myForTestService;

    @GetMapping("api/GetForTest")
    public ResponseDTO<Map<String, Object>> GetMyForTest()
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myForTestService.GetMyForTest(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("api/CmdForTest")
    public ResponseDTO<Map<String, Object>> CmdForTest(@RequestBody RequestDto requestDto)
    //public ResponseDTO<Map<String, Object>> SetMyForTest(@RequestBody ElementDto requestDto)
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ContainerDto container = null;
        CommandDto cmd = null;
        for(int i = 0; i < requestDto.cmds.size(); i++)
        {
            cmd = requestDto.cmds.get(i);
            switch (cmd.cmd)
            {
                case "get":
                    for(int j = 0; j < cmd.containers.size(); j++)
                    {
                        container = cmd.containers.get(j);
                        switch (container.container)
                        {
                            case "MyForTest":
                                container.Element = myForTestService.Get(userId, container.Element);
                                break;
                        }
                    }
                    break;
                case "set":
                    for(int j = 0; j < cmd.containers.size(); j++)
                    {
                        container = cmd.containers.get(j);
                        switch (container.container)
                        {
                            case "MyForTest":
                                container.Element = myForTestService.Set(userId, container.Element);
                                break;
                        }
                    }
                    break;
            }
        }

        /*
        CommandDto cmdDto = new CommandDto();
        cmdDto.cmd = "hello";
        cmdDto.containers = new ArrayList<>();

        RequestDto result = new RequestDto();
        result.cmds = new ArrayList<>();
        result.cmds.add(cmdDto);
        map.put("cmdRequest", result);
         */

        /*
        ElementDto element = new ElementDto();
        element.setElement("hello");
        element.setValue("22");

        ContainerDto container = new ContainerDto();
        container.Element = new ArrayList<>();
        container.Element.add(element);

        CommandDto command = new CommandDto();
        command.cmd = "get";
        command.containers = new ArrayList<>();
        command.containers.add(container);

        List<CommandDto> buffer = new ArrayList<>();
        buffer.add(command);
         */

        //map.put("cmdRequest", buffer);
        map.put("cmdRequest", requestDto.cmds);

        Map<String, Object> response = map;
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /*
    @PostMapping("api/SetForTest")
    public ResponseDTO<Map<String, Object>> SetMyForTest(@RequestBody RequestDto requestDto)
    //public ResponseDTO<Map<String, Object>> SetMyForTest(@RequestBody ElementDto requestDto)
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Map<String, Object> response = myForTestService.SetMyForTest(userId, requestDto.getKey(), requestDto.getValue(), map);
        Map<String, Object> response = myForTestService.SetMyForTest(userId,
                requestDto.cmds.get(0).containers.get(0).Element.get(0).getElement(),
                requestDto.cmds.get(0).containers.get(0).Element.get(0).getValue(),
                map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

     */
}