package com.deepu.os.cpuscheduling.service.serviceImpl;

import com.deepu.os.cpuscheduling.constant.Constant;
import com.deepu.os.cpuscheduling.enumeration.ResponseStatus;
import com.deepu.os.cpuscheduling.request.CommonRequest;
import com.deepu.os.cpuscheduling.response.CommonResponse;
import com.deepu.os.cpuscheduling.response.ProcessObjects;
import com.deepu.os.cpuscheduling.service.SchedulingService;
import com.deepu.os.cpuscheduling.util.Process;
import org.springframework.stereotype.Service;
import javax.naming.directory.InvalidAttributesException;
import java.util.*;

@Service
public class SchedulingServiceImpl implements SchedulingService {
    @Override
    public CommonResponse calculateFirstComeFirstServe(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        int n = burstTime.length;

        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        Integer[] processOrder = new Integer[n];
        for (int i = 0; i < n; i++) {
            processOrder[i] = i;
        }
        Arrays.sort(processOrder, Comparator.comparingInt(i -> arrivalTime[i]));
        finishTime[processOrder[0]] = burstTime[processOrder[0]];
        waitingTime[processOrder[0]] = 0;
        turnAroundTime[processOrder[0]] = burstTime[processOrder[0]];
        for (int i = 1; i < n; i++) {
            int currentProcess = processOrder[i];
            int previousProcess = processOrder[i - 1];
            finishTime[currentProcess] = finishTime[previousProcess] + burstTime[currentProcess];
            waitingTime[currentProcess] = finishTime[previousProcess] - arrivalTime[currentProcess];
            if (waitingTime[currentProcess] < 0) {
                waitingTime[currentProcess] = 0;
            }
            turnAroundTime[currentProcess] = burstTime[currentProcess] + waitingTime[currentProcess];
        }
        for (int i = 0; i < n; i++) {
            totalWaitTime += waitingTime[i];
            totalTurnAroundTime += turnAroundTime[i];
        }
        for (int i = 0; i < n; i++) {
            jobs[i] = (char) ('A' + i);
        }
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(null);
        processObjects.setTimeQuantum(null);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageTurnAroundTime((double) totalTurnAroundTime / n);
        processObjects.setAverageWaitingTime((double) totalWaitTime / n);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.FCFS_CALCULATED_SUCCESS);
        return commonResponse;
    }

    @Override
    public CommonResponse calculateShortestJobFirst(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        int n = burstTime.length;
        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        Process[] processes = new Process[n];
        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i + 1, burstTime[i], arrivalTime[i]);
            jobs[i] = (char) ('A' + i);
        }
        Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime));
        int currentTime = 0;
        int completed = 0;
        int index = 0;
        while (completed < n) {
            while (index < n && processes[index].getArrivalTime() <= currentTime) {
                pq.add(processes[index]);
                index++;
            }
            if (!pq.isEmpty()) {
                Process currentProcess = pq.poll();
                waitingTime[currentProcess.getId() - 1] = currentTime - currentProcess.getArrivalTime();
                finishTime[currentProcess.getId() - 1] = currentTime + currentProcess.getBurstTime();
                turnAroundTime[currentProcess.getId() - 1] = finishTime[currentProcess.getId() - 1] - currentProcess.getArrivalTime();
                totalWaitTime += waitingTime[currentProcess.getId() - 1];
                totalTurnAroundTime += turnAroundTime[currentProcess.getId() - 1];
                currentTime += currentProcess.getBurstTime();
                completed++;
            } else {
                currentTime = processes[index].getArrivalTime();
            }
        }
        double averageWaitTime = (double) totalWaitTime / n;
        double averageTurnAroundTime = (double) totalTurnAroundTime / n;
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(null);
        processObjects.setTimeQuantum(null);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageWaitingTime(averageWaitTime);
        processObjects.setAverageTurnAroundTime(averageTurnAroundTime);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.SJF_CALCULATED_SUCCESS);
        return commonResponse;
    }

    @Override
    public CommonResponse calculateShortestRemainingTimeFirst(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        int n = burstTime.length;
        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        Arrays.fill(waitingTime, 0);
        Arrays.fill(turnAroundTime, 0);
        Arrays.fill(finishTime, 0);
        Process[] processes = new Process[n];
        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i, burstTime[i], arrivalTime[i]);
            jobs[i] = (char) ('A' + i);
        }
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingTime).thenComparingInt(Process::getArrivalTime));
        int currentTime = 0;
        int completed = 0;
        int[] isInQueue = new int[n];
        Arrays.fill(isInQueue, 0);
        while (completed < n) {
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime && isInQueue[process.getId()] == 0 && process.getRemainingTime() > 0) {
                    pq.add(process);
                    isInQueue[process.getId()] = 1;
                }
            }
            if (!pq.isEmpty()) {
                Process current = pq.poll();
                if (current.getRemainingTime() == current.getBurstTime()) {
                    waitingTime[current.getId()] = currentTime - current.getArrivalTime();
                } else {
                    waitingTime[current.getId()] += currentTime - finishTime[current.getId()];
                }
                finishTime[current.getId()] = currentTime + 1;
                current.setRemainingTime(current.getRemainingTime() - 1);
                currentTime++;
                if (current.getRemainingTime() == 0) {
                    turnAroundTime[current.getId()] = finishTime[current.getId()] - current.getArrivalTime();
                    totalWaitTime += waitingTime[current.getId()];
                    totalTurnAroundTime += turnAroundTime[current.getId()];
                    completed++;
                } else {
                    pq.add(current);
                }
            } else {
                currentTime++;
            }
        }
        double averageWaitTime = (double) totalWaitTime / n;
        double averageTurnAroundTime = (double) totalTurnAroundTime / n;
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(null);
        processObjects.setTimeQuantum(null);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageWaitingTime(averageWaitTime);
        processObjects.setAverageTurnAroundTime(averageTurnAroundTime);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.SRTF_CALCULATED_SUCCESS);
        return commonResponse;
    }

    @Override
    public CommonResponse calculateRoundRobin(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        int timeQuantum = commonRequest.getTimeQuantum();
        int n = burstTime.length;
        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        for (int i = 0; i < n; i++) {
            jobs[i] = (char) ('A' + i);
            waitingTime[i] = 0;
        }
        Integer[] remainingBurstTime = burstTime.clone();
        Queue<Integer> queue = new LinkedList<>();
        int currentTime = 0;
        int completed = 0;
        for (int i = 0; i < n; i++) {
            if (arrivalTime[i] <= currentTime) {
                queue.add(i);
            }
        }
        while (completed < n) {
            if (queue.isEmpty()) {
                currentTime++;
                for (int i = 0; i < n; i++) {
                    if (arrivalTime[i] <= currentTime && remainingBurstTime[i] > 0) {
                        queue.add(i);
                    }
                }
                continue;
            }
            int process = queue.poll();
            if (remainingBurstTime[process] > timeQuantum) {
                currentTime += timeQuantum;
                remainingBurstTime[process] -= timeQuantum;
            } else {
                currentTime += remainingBurstTime[process];
                finishTime[process] = currentTime;
                remainingBurstTime[process] = 0;
                completed++;
                turnAroundTime[process] = finishTime[process] - arrivalTime[process];
                waitingTime[process] = turnAroundTime[process] - burstTime[process];
                totalWaitTime += waitingTime[process];
                totalTurnAroundTime += turnAroundTime[process];
            }
            for (int i = 0; i < n; i++) {
                if (arrivalTime[i] <= currentTime && remainingBurstTime[i] > 0 && !queue.contains(i) && i != process) {
                    queue.add(i);
                }
            }
            if (remainingBurstTime[process] > 0) {
                queue.add(process);
            }
        }
        double averageWaitTime = (double) totalWaitTime / n;
        double averageTurnAroundTime = (double) totalTurnAroundTime / n;
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(null);
        processObjects.setTimeQuantum(timeQuantum);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageWaitingTime(averageWaitTime);
        processObjects.setAverageTurnAroundTime(averageTurnAroundTime);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.RR_CALCULATED_SUCCESS);
        return commonResponse;
    }

    @Override
    public CommonResponse calculatePriorityNonPreemptive(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        if(commonRequest.getPriorities().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_PRIORITIES);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        Integer[] priorities = commonRequest.getPriorities().toArray(new Integer[0]);
        int n = burstTime.length;
        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        for (int i = 0; i < n; i++) {
            jobs[i] = (char) ('A' + i);
            waitingTime[i] = 0;
            turnAroundTime[i] = 0;
            finishTime[i] = 0;
        }
        int currentTime = 0;
        int completed = 0;
        boolean[] isCompleted = new boolean[n];
        while (completed < n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (arrivalTime[i] <= currentTime && !isCompleted[i]) {
                    if (priorities[i] < highestPriority) {
                        highestPriority = priorities[i];
                        idx = i;
                    } else if (priorities[i] == highestPriority) {
                        if (arrivalTime[i] < arrivalTime[idx]) {
                            idx = i;
                        }
                    }
                }
            }
            if (idx != -1) {
                currentTime += burstTime[idx];
                finishTime[idx] = currentTime;
                turnAroundTime[idx] = finishTime[idx] - arrivalTime[idx];
                waitingTime[idx] = turnAroundTime[idx] - burstTime[idx];
                totalWaitTime += waitingTime[idx];
                totalTurnAroundTime += turnAroundTime[idx];
                isCompleted[idx] = true;
                completed++;
            } else {
                currentTime++;
            }
        }
        double averageWaitTime = (double) totalWaitTime / n;
        double averageTurnAroundTime = (double) totalTurnAroundTime / n;
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(priorities);
        processObjects.setTimeQuantum(null);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageTurnAroundTime(averageTurnAroundTime);
        processObjects.setAverageWaitingTime(averageWaitTime);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.PNP_CALCULATED_SUCCESS);
        return commonResponse;
    }

    @Override
    public CommonResponse calculatePriorityPreemptive(CommonRequest commonRequest) throws InvalidAttributesException {
        if(commonRequest.getArrivalTimes().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_AT_BT);
        }
        if(commonRequest.getPriorities().size()!=commonRequest.getBurstTimes().size()){
            throw new InvalidAttributesException(Constant.INVALID_PRIORITIES);
        }
        Integer[] burstTime = commonRequest.getBurstTimes().toArray(new Integer[0]);
        Integer[] arrivalTime = commonRequest.getArrivalTimes().toArray(new Integer[0]);
        Integer[] priorities = commonRequest.getPriorities().toArray(new Integer[0]);
        int n = burstTime.length;
        ProcessObjects processObjects = new ProcessObjects();
        Integer[] waitingTime = new Integer[n];
        Integer[] turnAroundTime = new Integer[n];
        Integer[] finishTime = new Integer[n];
        Character[] jobs = new Character[n];
        int totalWaitTime = 0, totalTurnAroundTime = 0;
        for (int i = 0; i < n; i++) {
            jobs[i] = (char) ('A' + i);
        }
        Integer[] remainingBurstTime = burstTime.clone();
        int[] isCompleted = new int[n];
        int currentTime = 0;
        int completed = 0;
        while (completed != n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (arrivalTime[i] <= currentTime && isCompleted[i] == 0) {
                    if (priorities[i] < highestPriority) {
                        highestPriority = priorities[i];
                        idx = i;
                    } else if (priorities[i] == highestPriority) {
                        if (arrivalTime[i] < arrivalTime[idx]) {
                            idx = i;
                        }
                    }
                }
            }
            if (idx != -1) {
                remainingBurstTime[idx] -= 1;
                currentTime++;
                if (remainingBurstTime[idx] == 0) {
                    finishTime[idx] = currentTime;
                    turnAroundTime[idx] = finishTime[idx] - arrivalTime[idx];
                    waitingTime[idx] = turnAroundTime[idx] - burstTime[idx];
                    totalWaitTime += waitingTime[idx];
                    totalTurnAroundTime += turnAroundTime[idx];
                    isCompleted[idx] = 1;
                    completed++;
                }
            } else {
                currentTime++;
            }
        }
        processObjects.setArrivalTimes(arrivalTime);
        processObjects.setBurstTimes(burstTime);
        processObjects.setFinishTimes(finishTime);
        processObjects.setJobs(jobs);
        processObjects.setPriorities(priorities);
        processObjects.setTimeQuantum(null);
        processObjects.setTurnAroundTimes(turnAroundTime);
        processObjects.setWaitingTimes(waitingTime);
        processObjects.setAverageTurnAroundTime((double)totalTurnAroundTime/n);
        processObjects.setAverageWaitingTime((double)totalWaitTime/n);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(200);
        commonResponse.setData(processObjects);
        commonResponse.setErrorMessage(null);
        commonResponse.setStatus(ResponseStatus.SUCCESS);
        commonResponse.setSuccessMessage(Constant.PP_CALCULATED_SUCCESS);
        return commonResponse;
    }
}
