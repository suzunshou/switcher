package io.github.suzunshou.switcher.annotation;

import io.github.suzunshou.switcher.Configutation;
import io.github.suzunshou.switcher.handler.ValueChange;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author suzunshou 2020-12-25 11:12:04
 */
public class Annotations {

    public static final Map<String, Field> switchNameToField = new HashMap<>();
    public static final Map<String, ValueChange> switchNameToValueChangeHandler = new HashMap<>();

    public static void registerAnnotation(Configutation configutation) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                //scan All Packages.
                .setUrls(ClasspathHelper.forPackage(configutation.getScanPackages()))
                .setScanners(new FieldAnnotationsScanner()));
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Switcher.class);
        for (Field field : fields) {
            Switcher switcher = field.getAnnotation(Switcher.class);
            if (switchNameToField.containsKey(switcher.name())) {
                throw new RuntimeException("switch name [" + switcher.name() + "] duplicate!");
            }
            field.setAccessible(true);
            switchNameToField.put(switcher.name(), field);
            try {
                ValueChange valueChange = (ValueChange) Class.forName(switcher.valueChangeHandler().getName()).newInstance();
                switchNameToValueChangeHandler.put(switcher.name(), valueChange);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("resolve valueChange failure!", e);
            }
        }
    }

    public static void changeValue(KeyValue keyValue) throws IllegalAccessException {
        Field field = Annotations.switchNameToField.get(keyValue.getKey());
        if (field == null) {
            throw new RuntimeException("switch name [" + keyValue.getKey() + "] not exists!");
        }
        ValueChange valueChange = switchNameToValueChangeHandler.get(keyValue.getKey());
        Object value = valueChange.changed(field, keyValue);
        field.set(field.getDeclaringClass(), value);
    }
}
