package inu.graduation.sns.model.good.dto;

import inu.graduation.sns.domain.Good;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodDto {

    private Long id;
    private Boolean isGood;

    public static GoodDto from(Good good) {
        GoodDto goodDto = new GoodDto();
        goodDto.id = good.getId();
        goodDto.isGood = true;
        return goodDto;
    }
}
