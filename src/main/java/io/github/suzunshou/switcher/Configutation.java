package io.github.suzunshou.switcher;

import lombok.Builder;
import lombok.Data;

/**
 * @author suzunshou 2020-12-25 15:46:07
 */
@Data
@Builder
public class Configutation {
    /**
     * 服务端口
     */
    private int port = 8888;

    /**
     * 每次读取TCP数据最大限制
     */
    private int readBytesLimit = 1024;

    /**
     * 读取完数据就关闭连接
     */
    private boolean closeAfterRead = false;

    /**
     * key-value分隔符
     */
    private String keyValueSplit = "=";

    /**
     * 强制关闭0字节连接次数
     */
    private int enforceDisconnectOfNullBytesCount = 0;

    /**
     * 要扫描的switch的包路径
     */
    private String scanPackages = ".";
}
