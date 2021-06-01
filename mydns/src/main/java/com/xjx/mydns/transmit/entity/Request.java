package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.SocketAddress;

/**
 * DNS查询报文接收解析后存储为Request对象等待处理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    short sequence;
    Packet packet;
    SocketAddress socketAddress;

    public Request(Packet packet, SocketAddress socketAddress) {
        this.packet = packet;
        this.socketAddress = socketAddress;
    }
}
