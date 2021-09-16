package inu.graduation.sns.model.notification.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNotificationRequest {

    @NotNull
    private String title;

    @NotNull
    private String content;
}
