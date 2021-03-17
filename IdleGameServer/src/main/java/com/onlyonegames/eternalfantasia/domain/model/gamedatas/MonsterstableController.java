package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/Table/Monsters" })
public class MonsterstableController {

    private MonsterstableRepository monstersRepository;

    @Autowired
    public MonsterstableController(MonsterstableRepository monstersRepository) {
        this.monstersRepository = monstersRepository;
    }

    @GetMapping
    public List findAll() {
        return monstersRepository.findAll();
    }
}