package com.deepu.os.cpuscheduling.controller;

import com.deepu.os.cpuscheduling.request.CommonRequest;
import com.deepu.os.cpuscheduling.response.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SchedulingController {

    @PostMapping("/fcfs")
    ResponseEntity<CommonResponse> firstComeFirstServe(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

    @PostMapping("/sjf")
    ResponseEntity<CommonResponse> shortestJobFirst(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

    @PostMapping("/srtf")
    ResponseEntity<CommonResponse> shortestRemainingTimeFirst(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

    @PostMapping("/rr")
    ResponseEntity<CommonResponse> roundRobin(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

    @PostMapping("/pnp")
    ResponseEntity<CommonResponse> priorityNonPreemptive(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

    @PostMapping("/pp")
    ResponseEntity<CommonResponse> priorityPreemptive(@RequestBody @Valid CommonRequest commonRequest , BindingResult result);

}
