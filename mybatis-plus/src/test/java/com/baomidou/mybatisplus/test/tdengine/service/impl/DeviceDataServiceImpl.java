package com.baomidou.mybatisplus.test.tdengine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.tdengine.entity.DeviceData;
import com.baomidou.mybatisplus.test.tdengine.mapper.DeviceDataMappper;
import com.baomidou.mybatisplus.test.tdengine.service.IDeviceDataService;
import org.springframework.stereotype.Service;

@Service
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMappper, DeviceData> implements IDeviceDataService {
}
