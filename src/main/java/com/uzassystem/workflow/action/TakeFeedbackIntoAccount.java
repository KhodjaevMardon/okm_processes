package com.uzassystem.workflow.action;

import com.openkm.api.OKMDocument;
import java.util.*;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMPropertyGroup;

public class TakeFeedbackIntoAccount implements ActionHandler {
	private static final long serialVersionUID = 1L;
	public String docName;
	public String docUUID;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUUID");
		this.docName = (String) executionContext.getContextInstance().getVariable("docName");

		OKMDocument.getInstance().rename((String) null, this.docUUID, this.docName);
		Map<String, String> status_map = new HashMap<String, String>();
		status_map.put("okp:status.status", "WIP");
		String version = (String) executionContext.getContextInstance().getVariable("docName");
		Integer int_version = Integer.parseInt(version) + 1;
		status_map.put("okp:status.int_version", int_version.toString());
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

		// checkout document
		OKMDocument.getInstance().checkout(null, this.docUUID);

	}
}