package com.baomidou.mybatisplus.test.tdengine.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.tdengine.SubtableName;
import com.baomidou.mybatisplus.annotation.tdengine.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author Gerrit
 * @since 2023-08-16
 */
@Data
@Builder
@SubtableName("device_${deviceNo}_data")
public class DeviceData {

    @TableId
    private Date ts;
    private String rawData;
    private Double voltage;

    @Tag
    private String deviceNo;

    @Tag
    private String deviceName;
}
