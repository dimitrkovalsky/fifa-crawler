package com.liberty;

import com.liberty.config.Config;
import com.liberty.model.RequestRate;
import com.liberty.repositories.RequestRateRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class RateAnalyserRunner {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        RequestRateRepository repository = context.getBean(RequestRateRepository.class);

        checkMinutes(repository);
        System.exit(0);
    }

    private static void checkMinutes(RequestRateRepository repository) {
        Map<Integer, Long> map = new HashMap<>();
        for (int i = 1; i <= 60; i++) {
            LocalDateTime failTime = LocalDateTime.of(2016, 11, 22, 12, 28, 18, 765);
            LocalDateTime from = failTime.minus(i, ChronoUnit.MINUTES);

            List<RequestRate> rates = repository.findAllByTimestampBetween(toMillis(from), toMillis(failTime));
            long rate = getOverallRate(rates);
            map.put(i, rate);
        }
        map.forEach((k, v) -> {
            if (k - 1 >= 1)
                System.out.println(k + " : " + v + " => " + (v - map.get(k - 1)));
            else
                System.out.println(k + " : " + v + " => " + v);
        });
    }

    private static void checkHours(RequestRateRepository repository) {
        Map<Integer, Long> map = new HashMap<>();
        for (int i = 1; i <= 24; i++) {
            LocalDateTime failTime = LocalDateTime.of(2016, 11, 22, 12, 28, 18, 765);
            LocalDateTime from = failTime.minus(i, ChronoUnit.HOURS);

            List<RequestRate> rates = repository.findAllByTimestampBetween(toMillis(from), toMillis(failTime));
            long rate = getOverallRate(rates);
            map.put(i, rate);
        }
        map.forEach((k, v) -> {
            if (k - 1 >= 1)
                System.out.println(k + " : " + v + " => " + (v - map.get(k - 1)));
            else
                System.out.println(k + " : " + v + " => " + v);
        });
    }

    private static long getOverallRate(List<RequestRate> rates) {
        return rates.stream().mapToInt(RequestRate::getRequestPerMinute).sum();
    }

    private static long toMillis(LocalDateTime from) {
        return from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
