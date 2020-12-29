package io.github.suzunshou.switcher.handler;

import io.github.suzunshou.switcher.annotation.KeyValue;

import java.lang.reflect.Field;

public interface ValueChange {

    Object changed(Field field, KeyValue keyValue);
}
