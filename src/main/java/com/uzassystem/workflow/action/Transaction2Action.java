package com.uzassystem.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class Transaction2Action implements ActionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		System.out.println("Executed transaction 2");
	}
}