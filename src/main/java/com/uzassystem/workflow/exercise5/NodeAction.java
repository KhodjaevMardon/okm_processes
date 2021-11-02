package com.uzassystem.workflow.exercise5;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.Input;

public class NodeAction implements ActionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Input number = (Input) executionContext.getContextInstance().getVariable("number");
		int value = 0;

		if (number != null) {
			value = Integer.valueOf(number.getValue());
		} else {
			value = 0;
		}

		value = value * 3;
		Input modified = new Input();
		modified.setName("modified"); // Should be equal to instance variable
										// name
		modified.setLabel("modified");
		modified.setValue(String.valueOf(value * 3));
		executionContext.getContextInstance().setVariable("modified", modified); // Should
																					// be
																					// equal
																					// to
																					// input
																					// name
	}
}