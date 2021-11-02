package com.uzassystem.workflow.action;

import com.openkm.bean.form.Download;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.form.Node;

public class UpdateForReview implements ActionHandler {
	private static final long serialVersionUID = 1L;
	public String docUUID;
	public String docName;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUUID");
		this.docName = (String) executionContext.getContextInstance().getVariable("docName");

		// update status metadata
		Map<String, String> status_map = new HashMap<String, String>();
		status_map.put("okp:status.status", "RFR");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

		// update traceability metadata
		Map<String, String> traceability_map = new HashMap<String, String>();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		traceability_map.put("okp:traceability.review_request_date", dtf.format(now));
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:traceability", traceability_map);

		Download downloadFile = new Download();
		List<Node> nodeList = new ArrayList<Node>();
		Node node = new Node();
		node.setUuid(this.docUUID);
		node.setLabel(this.docName);
		nodeList.add(node);
		downloadFile.setNodes(nodeList);
		executionContext.getContextInstance().setVariable("downloadFile", downloadFile);

	}
}