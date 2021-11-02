package com.uzassystem.workflow.documentRemoval;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;

import org.jbpm.taskmgmt.exe.Assignable;

public class AuthorAssignment implements AssignmentHandler {
	private static final long serialVersionUID = 1L;

	private String authorName;

	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		this.authorName = (String) executionContext.getContextInstance().getVariable("author_name");

		if (this.authorName != null) {
			assignable.setActorId(this.authorName);
		} else {
			assignable.setActorId("massaad");
		}
	}
}