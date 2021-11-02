package com.uzassystem.workflow.action;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.form.TextArea;

public class ApproverFeedback implements ActionHandler {
	private static final long serialVersionUID = 1L;

	public String docUUID;
	public String approverNotes;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// get uuid
		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUUID");
		TextArea approverNote = new TextArea();
		approverNote = (TextArea) executionContext.getContextInstance().getVariable("approverNotes");
		this.approverNotes = approverNote.getValue();

		// update status metadata
		Map<String, String> status_map = new HashMap<String, String>();
		status_map.put("okp:status.status", "TBM");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

		// update traceability metadata
		Map<String, String> traceability_map = new HashMap<String, String>();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		traceability_map.put("okp:traceability.approve_date", dtf.format(now));
		traceability_map.put("okp:traceability.back_to_author_date", dtf.format(now));
		traceability_map.put("okp:traceability.approval_decision", "reject");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:traceability", traceability_map);

		TextArea notes = new TextArea();
		notes.setName("approverNotes");
		notes.setLabel("Approver Notes");
		notes.setValue(this.approverNotes);
		executionContext.getContextInstance().setVariable("feedback", notes);

	}
}
