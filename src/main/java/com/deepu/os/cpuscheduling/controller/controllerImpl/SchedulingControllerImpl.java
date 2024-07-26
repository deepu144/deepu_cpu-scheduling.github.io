package com.deepu.os.cpuscheduling.controller.controllerImpl;

import com.deepu.os.cpuscheduling.constant.Constant;
import com.deepu.os.cpuscheduling.controller.SchedulingController;
import com.deepu.os.cpuscheduling.enumeration.ResponseStatus;
import com.deepu.os.cpuscheduling.request.CommonRequest;
import com.deepu.os.cpuscheduling.response.CommonResponse;
import com.deepu.os.cpuscheduling.service.SchedulingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@RestController
@RequestMapping("/os")
public class SchedulingControllerImpl implements SchedulingController {
    @Autowired
    private SchedulingService schedulingService;
    private final static Logger LOGGER = LoggerFactory.getLogger(SchedulingControllerImpl.class);

    @Override
    @PostMapping("/fcfs")
    public ResponseEntity<CommonResponse> firstComeFirstServe(CommonRequest commonRequest, BindingResult result) {
        try {
            if(result.hasErrors()){
                LOGGER.error("** firstComeFirstServe: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculateFirstComeFirstServe(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** firstComeFirstServe: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }

    @PostMapping("/sjf")
    public ResponseEntity<CommonResponse> shortestJobFirst(@RequestBody @Valid CommonRequest commonRequest , BindingResult result){
        try {
            if(result.hasErrors()){
                LOGGER.error("** shortestJobFirst: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculateShortestJobFirst(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** shortestJobFirst: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }


    @PostMapping("/srtf")
    public ResponseEntity<CommonResponse> shortestRemainingTimeFirst(@RequestBody @Valid CommonRequest commonRequest , BindingResult result){
        try {
            if(result.hasErrors()){
                LOGGER.error("** shortestRemainingTimeFirst: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculateShortestRemainingTimeFirst(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** shortestRemainingTimeFirst: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }

    @PostMapping("/rr")
    public ResponseEntity<CommonResponse> roundRobin(@RequestBody @Valid CommonRequest commonRequest , BindingResult result){
        try {
            if(result.hasErrors()){
                LOGGER.error("** roundRobin: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculateRoundRobin(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** roundRobin: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }

    @PostMapping("/pnp")
    public ResponseEntity<CommonResponse> priorityNonPreemptive(@RequestBody @Valid CommonRequest commonRequest , BindingResult result){
        try {
            if(result.hasErrors()){
                LOGGER.error("** priorityNonPreemptive: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculatePriorityNonPreemptive(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** priorityNonPreemptive: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }

    @PostMapping("/pp")
    public ResponseEntity<CommonResponse> priorityPreemptive(@RequestBody @Valid CommonRequest commonRequest , BindingResult result){
        try {
            if(result.hasErrors()){
                LOGGER.error("** priorityPreemptive: {}", Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBadRequestCommonResponse(result));
            }
            return ResponseEntity.ok(schedulingService.calculatePriorityPreemptive(commonRequest));
        }catch (Exception e ){
            LOGGER.error("** priorityPreemptive: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerError(e));
        }
    }

    private CommonResponse getBadRequestCommonResponse(BindingResult result) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(400);
        commonResponse.setStatus(ResponseStatus.FAILED);
        commonResponse.setData(null);
        commonResponse.setErrorMessage(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        return commonResponse;
    }

    public CommonResponse getServerError(Exception e){
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(500);
        commonResponse.setStatus(ResponseStatus.FAILED);
        commonResponse.setData(e.getMessage());
        commonResponse.setErrorMessage(Constant.SERVER_ERROR_MESSAGE);
        return commonResponse;
    }

}
