package com.atguigu.msmservice.service;

import java.util.Map;

public interface MsmService {
    boolean sedMsm(String phone, Map<String, String> map);
}
