package org.mengzz.bean.demo;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.DateToStringConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mengzz
 **/
class OrikaDemoTest {

    @Test
    void testMap() {
        DefaultMapperFactory mapperFactory = getMapperFactory();
        MapperFacade facade = mapperFactory.getMapperFacade();

        SourceClass source = getSourceClass();
        // 基本使用
        TargetClass target = facade.map(source, TargetClass.class);
        compare(source, target);
        assertNotEquals(source.getEmail(), target.getMyEmail());
    }

    @Test
    void testMapAsList() {
        DefaultMapperFactory mapperFactory = getMapperFactory();
        MapperFacade facade = mapperFactory.getMapperFacade();

        SourceClass source = getSourceClass();
        // 集合映射
        List<TargetClass> target = facade.mapAsList(Lists.newArrayList(source), TargetClass.class);
        compare(source, target.get(0));
        assertNotEquals(source.getEmail(), target.get(0).getMyEmail());
    }

    @Test
    void testDiffFieldName() {
        DefaultMapperFactory mapperFactory = getMapperFactory();
        // 设置不同字段名双向映射
        mapperFactory.classMap(SourceClass.class, TargetClass.class)
                .field(SourceClass.Fields.email, TargetClass.Fields.myEmail)
                .byDefault()
                .register();

        MapperFacade facade = mapperFactory.getMapperFacade();
        SourceClass source = getSourceClass();
        TargetClass target = facade.map(source, TargetClass.class);
        compare(source, target);
        assertEquals(source.getEmail(), target.getMyEmail());
    }

    @Test
    void testConverter() throws ParseException {
        String format = "yyyy-MM-dd";
        DefaultMapperFactory mapperFactory = getMapperFactory();
        // 设置类型转换
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new DateToStringConverter(format));

        String birth = "2021-11-21";
        Date date = new SimpleDateFormat(format).parse(birth);
        SourceClass source = getSourceClass()
                .setBirth(date);

        MapperFacade facade = mapperFactory.getMapperFacade();
        TargetClass target = facade.map(source, TargetClass.class);
        compare(source, target);
        assertEquals(birth, target.getBirth());
    }

    private DefaultMapperFactory getMapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

    private void compare(SourceClass source, TargetClass target) {
        assertEquals(source.getId(), target.getId());
        assertEquals(source.getName(), target.getName());
        assertEquals(source.getAge(), target.getAge());
    }

    private SourceClass getSourceClass() {
        return SourceClass.builder()
                .id(1L)
                .name("test")
                .age(18)
                .email("email@xxx")
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    @Accessors(chain = true)
    static class SourceClass {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private Date birth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    static class TargetClass {
        private Long id;
        private String name;
        private int age;
        private String myEmail;
        private String birth;
    }

}
