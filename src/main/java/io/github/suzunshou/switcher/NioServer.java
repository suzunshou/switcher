package io.github.suzunshou.switcher;

import io.github.suzunshou.switcher.annotation.Annotations;
import io.github.suzunshou.switcher.annotation.KeyValue;
import io.github.suzunshou.switcher.response.ResponseEnum;
import io.github.suzunshou.switcher.response.ResponseMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NioServer extends Thread {
    Selector selector;
    Configutation configutation;
    ConcurrentHashMap<SocketChannel, Integer> channelToCountMap = new ConcurrentHashMap<>();

    public NioServer(Configutation configutation) {
        this.configutation = configutation;
    }

    @Override
    public void run() {
        try {
            initServer();
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(configutation.getPort()));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        Annotations.registerAnnotation(configutation);
    }

    public void listen() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                dispatch(key);
            }
        }
    }

    private void dispatch(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            doAccept(key);
        } else if (key.isReadable()) {
            doRead(key);
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(configutation.getReadBytesLimit());
        try {
            KeyValue keyValue = resolveKeyValue(socketChannel, readBuffer);
            if (keyValue == null) {
                calculateNullBytesCount(socketChannel);
                if (checkNullByteCount(socketChannel)) {
                    writeMessage(socketChannel, new ResponseMessage(ResponseEnum.NULL.getCode(), "without readable data!"));
                }
                return;
            }
            Annotations.changeValue(keyValue);
            writeMessage(socketChannel, new ResponseMessage(ResponseEnum.SUCCESS.getCode(), "success"));
        } catch (Exception e) {
            writeMessage(socketChannel, new ResponseMessage(ResponseEnum.ERROR.getCode(), String.format("error: %s", e.getMessage())));
            e.printStackTrace();
        }
        closeChannel(socketChannel, false);
    }

    private boolean checkNullByteCount(SocketChannel socketChannel) throws IOException {
        if (channelToCountMap.get(socketChannel) >= configutation.getEnforceDisconnectOfNullBytesCount()) {
            channelToCountMap.remove(socketChannel);
            closeChannel(socketChannel, true);
            return false;
        }
        return true;
    }

    private void calculateNullBytesCount(SocketChannel socketChannel) {
        if (!channelToCountMap.containsKey(socketChannel)) {
            channelToCountMap.put(socketChannel, 0);
        }
        channelToCountMap.put(socketChannel, channelToCountMap.get(socketChannel) + 1);
    }

    private void writeMessage(SocketChannel socketChannel, ResponseMessage responseMessage) throws IOException {
        if (socketChannel.isOpen()) {
            socketChannel.write(ByteBuffer.wrap(responseMessage.toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    private KeyValue resolveKeyValue(SocketChannel socketChannel, ByteBuffer readBuffer) throws IOException {
        int read = socketChannel.read(readBuffer);
        if (read > 0) {
            readBuffer.flip();
            byte[] data = readBuffer.array();
            String res = new String(data, 0, readBuffer.remaining());
            String sep = System.lineSeparator();
            if (res.lastIndexOf(sep) == res.length() - sep.length()) {
                res = res.substring(0, res.lastIndexOf(sep));
            }
            int index = res.indexOf(configutation.getKeyValueSplit());
            if (index != -1) {
                return new KeyValue(res.substring(0, index), res.substring(index + sep.length()));
            }
        }
        return null;
    }

    private void closeChannel(SocketChannel socketChannel, boolean forceClose) throws IOException {
        if (configutation.isCloseAfterRead() || forceClose) {
            socketChannel.close();
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
