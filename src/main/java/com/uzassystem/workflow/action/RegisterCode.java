package com.uzassystem.workflow.action;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.form.Download;
import com.openkm.bean.form.Node;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Upload;
import com.openkm.dao.LegacyDAO;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMPropertyGroup;

public class RegisterCode implements ActionHandler {
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
	public String reviewer_name;
	public String approver_name;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Select pro_tiagram = (Select) executionContext.getContextInstance().getVariable("okp:codification.pro_tiagram");
		if (pro_tiagram != null) {
			this.tiagram_value = pro_tiagram.getValue();
		}
		Select pro_phase = (Select) executionContext.getContextInstance().getVariable("okp:codification.pro_phase");
		if (pro_phase != null) {
			this.phase_value = pro_phase.getValue();
		}

		Select discipline = (Select) executionContext.getContextInstance().getVariable("okp:codification.discipline");
		if (discipline != null) {
			this.discip_value = discipline.getValue();
		}

		Select doc_type = (Select) executionContext.getContextInstance().getVariable("okp:codification.doc_type");
		if (doc_type != null) {
			this.docType_value = doc_type.getValue();
		}

		Select location = (Select) executionContext.getContextInstance().getVariable("okp:codification.location");
		if (location != null) {
			this.loc_value = location.getValue();
		}

		Select doc_extension = (Select) executionContext.getContextInstance().getVariable("fileExtension");
		if (doc_extension != null) {
			this.docExtension = doc_extension.getValue();
		}
		Select reviewerName = (Select) executionContext.getContextInstance().getVariable("reviewerList");
		if (reviewerName != null) {
			this.reviewer_name = reviewerName.getValue();
		}
		Select approverName = (Select) executionContext.getContextInstance().getVariable("approverList");
		if (approverName != null) {
			this.approver_name = approverName.getValue();
		}
		this.docCode = this.tiagram_value + "-" + this.phase_value + "-" + this.discip_value + "-" + this.docType_value
				+ "-" + this.loc_value;

		String sequenceQuery;

		try {
			sequenceQuery = "SELECT DMV_COL03 FROM OKM_DB_METADATA_VALUE WHERE DMV_COL00 = '" + this.docCode + "';";
			this.docSequenceFlag = LegacyDAO.executeSQL(sequenceQuery);
			if (((List<String>) this.docSequenceFlag.get(0)).size() == 0) {
				this.docSequence = "000";
			} else if (((List<String>) this.docSequenceFlag.get(0)).size() > 0
					&& ((List<String>) this.docSequenceFlag.get(0)).size() < 10) {
				this.docSequence = "00" + String.valueOf(((List<String>) this.docSequenceFlag.get(0)).size());
			} else if (((List<String>) this.docSequenceFlag.get(0)).size() >= 10
					&& ((List<String>) this.docSequenceFlag.get(0)).size() < 100) {
				this.docSequence = "0" + String.valueOf(((List<String>) this.docSequenceFlag.get(0)).size());
			} else {
				this.docSequence = String.valueOf(((List<String>) this.docSequenceFlag.get(0)).size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String rootPath;

		this.docName = this.docCode + "-" + this.docSequence + "." + this.docExtension;
		rootPath = "/okm:root/projects";
		String subPro = "/" + this.tiagram_value;
		String subPhase = "/" + this.phase_value;
		String subDisci = "/" + this.discip_value;
		String subDocType = "/" + this.docType_value;
		String subLoc = "/" + this.loc_value;
		String fullPath = rootPath + subPro + subPhase + subDisci + subDocType + subLoc;

		this.author_name = (String) executionContext.getContextInstance().getVariable("author_name");
		try {
			OKMFolder.getInstance().createMissingFolders((String) null, fullPath);
			this.templateName = this.templateName + "." + this.docExtension;
			OKMDocument.getInstance().copy((String) null, this.templatePath + this.templateName, fullPath);
			this.docUUID = OKMRepository.getInstance().getNodeUuid((String) null, fullPath + "/" + this.templateName);
			this.folderUUID = OKMRepository.getInstance().getNodeUuid((String) null, fullPath + "/");
			executionContext.setVariable("folderUUID", this.folderUUID);
			executionContext.setVariable("docUUID", this.docUUID);
			executionContext.setVariable("docName", this.docName);

			OKMDocument.getInstance().rename((String) null, this.docUUID, this.docName);
			Map<String, String> status_map = new HashMap<String, String>();
			status_map.put("okp:status.status", "WIP");
			status_map.put("okp:status.int_version", "00");
			status_map.put("okp:status.ext_version", "00");
			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:status", status_map);

			// checkout document
			OKMDocument.getInstance().checkout(null, this.docUUID);

			// traceability metadata
			Map<String, String> traceability_map = new HashMap<String, String>();
			traceability_map.put("okp:traceability.author", this.author_name);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			LocalDateTime now = LocalDateTime.now();
			traceability_map.put("okp:traceability.creation_date", dtf.format(now));
			traceability_map.put("okp:traceability.reviewer", this.reviewer_name);
			traceability_map.put("okp:traceability.approver", this.approver_name);
			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUUID, "okg:traceability",
					traceability_map);

			executionContext.setVariable("creation_date", dtf.format(now));
		} catch (Exception var17) {
			var17.printStackTrace();
		}
		// Download object
		Download downloadFile = new Download();
		List<Node> nodeList = new ArrayList<Node>();
		// downloadFile.setName("initialDownload");
		// downloadFile.setLabel("Download Document");
		Node node = new Node();
		node.setUuid(this.docUUID);
		node.setLabel(this.docName);
		nodeList.add(node);
		downloadFile.setNodes(nodeList);
		executionContext.getContextInstance().setVariable("downloadFile", downloadFile);

		Upload upd = new Upload();
		upd.setDocumentUuid(this.docUUID);
		upd.setName("dynamic");
		upd.setType("update");
		executionContext.setVariable("updData", upd);

		try {
			String query = "insert into OKM_DB_METADATA_VALUE (DMV_TABLE, DMV_COL00, DMV_COL01, DMV_COL02, DMV_COL03, DMV_COL04) VALUES ('versioning', '"
					+ this.docCode + "', '00', '00', '" + this.docSequence + "', '" + this.docUUID + "'); ";
			LegacyDAO.executeSQL(query);
		} catch (Exception var16) {
			var16.printStackTrace();
		}

		executionContext.getToken().signal();

	}
}