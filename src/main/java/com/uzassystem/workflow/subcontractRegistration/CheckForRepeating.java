package com.uzassystem.workflow.subcontractRegistration;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class CheckForRepeating implements DecisionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {

		String repeatOrNot = (String) executionContext.getContextInstance().getVariable("repeatProcess");
		if (repeatOrNot.equals("no")) {
			return "to end";
		}
		return "repeat";

		// if (repeatOrNot.equals("yes")) {
		// return "repeat";
		// }
		//
		// return "end";

	}
}