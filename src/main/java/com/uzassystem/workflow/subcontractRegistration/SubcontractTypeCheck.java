package com.uzassystem.workflow.subcontractRegistration;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import com.openkm.bean.form.Select;

public class SubcontractTypeCheck implements DecisionHandler {
	private static final long serialVersionUID = 1L;
	private String docType;

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		Select documentType = (Select) executionContext.getContextInstance().getVariable("okp:subcontract.type");
		if (documentType != null) {
			this.docType = documentType.getValue();
		}

		// if (this.docType.equals("CNT")) {
		// return "to uploadContract";
		// } else if (this.docType.equals("AMT")) {
		// return "to uploadAmendment";
		// }
		//
		// return "to uploadAnnex";

		switch (docType) {
		case "CNT":
			return "to upload Subcontract";
		case "AMT":
			return "to upload Amendment";
		case "ANX":
			return "to upload Annex";
		default:
			throw new RuntimeException("Unexpected docType (expected CNT|AMT|ANX)");
		}

	}
}