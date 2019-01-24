package com.jie.service.serviceimpl;

import com.jie.service.iservice.ITestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements ITestService {
    @Override
    public String test() {

        return "test";
    }
}
