package io.github.suzunshou.switcher;

public class SwitcherMain {

    public static void main(String[] args) {
        Configutation configutation = Configutation.builder()
                .port(8888)
                .readBytesLimit(1024)
                .closeAfterRead(false)
                .keyValueSplit("=")
                .enforceDisconnectOfNullBytesCount(2)
                .build();
        SwitchServer switchServer = new SwitchServer(configutation);
        switchServer.start();
        checkValue();
    }

    private static void checkValue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Constant.DEMO_SWITCH);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
