package com.uzassystem.workflow.subcontractRegistration;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SetNoRepetition implements ActionHandler {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		executionContext.getContextInstance().setVariable("repeatProcess", "no");

	}
}
