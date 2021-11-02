package com.uzassystem.workflow.unitRegistration;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class RegistrationVerdict implements DecisionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {

		String verdict = (String) executionContext.getContextInstance().getVariable("verdict");

		switch (verdict) {
		case "retry":
			return "to Unit Properties Reverification";
		case "finish":
			return "to end";
		case "repeat":
			return "to New Unit Properties Verification";
		default:
			throw new RuntimeException(
					"Unexpected value of variable 'verdict' in executionContext. Expected:'retry' or 'finish'.");
		}
	}

}