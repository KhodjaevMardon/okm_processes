package com.uzassystem.workflow.documentRemoval;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.util.JBPMUtils;
import com.openkm.util.WorkflowUtils;
import com.openkm.bean.form.Input;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.core.WorkflowException;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.GraphSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.graph.def.ProcessDefinition;

import java.util.*;
//import java.util.Map.Entry;

public class ProcessInstanceCheck implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext execCtxt) throws Exception {
		// I
		String docUUID = ((Input) execCtxt.getContextInstance().getVariable("Doc_UUID")).getValue();

		List<ProcessInstance> processInstancesList = new ArrayList<>();
		processInstancesList = findProcessInstancesByVariableValue("docUUID", docUUID);

		execCtxt.getContextInstance().setVariable("processInstancesCount", String.valueOf(processInstancesList.size()));

		execCtxt.getToken().signal();
	}

	/**
	 * Get process instances, requested variable of which has requested value.
	 */
	@SuppressWarnings("rawtypes")
	public static List<ProcessInstance> findProcessInstancesByVariableValue(String variable, String value)
			throws WorkflowException {
		Logger log = LoggerFactory.getLogger(WorkflowUtils.class);
		log.debug("findProcessInstanceByNode({})", value);

		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<ProcessInstance> al = new ArrayList<>();

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
							al.add(WorkflowUtils.copy(procIns));
						}
					}
				}
			}
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			jbpmContext.close();
		}

		log.debug("findProcessInstanceByNode: {}", al);
		return al;
	}

}