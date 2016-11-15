package com.liberty.rest;

import com.liberty.model.Statistic;
import com.liberty.robot.AuctionRobot;
import com.liberty.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/stats")
@Slf4j
public class StatisticResource {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private AuctionRobot auctionRobot;

    @RequestMapping(method = RequestMethod.GET)
    public Statistic get() {
        Statistic generalStatistic = statisticService.getGeneralStatistic();
        generalStatistic.setRobotEnabled(!auctionRobot.isDisabled());
        return generalStatistic;
    }
}
