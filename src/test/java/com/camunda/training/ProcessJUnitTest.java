package com.camunda.training;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {
  
  @Test
  @Deployment(resources = "twitter_qa.bpmn")
  public void testHappyPath() {
    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap<>();
    variables.put("content", "Executing job from test");
    // Start process with Java API and variables
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(
            "TwitterQAProcess", variables);

    assertThat(processInstance).isWaitingAt(findId("Review Tweet"));
    assertThat(task()).hasCandidateGroup("management").isNotAssigned();

    List<Task> taskList = taskService()
            .createTaskQuery()
                    .taskCandidateGroup("management")
                            .processInstanceId(processInstance.getId())
                                    .list();
    assertThat(taskList).isNotNull();
    assertThat(taskList).hasSize(1);

    Task task = taskList.get(0);

    Map<String, Object> approvedMap = new HashMap<String, Object>();
    approvedMap.put("approved", true);
    taskService().complete(task.getId(), approvedMap);

    List<Job> jobList = jobQuery()
            .processInstanceId(processInstance.getId())
                    .list();
    assertThat(jobList).hasSize(1);
    Job job = jobList.get(0);
    execute(job);
    // Make assertions on the process instance
    assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "twitter_qa.bpmn")
  public void testTweetRejected() {
    Map<String, Object> varMap = new HashMap<>();
    varMap.put("approved", true);
    varMap.put("content", "testTweetRejected method content");

    ProcessInstance processInstance = runtimeService()
            .createProcessInstanceByKey("TwitterQAProcess")
            .setVariables(varMap)
            .startAfterActivity(findId("Review Tweet"))
            .execute();
    assertThat(processInstance).isWaitingAt("publishOnTwitter");
    BpmnAwareTests.execute(BpmnAwareTests.job());
    assertThat(processInstance).isEnded().hasPassed(findId("Tweet rejected"));
  }

}
