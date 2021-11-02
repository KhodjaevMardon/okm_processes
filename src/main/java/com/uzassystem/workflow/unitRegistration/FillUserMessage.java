package com.uzassystem.workflow.unitRegistration;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.Select;
import com.openkm.bean.form.Text;
import com.openkm.bean.form.Input;

public class FillUserMessage implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// In this class we initiate nearly all the form items in Unit
		// Properties Verification

		// if we are repeating, uncheck the repeat flag
		executionContext.getContextInstance().setVariable("to repeat", "false");

		// get the unit type to use its name later
		Select databaseUnitTypeField = (Select) executionContext.getContextInstance()
				.getVariable("okp:databaseUnit.type");
		String databaseUnitType = databaseUnitTypeField.getValue();

		Map<String, String> unitTypesMap = new HashMap<>(4, 1.0f);
		unitTypesMap.put("DT", "Document Type"); // document type
		unitTypesMap.put("DC", "Document Category"); // document category
		unitTypesMap.put("CN", "Issuer"); // client name

		String message = "Please, enter properties of new " + unitTypesMap.get(databaseUnitType)
				+ " in the following fields.";

		// set the message above the input fields
		Text userMessage = new Text();
		userMessage.setData(message);
		userMessage.setLabel(message);

		executionContext.getContextInstance().setVariable("userMessage", userMessage);

		// set Input fields
		Input unitCodeArea = new Input();
		unitCodeArea.setLabel("databaseUnit.codeField");
		unitCodeArea.setData("databaseUnit.codeField");
		unitCodeArea.setName("databaseUnit.codeField");
		unitCodeArea.setValue("");
		executionContext.getContextInstance().setVariable("databaseUnit.codeField", unitCodeArea);

		Input unitNameArea = new Input();
		unitNameArea.setLabel("databaseUnit.codeField");
		unitNameArea.setData("databaseUnit.nameField");
		unitNameArea.setValue("");
		executionContext.getContextInstance().setVariable("databaseUnit.nameField", unitNameArea);
	}

}