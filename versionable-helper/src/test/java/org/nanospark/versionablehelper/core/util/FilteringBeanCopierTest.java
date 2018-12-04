package org.nanospark.versionablehelper.core.util;

import org.junit.Test;
import org.nanospark.versionablehelper.core.annotation.CrossDayChangeDetect;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;


public class FilteringBeanCopierTest {

    @Test
    public void canCreateCopierAndCopy() {
        //given
        String name = "Test";
        Date date = new Date();
        TestBean beanToCopy = getTestBean(name, date);

        //when
        FilteringBeanCopier<TestBean, TestBean> SUT = FilteringBeanCopier.create(TestBean.class, TestBean.class, null);
        TestBean bean = new TestBean();
        SUT.copy(beanToCopy, bean);

        //then
        assertThat(bean.getName()).isEqualTo(name);
        assertThat(bean.getDate()).isEqualTo(date);
    }

    @Test
    public void canDoFilteringOnProperty() {
        //given
        BiPredicate<PropertyDescriptor, PropertyDescriptor> filter = (propertyDescriptor, propertyDescriptor2) -> propertyDescriptor.getName().equals("name")
                && propertyDescriptor2.getName().equals("name");
        String name = "Test";
        Date date = new Date();
        TestBean beanToCopy = getTestBean(name, date);

        //when
        FilteringBeanCopier<TestBean, TestBean> SUT = FilteringBeanCopier.create(TestBean.class, TestBean.class, filter);
        TestBean bean = new TestBean();
        SUT.copy(beanToCopy, bean);

        //then
        assertThat(bean.getName()).isEqualTo(name);
        assertThat(bean.getDate()).isNotEqualTo(date);
    }

    private TestBean getTestBean(String name, Date date) {
        TestBean beanToCopy = new TestBean();
        beanToCopy.setName(name);
        beanToCopy.setDate(date);
        return beanToCopy;
    }


    public static class TestBean {
        private String name;


        private Date date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestBean)) return false;
            TestBean bean = (TestBean) o;
            return Objects.equals(name, bean.name) &&
                    Objects.equals(date, bean.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, date);
        }

        @CrossDayChangeDetect
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}