package com.deepu.os.cpuscheduling.service;

import com.deepu.os.cpuscheduling.request.CommonRequest;
import com.deepu.os.cpuscheduling.response.CommonResponse;

import javax.naming.directory.InvalidAttributesException;

public interface SchedulingService {
    CommonResponse calculateFirstComeFirstServe(CommonRequest commonRequest) throws InvalidAttributesException;

    CommonResponse calculateShortestJobFirst(CommonRequest commonRequest) throws InvalidAttributesException;

    CommonResponse calculateShortestRemainingTimeFirst(CommonRequest commonRequest) throws InvalidAttributesException;

    CommonResponse calculateRoundRobin(CommonRequest commonRequest) throws InvalidAttributesException;

    CommonResponse calculatePriorityNonPreemptive(CommonRequest commonRequest) throws InvalidAttributesException;

    CommonResponse calculatePriorityPreemptive(CommonRequest commonRequest) throws InvalidAttributesException;
}
