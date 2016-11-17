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
import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class RateAnalyserRunner {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        RequestRateRepository repository = context.getBean(RequestRateRepository.class);
        LocalDateTime failTime = LocalDateTime.of(2016, 11, 17, 1, 38, 8, 538);
        LocalDateTime from = failTime.minus(24, ChronoUnit.HOURS);

        List<RequestRate> rates = repository.findAllByTimestampBetween(toMillis(from), toMillis(failTime));
        long rate = getOverallRate(rates);
        System.out.println("RATE => " + rate);
        System.exit(0);
    }

    private static long getOverallRate(List<RequestRate> rates) {
        return rates.stream().mapToInt(RequestRate::getRequestPerMinute).sum();
    }

    private static long toMillis(LocalDateTime from) {
        return from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
