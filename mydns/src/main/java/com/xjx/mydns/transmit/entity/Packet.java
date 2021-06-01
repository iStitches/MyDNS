package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 传输用的数据包
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Packet {
    //最大容量
    int maxSize=0;
    //当前容量
    int size=0;
    //当前偏移量
    int offset=0;
    //存储数据
    byte[] data;

    //生成数据包
    public static Packet create(int length){
        Packet packet = new Packet();
        packet.data = new byte[length];
        packet.size = 0;
        packet.maxSize = length;
        return packet;
    }
    public static Packet create(byte[] data){
        Packet packet = new Packet();
        packet.maxSize = data.length;
        packet.size = data.length;
        packet.data = data;
        return packet;
    }

    //添加数据
    public Packet addByte(byte b){
        this.data[size++] = b;
        return this;
    }
    public Packet addBytes(byte[] bytes){
        System.arraycopy(bytes,0,data,size,bytes.length);
        size += bytes.length;
        return this;
    }
    public Packet addShort(short sh){
        this.data[size++] = (byte)((sh>>8) & 0xff);
        this.data[size++] = (byte)(sh & 0xff);
        return this;
    }
    public Packet addInt(int d){
        this.data[size++] = (byte)((d>>24) & 0xff);
        this.data[size++] = (byte)((d>>16) & 0xff);
        this.data[size++] = (byte)((d>>8) & 0xff);
        this.data[size++] = (byte)(d & 0xff);
        return this;
    }
    public Packet addLong(long d){
        this.data[size++] = (byte)((d>>56) & 0xff);
        this.data[size++] = (byte)((d>>48) & 0xff);
        this.data[size++] = (byte)((d>>40) & 0xff);
        this.data[size++] = (byte)((d>>32) & 0xff);
        this.data[size++] = (byte)((d>>24) & 0xff);
        this.data[size++] = (byte)((d>>16) & 0xff);
        this.data[size++] = (byte)((d>>8) & 0xff);
        this.data[size++] = (byte)(d & 0xff);
        return this;
    }

    //更改数据
    public Packet setByte(byte b){
        this.data[offset++] = b;
        return this;
    }
    public Packet setShort(short sh){
        this.data[offset++] = (byte)((sh>>8) & 0xff);
        this.data[offset++] = (byte)(sh & 0xff);
        return this;
    }
    public Packet setInt(int d){
        this.data[offset++] = (byte)((d>>24) & 0xff);
        this.data[offset++] = (byte)((d>>16) & 0xff);
        this.data[offset++] = (byte)((d>>8) & 0xff);
        this.data[offset++] = (byte)(d & 0xff);
        return this;
    }
    public Packet setLong(long d){
        this.data[offset++] = (byte)((d>>56) & 0xff);
        this.data[offset++] = (byte)((d>>48) & 0xff);
        this.data[offset++] = (byte)((d>>40) & 0xff);
        this.data[offset++] = (byte)((d>>32) & 0xff);
        this.data[offset++] = (byte)((d>>24) & 0xff);
        this.data[offset++] = (byte)((d>>16) & 0xff);
        this.data[offset++] = (byte)((d>>8) & 0xff);
        this.data[offset++] = (byte)(d & 0xff);
        return this;
    }

    /**
     * 解析下一个short，short两个字节，把第一个字节左移8位后  |  第二个字节
     * @return
     */
    public byte getNextByte(){
        return this.data[offset++];
    }

    public short getNextShort(){
        return (short)(((this.data[offset++] & 0xff)<<8) | (this.data[offset++] & 0xff));
    }

    public int getNextInt(){
        return (int)(((this.data[offset++] & 0xff)<<24) |
                     ((this.data[offset++] & 0xff)<<16) |
                     ((this.data[offset++] & 0xff)<<8) |
                     (this.data[offset++] & 0xff));
    }

    public long getnextLong(){
        return (long)(((this.data[offset++] & 0xff)<<56) |
                      ((this.data[offset++] & 0xff)<<48) |
                      ((this.data[offset++] & 0xff)<<40) |
                      ((this.data[offset++] & 0xff)<<32) |
                      ((this.data[offset++] & 0xff)<<24) |
                      ((this.data[offset++] & 0xff)<<16) |
                      ((this.data[offset++] & 0xff)<<8) |
                      (this.data[offset++] & 0xff));
    }

    public byte[] getNextBytes(int len){
        byte[] ans = new byte[len];
        System.arraycopy(this.data,offset,ans,0,len);
        this.offset += len;
        return ans;
    }

    //数据包转化为字节数组
    public byte[] getBytes(){
        if(size == maxSize)
            return this.data;
        else{
            byte[] ans = new byte[size];
            System.arraycopy(this.data,0,ans,0,size);
            return ans;
        }
    }

    public void setOffset(int pos){
        this.offset = pos;
    }

    public void skip(int len){
        this.offset += len;
    }

    public static short getShort(byte[] data, int offset, int length){
        short val = 0;
        for (int i = 0; i < length; i++)
            val |= (data[offset + i] & 0xff) << ((length - i - 1) * 8);
        return val;
    }
}
