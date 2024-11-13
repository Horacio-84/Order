package com.example.order.calculation;

import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledCalculator {

    @Autowired
    private OrderService orderService;

    // Injete o SumCalculator via construtor
    public ScheduledCalculator(SumCalculator sumCalculator) {
        this.sumCalculator = sumCalculator;
    }

    // Método agendado para rodar a cada intervalo (ex.: a cada 5 minutos)
    @Scheduled(fixedRate = 300000) // Tempo em milissegundos (5 minutos)
    public void calculateAndLogTotal() {

        orderService.calculateTotal();
    }
}
