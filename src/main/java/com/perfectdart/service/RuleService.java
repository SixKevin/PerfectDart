package com.perfectdart.service;

import com.perfectdart.dto.RuleResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RuleService {

    private final RestClient restClient = RestClient.create();

    // Structures internes pour lire le JSON de l'API complète Wikipedia
    private record WikiApiResponse(Query query) {}
    private record Query(List<Page> pages) {}
    private record Page(String title, String extract) {}

    public RuleResponse getDartsRules() {
        String url = "https://fr.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=1&exchars=1500&titles=Fléchettes&format=json&formatversion=2";

        WikiApiResponse response = restClient.get()
                .uri(url)
                .header("User-Agent", "PerfectDartApp/1.0")
                .retrieve()
                .body(WikiApiResponse.class);

        Page page = response.query().pages().get(0);
        return new RuleResponse(page.title(), page.extract());
    }
}