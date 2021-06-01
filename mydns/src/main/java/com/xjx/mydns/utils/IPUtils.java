package com.xjx.mydns.utils;

/**
 * 转换String类型的 IP地址
 */
public class IPUtils {

    // String转换为 byte[4]
    public static byte[] convertStrToBytes(String ipName){
        byte[] ans = new byte[4];
        String[] splits = ipName.split("\\.");
        for(int i=0;i<4;i++){
            ans[i] = (byte) (Integer.parseInt(splits[i]) & 0xff);
        }
        return ans;
    }

    // String转换为 long
    public static long convertStrToLong(String ipName){
        long ans = 0;
        String[] splits = null;
        if(ipName!=null && ipName!="")
           splits = ipName.split("\\.");
        if(splits.length > 0)
            for(int i=0;i<4;i++){
                 ans |= (Integer.valueOf(splits[i]) & 0xff) << (3-i)*8;
            }
        return ans & 0xffffffffL;
    }

    // byte[] 转换为 long
    public static long convertByteToLong(byte[] bytes, int start, int end){
        long ans = 0;
        for(int i=0;i<bytes.length;i++){
            ans |= ((long)bytes[start+i] & 0xff) << ((bytes.length-i-1)*8);
        }
        return ans;
    }
}
