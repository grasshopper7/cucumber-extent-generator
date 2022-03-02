package tech.grasshopper.processor;

import java.util.List;
import java.util.stream.Collectors;

import com.aventstack.extentreports.markuputils.MarkupHelper;

import lombok.Builder;
import lombok.Setter;
import tech.grasshopper.pojo.Step;

@Builder
public class StepProcessor {

	@Setter
	private Step step;

	public void process() {
		updateDataTableMarkup(step);
		updateDocString(step);
	}

	protected void updateDataTableMarkup(Step step) {
		List<List<String>> cells = step.getRows().stream().map(r -> r.getCells()).collect(Collectors.toList());
		if (cells.size() < 1)
			return;
		step.setDataTableMarkup(processTable(cells));
	}

	protected void updateDocString(Step step) {
		if (step.getDocString().getValue() == null || step.getDocString().getValue().isEmpty())
			return;
		step.setDocStringMarkup(MarkupHelper.createCodeBlock(step.getDocString().getValue()).getMarkup());
	}

	private String processTable(List<List<String>> data) {
		if (data.size() < 1)
			return "";
		String[][] array = new String[data.size()][];
		int i = 0;
		for (List<String> nestedList : data) {
			array[i++] = nestedList.toArray(new String[nestedList.size()]);
		}
		return MarkupHelper.createTable(array).getMarkup();
	}
}
