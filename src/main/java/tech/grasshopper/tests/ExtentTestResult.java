package tech.grasshopper.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import lombok.Builder;
import lombok.Builder.Default;
import tech.grasshopper.pojo.Result;
import tech.grasshopper.processor.ExceptionProcessor;

@Builder
public class ExtentTestResult {

	private ExtentTest extentTest;
	private Result result;

	@Default
	private boolean strictCucumber6Behavior = true;

	public void updateTestLogStatus() {
		String stepStatus = result.getStatus();
		Throwable parsedException = null;

		if (stepStatus.equalsIgnoreCase("failed")
				|| (stepStatus.equalsIgnoreCase("undefined") && strictCucumber6Behavior)
				|| (stepStatus.equalsIgnoreCase("pending") && strictCucumber6Behavior)) {

			if (result.getErrorMessage() == null)
				parsedException = new Exception("Generic Exception - Step is " + stepStatus);
			else
				parsedException = ExceptionProcessor.builder().result(result).build().process();

			// Hack to remove stack due to exception creation
			parsedException.setStackTrace(new StackTraceElement[0]);
		}

		if (stepStatus.equalsIgnoreCase("failed"))
			extentTest.fail(parsedException);
		else if (stepStatus.equalsIgnoreCase("passed"))
			extentTest.pass("");
		else if (stepStatus.equalsIgnoreCase("undefined") && strictCucumber6Behavior)
			extentTest.fail(parsedException);
		else if (stepStatus.equalsIgnoreCase("undefined"))
			extentTest.skip(MarkupHelper.createCodeBlock("Step is undefined"));
		else if (stepStatus.equalsIgnoreCase("pending") && strictCucumber6Behavior)
			extentTest.fail(parsedException);
		else if (stepStatus.equalsIgnoreCase("pending"))
			extentTest.skip(MarkupHelper.createCodeBlock(result.getErrorMessage()));
		else if (stepStatus.equalsIgnoreCase("skipped") && result.getErrorMessage() != null)
			extentTest.skip(MarkupHelper.createCodeBlock(result.getErrorMessage()));
		else
			extentTest.skip("");
	}
}
