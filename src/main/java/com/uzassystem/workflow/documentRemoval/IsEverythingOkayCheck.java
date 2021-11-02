package com.uzassystem.workflow.documentRemoval;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class IsEverythingOkayCheck implements DecisionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		// TODO Auto-generated method stub
		int dbRecordsNumber = Integer
				.parseInt((String) executionContext.getContextInstance().getVariable("dbRecordsCount"));
		int processInstancesCount = Integer
				.parseInt((String) executionContext.getContextInstance().getVariable("processInstancesCount"));
		String docFoundValue = (String) executionContext.getContextInstance().getVariable("docFound");

		if (dbRecordsNumber == 1 || processInstancesCount == 1 || docFoundValue.equals("true")) {
			return "to Removal Process";
		} else {
			// do smth about this shit
			return "to end";
			// throw new RuntimeException("Not everything is okay");
		}
	}

}