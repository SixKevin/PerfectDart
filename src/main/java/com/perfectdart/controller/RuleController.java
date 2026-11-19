package com.perfectdart.controller;

import com.perfectdart.dto.RuleResponse;
import com.perfectdart.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    public RuleResponse getRules() {
        return ruleService.getDartsRules();
    }
}