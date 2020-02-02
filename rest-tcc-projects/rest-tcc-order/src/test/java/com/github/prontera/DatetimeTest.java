package com.github.prontera;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

/**
 * @author Zhao Junjian
 * @date 2020/01/21
 */
public class DatetimeTest {

    /**
     * 可以看出使用static构造器of()函数的时候, 改变zone不会影响输出
     */
    @Test
    public void testHowTimeZoneSettingInfluencePrintOut_whenUsingOf() {
        final LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 20, 19, 0);
        System.out.println(localDateTime);
        System.out.println(localDateTime.atZone(ZoneId.of("UTC")));
        System.out.println(localDateTime.atZone(ZoneId.of("Asia/Shanghai")));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println(localDateTime);
    }

    /**
     * 有如下结论, {@link LocalDateTime}只会在初始化的时候指定zone, 后续操作无论如何改变zone, 都不会影响输出, zone仅仅是一个标记
     */
    @Test
    public void testHowTimeZoneSettingInfluencePrintOut_whenUsingNow() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        System.out.println(localDateTime.atZone(ZoneId.of("UTC")));
        System.out.println(localDateTime.atZone(ZoneId.of("Asia/Shanghai")));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println(localDateTime);
    }

    @Test
    public void testTimeBetween() {
        Assertions.assertEquals(1, ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(1)));
        Assertions.assertEquals(-1, ChronoUnit.SECONDS.between(LocalDateTime.now().plusSeconds(1), LocalDateTime.now()));
    }

}
