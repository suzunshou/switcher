package io.github.suzunshou.switcher.handler;

import io.github.suzunshou.switcher.annotation.KeyValue;

import java.lang.reflect.Field;

/**
 * @author suzunshou 2021-12-29 10:30:05
 */
public class DefaultValueChangeHandler implements ValueChange {

    @Override
    public Object changed(Field field, KeyValue keyValue) {
        Object value;
        if (field.getType() == Boolean.class || field.getType() == boolean.class) {
            value = Boolean.valueOf(keyValue.getValue());
        } else if (field.getType() == Byte.class || field.getType() == byte.class) {
            value = Byte.valueOf(keyValue.getValue());
        } else if (field.getType() == Short.class || field.getType() == short.class) {
            value = Short.valueOf(keyValue.getValue());
        } else if (field.getType() == Integer.class || field.getType() == int.class) {
            value = Integer.valueOf(keyValue.getValue());
        } else if (field.getType() == Long.class || field.getType() == long.class) {
            value = Long.valueOf(keyValue.getValue());
        } else if (field.getType() == Float.class || field.getType() == float.class) {
            value = Float.valueOf(keyValue.getValue());
        } else if (field.getType() == Double.class || field.getType() == double.class) {
            value = Double.valueOf(keyValue.getValue());
        } else if (field.getType() == String.class) {
            value = keyValue.getValue();
        } else {
            throw new RuntimeException("field type [" + field.getType().getName() + "] not support!");
        }
        return value;
    }
}
