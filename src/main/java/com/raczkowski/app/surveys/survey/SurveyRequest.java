package com.raczkowski.app.surveys.survey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.raczkowski.app.surveys.questions.QuestionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyRequest {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime endTime;
    private List<QuestionRequest> questions;
}