package tech.grasshopper.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.model.Test;

import lombok.Builder;
import lombok.Builder.Default;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.processor.EmbeddedProcessor;

@Builder
public class StepExtentTest {

	private Step step;

	private ExtentTest parentTest;

	@Default
	private boolean strictCucumber6Behavior = true;

	public ExtentTest createTest() {
		ExtentTest stepExtentTest = null;
		GherkinKeyword keyword = null;
		try {
			// Default set to And
			keyword = new GherkinKeyword("And");
			keyword = new GherkinKeyword(step.getKeyword().trim());
		} catch (ClassNotFoundException e) {
		}

		stepExtentTest = parentTest.createNode(keyword, step.getKeyword() + step.getName(),
				step.getMatch().getLocation());
		step.setTestId(stepExtentTest.getModel().getId());

		Test test = stepExtentTest.getModel();
		test.setStartTime(step.getStartTime());
		test.setEndTime(step.getEndTime());

		if (step.getRows().size() > 0)
			stepExtentTest.pass(step.getDataTableMarkup());
		if (step.getDocStringMarkup() != null)
			stepExtentTest.pass(step.getDocStringMarkup());
		for (String msg : step.getOutput())
			stepExtentTest.info(msg);
		if (step.getEmbeddings().size() > 0)
			EmbeddedProcessor.builder().test(stepExtentTest).embeddings(step.getEmbeddings()).build().process();

		ExtentTestResult.builder().extentTest(stepExtentTest).result(step.getResult())
				.strictCucumber6Behavior(strictCucumber6Behavior).build().updateTestLogStatus();
		return stepExtentTest;
	}
}
