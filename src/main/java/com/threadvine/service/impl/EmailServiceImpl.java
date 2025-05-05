package com.threadvine.service.impl;

import com.threadvine.dto.OrderDTO;
import com.threadvine.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    public void sendEmail(OrderDTO orderDTO) {

    }
}
