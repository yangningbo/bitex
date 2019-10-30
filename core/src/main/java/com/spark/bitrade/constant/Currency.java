package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhang Jinwei
 * @create 2017年12月07日
 * @desc 电子币种
 */
@AllArgsConstructor
@Getter
public enum Currency implements BaseEnum {

    /**
     * 比特币
     */
    BTC("比特币");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
