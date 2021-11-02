package com.uzassystem.workflow.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Upload;
import com.openkm.dao.LegacyDAO;

public class SetContractMetadata implements ActionHandler {

	private static final long serialVersionUID = 1L;

	private String contractType;
	private String docUuid;
	private String docName;
	private String currency;
	private String projectCode;
	private String amount;
	private String amountvat;
	private String vat;
	private String description;
	private String subproject;
	private final String CONNECTION_URL = "jdbc:sqlserver://172.30.10.2:1433;databaseName=dev_uzassdb";
	// jdbc:sqlserver://172.30.10.2:1433;databaseName=uzassdb for prod version
	private final String USER = "openkm";
	private String PASS = "Pas123lol*/6";
	private int projectId;
	private String linkedContract;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		this.contractType = ((Select) executionContext.getContextInstance().getVariable("okp:contract.type"))
				.getValue();

		this.projectCode = (String) executionContext.getContextInstance().getVariable("pro_tiagram");

		String queryForPassword = "SELECT * FROM okm_db_metadata_value WHERE DMV_TABLE = 'sqlcon';";
		String pass = LegacyDAO.executeSQL(queryForPassword).get(0).get(1);
		if (!pass.equals(null)) {
			this.PASS = pass;
		}

		String queryCode;
		PreparedStatement ps = null;
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASS)) {
			queryCode = "SELECT * from projects WHERE project_code = ?";
			ps = connection.prepareStatement(queryCode);
			ps.setString(1, this.projectCode);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				this.projectId = rs.getInt("projectId");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		Select currencySelect = (Select) executionContext.getContextInstance().getVariable("okp:contract.currency");
		if (currencySelect != null) {
			this.currency = currencySelect.getValue();
		}

		Input amountInput = (Input) executionContext.getContextInstance().getVariable("okp:contract.amount");
		if (amountInput != null) {
			this.amount = amountInput.getValue();
		}

		Input vatInput = (Input) executionContext.getContextInstance().getVariable("okp:contract.vat");
		if (vatInput != null) {
			this.vat = vatInput.getValue();
		}

		Input amountVatInput = (Input) executionContext.getContextInstance().getVariable("okp:contract.amount.vat");
		if (amountVatInput != null) {
			this.amountvat = amountVatInput.getValue();
		}

		Input contractList = (Input) executionContext.getContextInstance().getVariable("contract.number");
		if (contractList != null) {
			this.linkedContract = contractList.getValue();
		}

		Input subprojectInput = (Input) executionContext.getContextInstance().getVariable("okp:contract.subproject");
		if (subprojectInput != null) {
			this.subproject = subprojectInput.getValue();
		}

		TextArea descriptionArea = (TextArea) executionContext.getContextInstance().getVariable("okp:description");
		if (descriptionArea != null) {
			this.description = descriptionArea.getValue();
		}

		Upload upd = new Upload();
		upd = (Upload) executionContext.getContextInstance().getVariable("uploadContract");
		this.docUuid = upd.getDocumentUuid();
		String path = OKMRepository.getInstance().getNodePath(null, this.docUuid);
		this.docName = path.substring(path.lastIndexOf("/") + 1);

		String[] splitByDot = path.split("\\.");
		String extension = splitByDot[splitByDot.length - 1];

		String projectLink = "<a target='_blank' href='http://edms.uzassystem.net/ProjectManagement/project-details?projectId="
				+ this.projectId + "'>Click me to go to project page</a>";

		int parentSequenceNumber;

		String docPath = "/okm:root/Contractual Documents/" + this.projectCode + "/Contracts/" + this.linkedContract;

		if (this.contractType.equals("CNT")) {

			this.docName = (String) executionContext.getContextInstance().getVariable("newCntName") + "." + extension;

			Map<String, String> contract_map = new HashMap<String, String>();
			contract_map.put("okp:contract.pro_code", this.projectCode);
			contract_map.put("okp:contract.project.link", projectLink);
			contract_map.put("okp:contract.number", this.docName.split("\\.")[0]);
			contract_map.put("okp:contract.currency", this.currency);
			contract_map.put("okp:contract.amount", this.amount);
			contract_map.put("okp:contract.subproject", this.subproject);
			contract_map.put("okp:contract.vat", this.vat);
			contract_map.put("okp:contract.amount.vat", this.amountvat);
			contract_map.put("okp:contract.description", this.description);
			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:contract", contract_map);
		} else if (this.contractType.equals("AMT")) {

			String amdSequence = "001";
			parentSequenceNumber = extractSequenceNumberInt(this.linkedContract + ".");

			amdSequence = String.format("%03d",
					getActualGreatestSequence(docPath, this.contractType, parentSequenceNumber) + 1);

			this.docName = String.format("%s-CNT-AMD-%s-%s.%s", this.projectCode,
					extractSequenceNumberString(this.linkedContract + "."), amdSequence, extension);
			Map<String, String> amendment_map = new HashMap<String, String>();
			amendment_map.put("okp:amendment.pro_code", this.projectCode);
			amendment_map.put("okp:amendment.number", this.docName.split("\\.")[0]);
			amendment_map.put("okp:amendment.description", this.description);
			amendment_map.put("okp:amendment.project.link", projectLink);
			amendment_map.put("okp:amendment.contract.link", this.linkedContract);

			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:amendment", amendment_map);
			OKMDocument.getInstance().move(null, this.docUuid, docPath);

		} else if (this.contractType.equals("ANX")) {

			String anxSequence = "001";
			parentSequenceNumber = extractSequenceNumberInt(this.linkedContract + ".");

			anxSequence = String.format("%03d",
					getActualGreatestSequence(docPath, this.contractType, parentSequenceNumber) + 1);

			this.docName = this.projectCode + "-CNT-ANX-" + anxSequence + "." + extension;
			this.docName = String.format("%s-CNT-ANX-%s-%s.%s", this.projectCode,
					extractSequenceNumberString(this.linkedContract + "."), anxSequence, extension);
			Map<String, String> annex_map = new HashMap<String, String>();
			annex_map.put("okp:annex.pro_code", this.projectCode);
			annex_map.put("okp:annex.number", this.docName.split("\\.")[0]);
			annex_map.put("okp:annex.description", this.description);
			annex_map.put("okp:annex.project.link", projectLink);
			annex_map.put("okp:annex.contract.link", this.linkedContract);

			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:annex", annex_map);
			OKMDocument.getInstance().move(null, this.docUuid, docPath);

		}

		executionContext.getContextInstance().setVariable("DocumentNewName", this.docName);

		OKMDocument.getInstance().rename((String) null, this.docUuid, this.docName);
		OKMDocument.getInstance().lock(null, this.docUuid);

		executionContext.getToken().signal();
	}

	public int getGreatestSequence(List<String> docNames) {
		int answer = 0;
		for (int i = 0; i < docNames.size(); i++) {
			int currentSequenceNum = extractSequenceNumberInt(docNames.get(i));
			answer = Math.max(answer, currentSequenceNum);
		}
		return answer;
	}

	public int getActualGreatestSequence(String path, String docType, int parentSequenceNumber) throws Exception {
		List<String> names = getDocNamesListInFolder(path, docType);
		if (parentSequenceNumber >= 1) {
			return getGreatestSequence(filterSubdocumentsByContractNumber(names, parentSequenceNumber));
		}
		return getGreatestSequence(names);
	}

	public List<String> filterSubdocumentsByContractNumber(List<String> docNames, int parentSequenceNumber) {
		List<String> answer = new ArrayList<>();
		for (int i = 0; i < docNames.size(); i++) {
			String[] splitByDash = docNames.get(i).split("-");
			int currentParentSequenceNumber = Integer.parseInt(splitByDash[splitByDash.length - 2]);
			if (currentParentSequenceNumber == parentSequenceNumber) {
				answer.add(docNames.get(i));
			}
		}
		return answer;
	}

	public List<String> getDocNamesListInFolder(String path, String docType) throws Exception {
		List<String> answer = new ArrayList<>();
		Map<String, String> propertyGroupsByDocTypeTrigrams = new HashMap<>();
		propertyGroupsByDocTypeTrigrams.put("AMT", "okg:amendment");
		propertyGroupsByDocTypeTrigrams.put("ANX", "okg:annex");
		propertyGroupsByDocTypeTrigrams.put("CNT", "okg:contract");
		for (Document doc : OKMDocument.getInstance().getChildren(null, path)) {
			if (OKMPropertyGroup.getInstance().hasGroup(null, doc.getUuid(),
					propertyGroupsByDocTypeTrigrams.get(docType))) {

				String documentName = doc.getPath().substring(doc.getPath().lastIndexOf("/") + 1);
				answer.add(documentName);
			}
		}
		return answer;
	}

	public int extractSequenceNumberInt(String docName) {
		int lastIndexOfDot = docName.lastIndexOf(".");
		if (lastIndexOfDot <= 3) {
			throw new RuntimeException(
					"LastIndexOfDot in extractSequenceNumber is less than 3. Document name: " + docName);
		}
		return Integer.parseInt(docName.substring(lastIndexOfDot - 3, lastIndexOfDot));
	}

	public String extractSequenceNumberString(String docName) {
		int lastIndexOfDot = docName.lastIndexOf(".");
		if (lastIndexOfDot <= 3) {
			throw new RuntimeException(
					"LastIndexOfDot in extractSequenceNumber is less than 3. Document name: " + docName);
		}
		return docName.substring(lastIndexOfDot - 3, lastIndexOfDot);
	}

}
