package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/Table/Heros" })
public class HerostableController {

    private HerostableRepository herosRepository;

    @Autowired
    public HerostableController(HerostableRepository herosRepository) {
        this.herosRepository = herosRepository;
    }

    @GetMapping
    public List findAll() {
        return herosRepository.findAll();
    }
}