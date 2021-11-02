package com.uzassystem.workflow.action;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMPropertyGroup;

public class PrepareForSignAndLock implements ActionHandler {
	private static final long serialVersionUID = 1L;
	public String docUUID;
	public String docName;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUUID");
		this.docName = (String) executionContext.getContextInstance().getVariable("docName");

		// update status metadata
		Map<String, String> status_map = new HashMap<String, String>();
		status_map.put("okp:status.status", "RFA");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

		// update traceability metadata
		Map<String, String> traceability_map = new HashMap<String, String>();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		traceability_map.put("okp:traceability.approve_date", dtf.format(now));
		traceability_map.put("okp:traceability.approval_decision", "accept");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:traceability", traceability_map);

	}
}