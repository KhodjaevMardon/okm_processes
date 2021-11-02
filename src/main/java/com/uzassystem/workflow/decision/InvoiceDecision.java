package com.uzassystem.workflow.decision;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

import com.openkm.bean.form.Input;

public class InvoiceDecision implements DecisionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		Input number = (Input) executionContext.getContextInstance().getVariable("invoice");

		if (number != null) {
			int value = Integer.valueOf(number.getValue());

			if (value > 1000) {
				return "review";
			} else {
				return "approve";
			}
		} else {
			return "approve";
		}
	}
}