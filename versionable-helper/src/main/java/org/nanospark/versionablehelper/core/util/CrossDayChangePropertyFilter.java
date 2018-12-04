package org.nanospark.versionablehelper.core.util;

import org.nanospark.versionablehelper.core.annotation.CrossDayChangeDetect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

public class CrossDayChangePropertyFilter implements BiPredicate<PropertyDescriptor, PropertyDescriptor> {
    @Override
    public boolean test(PropertyDescriptor propertyDescriptor, PropertyDescriptor propertyDescriptor2) {
        return hasCrossChangeAnnotation(propertyDescriptor)
                && hasCrossChangeAnnotation(propertyDescriptor2);
    }

    private boolean hasCrossChangeAnnotation(PropertyDescriptor propertyDescriptor) {
        return hasCrossChangeAnnotation(propertyDescriptor.getReadMethod())
                || hasCrossChangeAnnotation(propertyDescriptor.getWriteMethod());

    }

    private boolean hasCrossChangeAnnotation(Method method) {
        return null != method.getDeclaredAnnotation(CrossDayChangeDetect.class);
    }

}
