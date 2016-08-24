/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by baoyongzhang on 16/8/24.
 */
@Preferences
public interface CollectionPreferences {

    void setMap(Map<String, Model1> map);
    Map<String, Model1> getMap();

    void setList(List<Model1> list);
    List<Model1> getList();

    void setSet(Set<Model1> list);
    Set<Model1> getSet();

    void setList2(List<Model2<String, Model1>> list);
    List<Model2<String, Model1>> getList2();

    class Model1 implements Comparable<Model1>{
        String name;

        public Model1(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(Model1 another) {
            return hashCode() - another.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Model1 model1 = (Model1) o;

            return name != null ? name.equals(model1.name) : model1.name == null;

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    class Model2<A, B> {
        A a;
        B b;

        public Model2(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Model2<?, ?> model2 = (Model2<?, ?>) o;

            if (a != null ? !a.equals(model2.a) : model2.a != null) return false;
            return b != null ? b.equals(model2.b) : model2.b == null;

        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }
    }

}
