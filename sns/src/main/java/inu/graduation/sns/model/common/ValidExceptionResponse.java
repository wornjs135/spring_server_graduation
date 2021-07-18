package inu.graduation.sns.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidExceptionResponse {

    private String message;

    private Map<String, String> errorField;
}
