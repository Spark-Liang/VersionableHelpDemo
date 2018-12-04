package org.nanospark.versionablehelper.core.util;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * This class is
 */
@SuppressWarnings("WeakerAccess")
public abstract class FilteringBeanCopier<S, T> {


    private static final FilteringBeanCopier.BeanCopierKey KEY_FACTORY = (FilteringBeanCopier.BeanCopierKey) KeyFactory.create(FilteringBeanCopier.BeanCopierKey.class);
    private static final Type FILTERING_BEAN_COPIER = Type.getType(FilteringBeanCopier.class);
    private static final Signature COPY =
            new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT});

    public static <S, T> FilteringBeanCopier<S, T> create(Class<S> sourceClass, Class<T> targetClass, BiPredicate<PropertyDescriptor, PropertyDescriptor> propertyFilter) {
        Generator<S, T> generator = new Generator<>(sourceClass, targetClass, propertyFilter);
        return generator.create();
    }

    public abstract void copy(S source, T target);


    interface BeanCopierKey {
        Object newInstance(String var1, String var2, String var3);
    }

    public static class Generator<S, T> extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(FilteringBeanCopier.class.getName());
        private static final BiPredicate<PropertyDescriptor, PropertyDescriptor> DEFAULT_PROPERTY_FILTER = (o, o1) -> true;
        private Class<S> source;
        private Class<T> target;
        private BiPredicate<PropertyDescriptor, PropertyDescriptor> propertyFilter;

        public Generator(Class<S> sourceClass, Class<T> targetClass, BiPredicate<PropertyDescriptor, PropertyDescriptor> propertyFilter) {
            super(SOURCE);
            if (!Modifier.isPublic(sourceClass.getModifiers())) {
                this.setNamePrefix(sourceClass.getName());
            }
            this.source = sourceClass;
            if (!Modifier.isPublic(targetClass.getModifiers())) {
                this.setNamePrefix(targetClass.getName());
            }
            this.target = targetClass;
            this.propertyFilter = propertyFilter != null ? propertyFilter : DEFAULT_PROPERTY_FILTER;
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        protected ClassLoader getDefaultClassLoader() {
            return this.source.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.source);
        }

        @SuppressWarnings("unchecked")
        public FilteringBeanCopier<S, T> create() {
            Object key = FilteringBeanCopier.KEY_FACTORY.newInstance(this.source.getName(), this.target.getName(), this.propertyFilter.toString());
            return (FilteringBeanCopier<S, T>) super.create(key);
        }

        public void generateClass(ClassVisitor v) {
            Type sourceType = Type.getType(this.source);
            Type targetType = Type.getType(this.target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_8, Constants.ACC_PUBLIC, this.getClassName(), FilteringBeanCopier.FILTERING_BEAN_COPIER, Constants.TYPES_EMPTY, Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(1, FilteringBeanCopier.COPY, Constants.TYPES_EMPTY);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(this.source);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(this.target);
            Map<String, PropertyDescriptor> names = new HashMap<>();

            for (PropertyDescriptor getter1 : getters) {
                names.put(getter1.getName(), getter1);
            }

            e.load_arg(1);
            e.checkcast(targetType);
            e.load_arg(0);
            e.checkcast(sourceType);

            for (PropertyDescriptor setter : setters) {
                PropertyDescriptor getter = names.get(setter.getName());
                if (getter != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    if (propertyFilter.test(getter, setter) && compatible(getter, setter)) {
                        e.dup2();
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }

            e.return_value();
            e.end_method();
            ce.end_class();
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
