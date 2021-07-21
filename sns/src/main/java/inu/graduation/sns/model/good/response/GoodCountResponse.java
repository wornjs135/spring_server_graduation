package inu.graduation.sns.model.good.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodCountResponse {

    private Long id;
    private Integer countGood;
}
