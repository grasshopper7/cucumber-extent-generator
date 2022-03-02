package tech.grasshopper.tests;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.aventstack.extentreports.ExtentTest;

import lombok.Builder;
import tech.grasshopper.pojo.Scenario;

@Builder
public class ScenarioOutlineExtentTest {

	private Scenario scenarioOutline;

	private ExtentTest parentTest;

	public ExtentTest createTest() {

		ExtentTest scenarioOutlineExtentTest = parentTest.createNode(
				com.aventstack.extentreports.gherkin.model.ScenarioOutline.class,
				parseTestNameFromId(scenarioOutline.getId()), scenarioOutline.getDescription());
		return scenarioOutlineExtentTest;
	}

	private String parseTestNameFromId(String id) {
		String[] splits = id.split(";");
		if (splits.length != 4)
			return id;

		String[] name = splits[1].split("-");
		return Arrays.stream(name).map(t -> {
			if (t.length() == 1)
				return t.toUpperCase();
			else if (t.length() > 1)
				return t.substring(0, 1).toUpperCase() + t.substring(1);
			else
				return "";
		}).collect(Collectors.joining(" "));
	}
}
