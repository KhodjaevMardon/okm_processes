package com.uzassystem.workflow.subcontractRegistration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class SetSubcontractMetadata implements ActionHandler {

	private static final long serialVersionUID = 1L;

	private String subcontractType;
	private String subcontractorName;
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
	private String linkedSubcontract;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		this.subcontractType = ((Select) executionContext.getContextInstance().getVariable("okp:subcontract.type"))
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

		Select currencySelect = (Select) executionContext.getContextInstance().getVariable("okp:subcontract.currency");
		if (currencySelect != null) {
			this.currency = currencySelect.getValue();
		}

		Input amountInput = (Input) executionContext.getContextInstance().getVariable("okp:subcontract.amount");
		if (amountInput != null) {
			this.amount = amountInput.getValue();
		}

		Input vatInput = (Input) executionContext.getContextInstance().getVariable("okp:subcontract.vat");
		if (vatInput != null) {
			this.vat = vatInput.getValue();
		}

		Input amountVatInput = (Input) executionContext.getContextInstance().getVariable("okp:subcontract.amount.vat");
		if (amountVatInput != null) {
			this.amountvat = amountVatInput.getValue();
		}

		Input contractList = (Input) executionContext.getContextInstance().getVariable("subcontract.number");
		if (contractList != null) {
			this.linkedSubcontract = contractList.getValue();
		}

		Input subprojectInput = (Input) executionContext.getContextInstance().getVariable("okp:subcontract.subproject");
		if (subprojectInput != null) {
			this.subproject = subprojectInput.getValue();
		}

		TextArea descriptionArea = (TextArea) executionContext.getContextInstance().getVariable("okp:description");
		if (descriptionArea != null) {
			this.description = descriptionArea.getValue();
		}

		this.subcontractorName = ((Select) executionContext.getContextInstance()
				.getVariable("okp:subcontract.subcontractor.name")).getValue();

		Upload upd = new Upload();
		upd = (Upload) executionContext.getContextInstance().getVariable("uploadSubcontract");
		this.docUuid = upd.getDocumentUuid();

		String path = OKMRepository.getInstance().getNodePath(null, this.docUuid);

		String extension = path.split("\\.")[Math.toIntExact((Arrays.stream(path.split("\\.")).count() - 1))];

		String projectLink = "<a target='_blank' href='http://edms.uzassystem.net/ProjectManagement/project-details?projectId="
				+ this.projectId + "'>Click me to go to project page</a>";

		String baseFolder = "/okm:root/Contractual Documents/";
		String folderPath = baseFolder + this.projectCode + "/Subcontracts/" + this.subcontractorName + "/"
				+ this.linkedSubcontract;

		int parentSequenceNumber; // for annexes and amendments - sequence
									// number of linked subcontract

		switch (this.subcontractType) {

		case "CNT":

			this.docName = (String) executionContext.getContextInstance().getVariable("newCntName") + "." + extension;

			Map<String, String> subcontractMap = new HashMap<String, String>(16, 1.0f);
			subcontractMap.put("okp:subcontract.pro_code", this.projectCode);
			subcontractMap.put("okp:subcontract.subcontractor.name", this.subcontractorName);
			subcontractMap.put("okp:subcontract.project.link", projectLink);
			subcontractMap.put("okp:subcontract.number", this.docName.split("\\.")[0]);
			subcontractMap.put("okp:subcontract.currency", this.currency);
			subcontractMap.put("okp:subcontract.amount", this.amount);
			subcontractMap.put("okp:subcontract.subproject", this.subproject);
			subcontractMap.put("okp:subcontract.vat", this.vat);
			subcontractMap.put("okp:subcontract.amount.vat", this.amountvat);
			subcontractMap.put("okp:subcontract.description", this.description);
			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:subcontract", subcontractMap);
			break;

		case "AMT":

			parentSequenceNumber = extractSequenceNumberInt(this.linkedSubcontract + ".");

			String amdSequence = "001";
			amdSequence = String.format("%03d",
					getActualGreatestSequence(folderPath, this.subcontractType, parentSequenceNumber) + 1);

			this.docName = this.projectCode + "-CNT-AMD-" + amdSequence + "." + extension;
			this.docName = String.format("%s-CNT-ANX-%s-%s.%s", this.projectCode,
					extractSequenceNumberString(this.linkedSubcontract + "."), amdSequence, extension);

			Map<String, String> amendmentMap = new HashMap<>(8, 1.0f);
			amendmentMap.put("okp:amendment.pro_code", this.projectCode);
			amendmentMap.put("okp:amendment.number", this.docName.split("\\.")[0]);
			amendmentMap.put("okp:amendment.description", this.description);
			amendmentMap.put("okp:amendment.project.link", projectLink);
			amendmentMap.put("okp:amendment.contract.link", this.linkedSubcontract);

			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:amendment", amendmentMap);
			OKMDocument.getInstance().move(null, this.docUuid, folderPath);
			break;

		case "ANX":

			parentSequenceNumber = extractSequenceNumberInt(this.linkedSubcontract + ".");

			String anxSequence = "001";
			anxSequence = String.format("%03d",
					getActualGreatestSequence(folderPath, this.subcontractType, parentSequenceNumber) + 1);

			this.docName = this.projectCode + "-CNT-ANX-" + anxSequence + "." + extension;
			this.docName = String.format("%s-CNT-ANX-%s-%s.%s", this.projectCode,
					extractSequenceNumberString(this.linkedSubcontract + "."), anxSequence, extension);

			Map<String, String> annexMap = new HashMap<String, String>(8, 1.0f);
			annexMap.put("okp:annex.pro_code", this.projectCode);
			annexMap.put("okp:annex.number", this.docName.split("\\.")[0]);
			annexMap.put("okp:annex.description", this.description);
			annexMap.put("okp:annex.project.link", projectLink);
			annexMap.put("okp:annex.subcontract.link", this.linkedSubcontract);

			OKMPropertyGroup.getInstance().setPropertiesSimple(null, this.docUuid, "okg:annex", annexMap);
			OKMDocument.getInstance().move(null, this.docUuid, folderPath);
			break;

		}

		OKMDocument.getInstance().rename(null, this.docUuid, this.docName);
		OKMDocument.getInstance().lock(null, this.docUuid);

		executionContext.getToken().signal();
	}

	public int getGreatestSequenceFromList(List<String> docNames) {
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
			return getGreatestSequenceFromList(filterSubdocumentsByContractNumber(names, parentSequenceNumber));
		}
		return getGreatestSequenceFromList(names);
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
		propertyGroupsByDocTypeTrigrams.put("CNT", "okg:subcontract");
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
