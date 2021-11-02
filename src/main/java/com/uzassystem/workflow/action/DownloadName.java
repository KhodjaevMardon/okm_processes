package com.uzassystem.workflow.action;

import com.openkm.bean.form.Download;
import java.util.*;
import java.util.List;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.bean.form.Node;

public class DownloadName implements ActionHandler {
	private static final long serialVersionUID = 1L;
	public String docCode;
	public String docName;
	public List<List<String>> docSequenceFlag;
	public String docSequence = "000";
	public String tiagram_label;
	public String tiagram_value;
	public String phase_label;
	public String phase_value;
	public String discip_label;
	public String discip_value;
	public String docType_label;
	public String docType_value;
	public String loc_label;
	public String loc_value;
	public String author_name;
	public String docExtension;
	public String templatePath = "/okm:root/templates/";
	public String templateName = "template";
	public String docUUID;
	public String folderUUID;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		this.docUUID = (String) executionContext.getContextInstance().getVariable("docUuid");
		this.docName = (String) executionContext.getContextInstance().getVariable("docName");

		Download downloadFile = new Download();
		List<Node> nodeList = new ArrayList<Node>();
		downloadFile.setName("initialDownload");
		downloadFile.setLabel("Download Document");
		Node node = new Node();
		node.setUuid(this.docUUID);
		node.setLabel(this.docName);
		nodeList.add(node);
		downloadFile.setNodes(nodeList);
		executionContext.getContextInstance().setVariable("initialDownload", downloadFile);

	}
}