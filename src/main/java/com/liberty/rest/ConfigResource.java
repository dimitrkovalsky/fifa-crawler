package com.liberty.rest;

import com.liberty.model.MinerStrategy;
import com.liberty.model.UserParameters;
import com.liberty.rest.request.NewTagRequest;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.rest.request.StringRequest;
import com.liberty.rest.response.ConfigResponse;
import com.liberty.service.AutoTradingService;
import com.liberty.service.ConfigService;
import com.liberty.service.TagService;
import com.liberty.service.UserParameterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/config")
@Slf4j
public class ConfigResource {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserParameterService parameterService;

    @Autowired
    private AutoTradingService autoTradingService;

    @Autowired
    private TagService tagService;

    @RequestMapping(method = RequestMethod.GET)
    public ConfigResponse get() {
        Map<String, Integer> tagDistribution = configService.getTagDistribution();
        Set<String> activeTags = configService.getActiveTags();
        return new ConfigResponse(tagDistribution, activeTags);
    }

    @RequestMapping(path = "/parameters", method = RequestMethod.GET)
    public UserParameters getParameters() {
        return parameterService.getUserParameters();
    }

    @RequestMapping(path = "/parameters", method = RequestMethod.POST)
    public void setParameters(@RequestBody ParameterUpdateRequest request) {
        parameterService.updateParameters(request);
    }

    @RequestMapping(path = "/strategy/buy", method = RequestMethod.GET)
    public List<MinerStrategy> getBuyStrategies() {
        return autoTradingService.getBuyStrategies();
    }

    @RequestMapping(path = "/strategy/buy", method = RequestMethod.POST)
    public void activateBuyStrategies(@RequestBody MinerStrategy strategy) {
        autoTradingService.activateBuyStrategy(strategy.getId());
    }

    @RequestMapping(path = "/strategy/sell", method = RequestMethod.GET)
    public List<MinerStrategy> getSellStrategies() {
        return autoTradingService.getSellStrategies();
    }

    @RequestMapping(path = "/strategy/sell", method = RequestMethod.POST)
    public void activateSellStrategies(@RequestBody MinerStrategy strategy) {
        autoTradingService.activateSellStrategy(strategy.getId());
    }

    @RequestMapping(path = "/activate", method = RequestMethod.POST)
    public void activateTag(@RequestBody StringRequest request) {
        configService.activateTag(request.getString());
    }

    @RequestMapping(path = "/deactivate", method = RequestMethod.POST)
    public void deactivateTag(@RequestBody StringRequest request) {
        configService.deactivateTag(request.getString());
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public void updateActive() {
        configService.updateActivePlayersPrices();
    }

    @RequestMapping(path = "/tag/add", method = RequestMethod.POST)
    public void addTag(@RequestBody NewTagRequest request) {
        tagService.addNewTag(request.getTag());
    }

}
