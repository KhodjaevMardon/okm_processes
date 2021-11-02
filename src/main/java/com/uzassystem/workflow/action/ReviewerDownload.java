package com.uzassystem.workflow.action;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMPropertyGroup;

public class ReviewerDownload implements ActionHandler {
	private static final long serialVersionUID = 1L;

	public String userId;
	public String docUUID;
	public String fullPath;
	public String folderUUID;
	public String docCode;
	public String docName;
	public String path;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUUID");

		// update status metadata
		Map<String, String> status_map = new HashMap<String, String>();
		status_map.put("okp:status.status", "UR");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

		// checkout document
		OKMDocument.getInstance().checkout(null, this.docUUID);

	}
}
