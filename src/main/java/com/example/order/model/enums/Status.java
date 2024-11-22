package com.example.order.model.enums;

public enum Status {
    PENDING,        // Pedido aguardando processamento
    PROCESSING,     // Pedido em processamento
    COMPLETED,      // Pedido processado com sucesso
    TRANSMITTED,    // Pedido transmitido com sucesso
    CANCELED,       // Pedido cancelado
    FAILED;         // Pedido que falhou no processamento

}