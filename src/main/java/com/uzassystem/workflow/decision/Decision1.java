package com.uzassystem.workflow.decision;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

import com.openkm.bean.form.Input;

public class Decision1 implements DecisionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		Input number = (Input) executionContext.getContextInstance().getVariable("number");

		if (number != null) {
			int value = Integer.valueOf(number.getValue());

			if (value > 20) {
				return "trans1";
			} else {
				return "trans2";
			}
		} else {
			return "trans2";
		}
	}
}