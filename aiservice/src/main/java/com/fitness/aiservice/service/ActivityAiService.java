package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityAiService {


    private final GeminiService geminiService;


    public Recommendation generateRecomendation(Activity activity) {

        String prompt = createPromtForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
//        log.info("response from AI: {}", aiResponse);


        return  processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n", "")
                    .trim();

            log.info("parsed response from AI: {}", jsonContent);
            JsonNode analysisNode = mapper.readTree(jsonContent).path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartrate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");


            List<String> improvements = extractImprovements(mapper.readTree(jsonContent).path("improvements"));
            List<String> suggestions = extractSuggestions(mapper.readTree(jsonContent).path("suggestions"));
            List<String> safety = extractSafety(mapper.readTree(jsonContent).path("safty"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .safetty(safety)
                    .suggestions(suggestions)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return defaultRecommendation(activity);
        }

    }

    private Recommendation defaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with current routine"))
                .safetty(Collections.singletonList(
                        "Always warm Up Before workour")
                )
                .suggestions(Collections.singletonList("consider consulting a fitness professional"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafety(JsonNode safetysNode) {
        List<String> safety = new ArrayList<>();
        if (safetysNode.isArray()) {
            safetysNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general guid lines") : safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String area = suggestion.path("wprkout").asText();
                String details = suggestion.path("description").asText();
                suggestions.add(area);
                suggestions.add(details);
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("NO Specific improvmentrs provided") : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String details = improvement.path("recommendation").asText();
                improvements.add(area);
                improvements.add(details);
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided") : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }


    private String createPromtForActivity(Activity activity) {

        return String.format(
                """
                        Analyze this finess activity and provide detailed recomendations in the following JSON formate\s
                        
                            {
                                  "analysis": {
                                    "overall": "overall analysis here",
                                    "pace": "pace analysis here",
                                    "heartrate": "Heart Rate analysis here",
                                    "caloriesBurned": "Calories analysis here"
                                  },
                                  "improvements": [
                                    {
                                      "area": "Area name",
                                      "recommendation": "Detailed recommendation"
                                    }
                                  ],
                                  "suggestions": [
                                    {
                                      "wprkout": "workout name",
                                      "description": "Detailed workout description"
                                    }
                                  ],
                                  "safty": [
                                    "safety point 1",
                                    "safety point 2"
                                  ]
                                }
                                      Analyze this activity:
                                      Activity Type: %s
                                      Duration: %d minutes
                                      Calories Burned: %d
                                      Additional Metrics: %s
                        
                                   provide detailed analysis focusing on performance, improvements,next sorkout
                                   Ensure the response follows the exact JSON format above
                        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMatrics()
        );


    }

}
