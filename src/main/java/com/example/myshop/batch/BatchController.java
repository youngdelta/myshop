package com.example.myshop.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job productJob;
    private final Job orderJob;
    private final JobExplorer jobExplorer;

    @PostMapping("/run")
    public ResponseEntity<String> runBatchJob(@RequestBody BatchJobRequest request) {
        String jobName = request.getJobName();
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            if ("productJob".equals(jobName)) {
                jobLauncher.run(productJob, jobParameters);
                return ResponseEntity.ok("Product batch job started successfully.");
            } else if ("orderJob".equals(jobName)) {
                jobLauncher.run(orderJob, jobParameters);
                return ResponseEntity.ok("Order batch job started successfully.");
            } else {
                return ResponseEntity.badRequest().body("Invalid job name: " + jobName);
            }
            
        } catch (Exception e) {
            log.error("Error running batch job: {}", jobName, e);
            return ResponseEntity.internalServerError().body("Error running batch job: " + e.getMessage());
        }
    }

    @GetMapping("/executions")
    public List<BatchJobExecutionDto> getBatchJobExecutions() {
        List<String> jobNames = jobExplorer.getJobNames();

        return jobNames.stream()
                .flatMap(jobName -> {
                    List<org.springframework.batch.core.JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 100);
                    return instances != null ? instances.stream() : Stream.empty();
                })
                .flatMap(jobInstance -> {
                    List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstance); // <--- 이 부분 수정
                    return executions != null ? executions.stream() : Stream.empty();
                })
                .map(this::convertToDto)
                .sorted((e1, e2) -> {
                    LocalDateTime e1StartTime = e1.getStartTime();
                    LocalDateTime e2StartTime = e2.getStartTime();

                    if (e1StartTime == null && e2StartTime == null) return 0;
                    if (e1StartTime == null) return 1; // Nulls last
                    if (e2StartTime == null) return -1; // Nulls last
                    return e2StartTime.compareTo(e1StartTime);
                })
                .collect(Collectors.toList());
    }

    private BatchJobExecutionDto convertToDto(JobExecution jobExecution) {
        String exitCode = (jobExecution.getExitStatus() != null) ? jobExecution.getExitStatus().getExitCode() : null;
        String exitMessage = (jobExecution.getExitStatus() != null) ? jobExecution.getExitStatus().getExitDescription() : null;

        return new BatchJobExecutionDto(
                jobExecution.getId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime(),
                jobExecution.getStatus().name(),
                exitCode,
                exitMessage
        );
    }
}