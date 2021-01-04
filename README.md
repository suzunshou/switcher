## An in-memory switch Component.

> start.
```java
Configutation configutation = Configutation.builder()
        .port(8888)
        .readBytesLimit(1024)
        .closeAfterRead(false)
        .keyValueSplit("=")
        .enforceDisconnectOfNullBytesCount(2)
        .build();
SwitchServer switchServer = new SwitchServer(configutation);
switchServer.start();
```

> use.
```java
@Switcher(name = "DEMO_SWITCH")
public static boolean DEMO_SWITCH = true;
```

> change.
```bash
nc localhost 8888
DEMO_SWITCH=false
```
