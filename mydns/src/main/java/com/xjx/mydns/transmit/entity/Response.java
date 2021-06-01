package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.SocketAddress;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    short sequence;
    byte[] packet;
    SocketAddress socketAddress;
}
