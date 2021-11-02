package com.uzassystem.workflow.unitRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Text;
import com.openkm.bean.form.Validator;
import com.openkm.dao.LegacyDAO;

public class UnitRegistration implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// So basically in this class we register the code and name of document type
	// They are expected to not be empty

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		Select databaseUnitTypeField = (Select) executionContext.getContextInstance()
				.getVariable("okp:databaseUnit.type");
		String databaseUnitType = databaseUnitTypeField.getValue();

		Input databaseUnitCodeField = (Input) executionContext.getContextInstance()
				.getVariable("okp:databaseUnit.code");
		String databaseUnitCode = databaseUnitCodeField.getValue();

		Input databaseUnitNameField = (Input) executionContext.getContextInstance()
				.getVariable("okp:databaseUnit.name");
		String databaseUnitName = databaseUnitNameField.getValue();

		executionContext.getContextInstance().setVariable("docTypeCode", databaseUnitCode);
		executionContext.getContextInstance().setVariable("docTypeName", databaseUnitName);

		List<List<String>> codeRecords = new ArrayList<>();
		List<List<String>> nameRecords = new ArrayList<>();

		Map<String, String> unitTypesMap = new HashMap<>(4, 1.0f);
		unitTypesMap.put("DT", "doc_type"); // document type
		unitTypesMap.put("DC", "doc_cat"); // document category
		unitTypesMap.put("CN", "issuer"); // client name

		String unitCodeDuplicationCheckQuery = "SELECT * FROM OKM_DB_METADATA_VALUE WHERE DMV_TABLE = '"
				+ unitTypesMap.get(databaseUnitType) + "' AND DMV_COL00 = '" + databaseUnitCode + "';";
		String unitNameDuplicationCheckQuery = "SELECT * FROM OKM_DB_METADATA_VALUE WHERE DMV_TABLE = '"
				+ unitTypesMap.get(databaseUnitType) + "' AND DMV_COL01 = '" + databaseUnitName + "';";

		codeRecords = LegacyDAO.executeSQL(unitCodeDuplicationCheckQuery);
		nameRecords = LegacyDAO.executeSQL(unitNameDuplicationCheckQuery);

		boolean isCodeUnique = (codeRecords.size() == 0);
		boolean isNameUnique = (nameRecords.size() == 0);

		boolean isUnique = (isCodeUnique && isNameUnique);

		executionContext.getContextInstance().setVariable("isUnique", String.valueOf(isUnique));

		executionContext.getContextInstance().setVariable("codeRecordsMap", codeRecords.toString());
		executionContext.getContextInstance().setVariable("nameRecordsMap", nameRecords.toString());

		if (isUnique) {

			String unitRegistrationQuery = "INSERT INTO OKM_DB_METADATA_VALUE (DMV_TABLE, DMV_COL00, DMV_COL01) VALUES ('"
					+ unitTypesMap.get(databaseUnitType) + "','" + databaseUnitCode + "', '" + databaseUnitName + "');";

			executionContext.getContextInstance().setVariable("unitRegistrationQuery", unitRegistrationQuery);

			LegacyDAO.executeSQL(unitRegistrationQuery);

			if (executionContext.getContextInstance().getVariable("to repeat").equals("true")) {

				executionContext.getContextInstance().setVariable("verdict", "repeat");

			} else {

				executionContext.getContextInstance().setVariable("verdict", "finish");

			}

		} else {

			Map<String, String> unitTextTypesMap = new HashMap<>(4, 1.0f);
			unitTextTypesMap.put("DT", "Document Type"); // document type
			unitTextTypesMap.put("DC", "Document Category"); // document
																// category
			unitTextTypesMap.put("CN", "Issuer"); // client name

			// to set fields as required to fill
			List<Validator> required = new ArrayList<>();
			Validator validator = new Validator();
			validator.setType("req");
			required.add(validator);

			Input unitCodeArea = new Input();
			unitCodeArea.setValidators(required);
			unitCodeArea.setLabel("databaseUnit.codeField");
			unitCodeArea.setData("databaseUnit.codeField");
			unitCodeArea.setName("databaseUnit.codeField");
			unitCodeArea.setValue(databaseUnitCode);
			executionContext.getContextInstance().setVariable("databaseUnit.codeField", unitCodeArea);

			Input unitNameArea = new Input();
			unitNameArea.setValidators(required);
			unitNameArea.setLabel("databaseUnit.nameField");
			unitNameArea.setData("databaseUnit.nameField");
			unitNameArea.setValue(databaseUnitName);
			executionContext.getContextInstance().setVariable("databaseUnit.nameField", unitNameArea);

			Text errorMessage = new Text();
			StringBuilder errorMessageText = new StringBuilder(
					"Please, edit following property fields of your new " + unitTypesMap.get(databaseUnitType) + ": ");

			if ((!isCodeUnique) && (!isNameUnique)) {
				errorMessageText.append("code and name");
			} else if (!isCodeUnique) {
				errorMessageText.append("code");
			} else if (!isNameUnique) {
				errorMessageText.append("name");
			}

			errorMessage.setData(errorMessageText.toString());
			errorMessage.setLabel(errorMessageText.toString());
			executionContext.getContextInstance().setVariable("userMessage", errorMessage);

			executionContext.getContextInstance().setVariable("verdict", "retry");

		}

		executionContext.getToken().signal();

	}

}
