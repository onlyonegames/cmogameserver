package com.onlyonegames.eternalfantasia.domain.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.monsterstable;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/GameData" })
public class LoadGameDatasController {

    @PersistenceContext
    // @PersistenceUnit
    private EntityManager entityManage;

    @Transactional
    @GetMapping
    public Map getData() {
        Map<String, List> map = new HashMap<>();
        StoredProcedureQuery sp = entityManage.createStoredProcedureQuery("readgamedatas_procedure");
        boolean isResultSet = sp.execute();
        if (isResultSet) {
            List<herostable> results1 = (List<herostable>) sp.getResultList(); // get the first result set : herostable
            List<monsterstable> results2 = null;
            if (sp.hasMoreResults()) {
                results2 = sp.getResultList(); // get the second result set : monsterstable
            }
            map.put("heros", results1);
            map.put("monsters", results2);
        }
        return map;
    }
}