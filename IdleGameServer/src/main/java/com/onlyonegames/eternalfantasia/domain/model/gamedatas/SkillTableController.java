package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/Table/Skills" })
public class SkillTableController {

    private SkillTableRepository skillTableRepository;

    @Autowired
    public SkillTableController(SkillTableRepository skillTableRepository) {
        this.skillTableRepository = skillTableRepository;
    }

    @GetMapping
    public List findAll() {
        return skillTableRepository.findAll();
    }
}