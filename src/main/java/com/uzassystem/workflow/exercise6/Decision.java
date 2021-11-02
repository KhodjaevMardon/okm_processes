package com.uzassystem.workflow.exercise6;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

import com.openkm.bean.form.Input;

public class Decision implements DecisionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		Input number = (Input) executionContext.getContextInstance().getVariable("number");
		Input suggested = (Input) executionContext.getContextInstance().getVariable("suggested");

		if (number != null && suggested != null && number.getValue().equals(suggested.getValue())) {
			return "good";
		} else {
			return "bad";
		}
	}
}