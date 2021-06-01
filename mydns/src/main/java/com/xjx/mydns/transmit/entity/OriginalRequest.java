package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.SocketAddress;

/**
 * 客户端Request转发至上游DNS服务器后,需要保存客户端Request,在返回客户端Response时需要保证报文序号一致
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalRequest {
    public short sequence;
    public SocketAddress socketAddress;
}
