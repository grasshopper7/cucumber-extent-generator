package tech.grasshopper.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Test;

import lombok.Builder;
import tech.grasshopper.pojo.Scenario;

@Builder
public class ScenarioExtentTest {

	private Scenario scenario;

	private ExtentTest parentTest;

	public ExtentTest createTest() {
		ExtentTest scenarioExtentTest = parentTest.createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,
				scenario.getName(), scenario.getDescription());

		scenario.getTags().forEach(t -> scenarioExtentTest.assignCategory(t.getName()));

		Test test = scenarioExtentTest.getModel();
		test.setStartTime(scenario.getStartTime());
		test.setEndTime(scenario.getEndTime());
		scenario.setTestId(test.getId());

		return scenarioExtentTest;
	}
}
