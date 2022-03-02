package tech.grasshopper.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Test;

import lombok.Builder;
import tech.grasshopper.pojo.Feature;

@Builder
public class FeatureExtentTest {

	private Feature feature;

	private ExtentReports extent;

	public ExtentTest createTest() {
		ExtentTest featureExtentTest = extent.createTest(com.aventstack.extentreports.gherkin.model.Feature.class,
				feature.getName(), feature.getDescription());

		feature.getTags().forEach(t -> featureExtentTest.assignCategory(t.getName()));

		Test test = featureExtentTest.getModel();
		test.setStartTime(feature.getStartTime());
		test.setEndTime(feature.getEndTime());
		feature.setTestId(test.getId());

		return featureExtentTest;
	}
}
