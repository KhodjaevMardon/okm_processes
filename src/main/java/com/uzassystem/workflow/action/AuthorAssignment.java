package com.uzassystem.workflow.action;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;

import org.jbpm.taskmgmt.exe.Assignable;

public class AuthorAssignment implements AssignmentHandler {
	private static final long serialVersionUID = 1L;

	public String author_name;

	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {

		this.author_name = (String) executionContext.getContextInstance().getVariable("author_name");

		if (this.author_name != null) {
			assignable.setActorId(this.author_name);
		} else {
			assignable.setActorId("massaad");
		}

	}
}