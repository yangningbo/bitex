package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhang Jinwei
 * @date 2018年01月22日
 */
@AllArgsConstructor
@Getter
public enum AppealStatus implements BaseEnum {
    NOT_PROCESSED("未处理"), PROCESSED("已处理");
    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
