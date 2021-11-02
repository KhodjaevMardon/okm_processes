/**
 * 
 */
package com.uzassystem.workflow.deletedoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.Input;
import com.openkm.core.WorkflowException;
import com.openkm.util.JBPMUtils;
import com.openkm.util.WorkflowUtils;

/**
 * @author openkm
 *
 */
public class CheckPI implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3545345611979810844L;

	@Override
	public void execute(ExecutionContext execCtxt) throws Exception {

		String docUUID = ((Input) execCtxt.getContextInstance().getVariable("Doc_UUID")).getValue();
		String processInstanceFieldName = ((Input) execCtxt.getContextInstance().getVariable("fieldName")).getValue();

		HashMap<String, List<String>> processInstancesMap = new HashMap<>();
		StringBuilder idsBuilderText = new StringBuilder();
		int processInstancesCount = 0;
		processInstancesMap = findProcessInstancesByVariableValue(processInstanceFieldName, docUUID);

		for (Entry<String, List<String>> entry : processInstancesMap.entrySet()) {
			idsBuilderText.append(entry.getKey() + " : ");
			for (String id : entry.getValue()) {
				idsBuilderText.append(id + " ");
				processInstancesCount++;
			}
			idsBuilderText.append("\n");
		}

		String idsStr = String.valueOf(idsBuilderText);

		execCtxt.getContextInstance().setVariable("processInstancesIds", idsStr);

		execCtxt.getContextInstance().setVariable("processInstancesCount", String.valueOf(processInstancesCount));
		// this shit will give you 'hibernate commit failed' error. DONT SAVE
		// PROCESS INSTANCES LIST TO EXECUTIONCONTEXT.
		// execCtxt.getContextInstance().setVariable("processInstancesFound",
		// processInstancesList);

		HashMap<String, List<String>> unnecessaryProcessInstancesMap = new HashMap<>();
		StringBuilder badIdsBuilderText = new StringBuilder();
		int unnecessaryProcessInstancesCount = 0;
		unnecessaryProcessInstancesMap = findProcessInstancesByVariableValue("uuid", docUUID);

		for (Entry<String, List<String>> entry : unnecessaryProcessInstancesMap.entrySet()) {
			badIdsBuilderText.append(entry.getKey() + " : ");
			for (String id : entry.getValue()) {
				badIdsBuilderText.append(id + " ");
				unnecessaryProcessInstancesCount++;
			}
			badIdsBuilderText.append("\n");
		}

		String badIdsStr = String.valueOf(badIdsBuilderText);

		execCtxt.getContextInstance().setVariable("badProcessInstancesIds", badIdsStr);

		execCtxt.getContextInstance().setVariable("badProcessInstancesCount",
				String.valueOf(unnecessaryProcessInstancesCount));
		// this shit will give you 'hibernate commit failed' error. DONT SAVE
		// PROCESS INSTANCES LIST TO EXECUTIONCONTEXT.
		// execCtxt.getContextInstance().setVariable("badProcessInstancesFound",
		// badProcessInstancesList);

		execCtxt.getToken().signal();

	}

	@SuppressWarnings("rawtypes")
	public static HashMap<String, List<String>> findProcessInstancesByVariableValue(String variable, String value)
			throws WorkflowException {
		Logger log = LoggerFactory.getLogger(WorkflowUtils.class);
		log.debug("findProcessInstanceByNode({})", value);

		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		HashMap<String, List<String>> resultMap = new HashMap<>();
		// List<ProcessInstance> al = new ArrayList<ProcessInstance>();

		try {
			if (value != null) {
				GraphSession graphSession = jbpmContext.getGraphSession();
				List procDefList = graphSession.findAllProcessDefinitions();

				for (Iterator itPd = procDefList.iterator(); itPd.hasNext();) {
					ProcessDefinition procDef = (org.jbpm.graph.def.ProcessDefinition) itPd.next();
					List procInsList = graphSession.findProcessInstances(procDef.getId());

					for (Iterator itPi = procInsList.iterator(); itPi.hasNext();) {
						org.jbpm.graph.exe.ProcessInstance procIns = (org.jbpm.graph.exe.ProcessInstance) itPi.next();

						if (value.equals(procIns.getContextInstance().getVariable(variable))) {
							String key = String.valueOf(procDef.getId()) + " " + procDef.getName();
							if (!resultMap.containsKey(key)) {
								resultMap.put(key, new ArrayList<String>());
							}
							resultMap.get(key).add(String.valueOf(procIns.getId()));

							// al.add(WorkflowUtils.copy(procIns));
						}
					}
				}
			}
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			jbpmContext.close();
		}

		log.debug("findProcessInstanceByNode: {}", resultMap);

		return resultMap;
		// return al;
	}

}
