package com.baomidou.mybatisplus.test.tdengine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.test.tdengine.entity.DeviceData;
import com.baomidou.mybatisplus.test.tdengine.mapper.DeviceDataMappper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:tdengine/spring-test-tdengine.xml"})
class TDengineTest {

    @Autowired
    private DeviceDataMappper mapper;

    @BeforeAll
    static void insert(@Autowired DeviceDataMappper mapper) {
        Date ts = new Date();
        DeviceData data = DeviceData.builder()
            .ts(ts)
            .rawData("{voltage:12.0}")
            .voltage(12.0)
            .deviceNo("001")
            .deviceName("设备1")
            .build();
        assertEquals(1, mapper.insert(data));
        assertEquals(1, mapper.selectCount(null));
        assertEquals(1, mapper.deleteById(ts));
        assertEquals(0, mapper.selectCount(null));
    }

    @BeforeAll
    static void insertBatch(@Autowired DeviceDataMappper mapper) {
        int total = 10000;
        int deviceCount = 10;
        Random random = new Random();
        Calendar startDate = new GregorianCalendar(2022, Calendar.JANUARY, 1);
        Calendar endDate = new GregorianCalendar(2023, Calendar.DECEMBER, 31);
        for (int i = 0; i < deviceCount; i++) {
            String deviceNo = String.valueOf((char) ('A' + i));
            List<DeviceData> list = new ArrayList<>(total);
            for (int j = 0; j < total / deviceCount; j++) {
                double voltage = random.nextDouble() + 12;
                DeviceData data = DeviceData.builder()
                    .ts(generateRandomDate(random, startDate, endDate))
                    .rawData(String.format("{voltage:%s}", voltage))
                    .voltage(voltage)
                    .deviceNo(deviceNo)
                    .deviceName("设备" + deviceNo)
                    .build();
                list.add(data);
            }
            assertEquals(total / deviceCount, mapper.insertBatch(list));
        }
        assertEquals(total, mapper.selectCount(null));
    }

    @Test
    void selectLastOne() {
        DeviceData deviceData = mapper.selectLastOne(null);
        assertNotNull(deviceData);
        System.out.println("deviceData = " + deviceData);
    }

    @Test
    void selectLastList() {
        LambdaQueryWrapper<DeviceData> wrapper = new LambdaQueryWrapper<>(DeviceData.class)
            .groupBy(DeviceData::getDeviceNo);
        List<DeviceData> list = mapper.selectLastList(wrapper);
        assertEquals(10, list.size());
        System.out.println("list = " + list);
    }

    @Test
    void selectLastRowList() {
        LambdaQueryWrapper<DeviceData> wrapper = new LambdaQueryWrapper<>(DeviceData.class)
            .groupBy(DeviceData::getDeviceNo);
        List<DeviceData> list = mapper.selectLastRowList(wrapper);
        assertEquals(11, list.size());
        System.out.println("list = " + list);
    }

    private static Date generateRandomDate(Random random, Calendar startDate, Calendar endDate) {
        long startMillis = startDate.getTimeInMillis();
        long endMillis = endDate.getTimeInMillis();
        long randomMillisSinceEpoch = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));

        Calendar randomDate = Calendar.getInstance();
        randomDate.setTimeInMillis(randomMillisSinceEpoch);
        return randomDate.getTime();
    }
}
