package com.uzassystem.workflow;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.dao.DatabaseMetadataDAO;
import com.openkm.dao.bean.DatabaseMetadataValue;
import com.openkm.frontend.client.util.metadata.DatabaseMetadataMap;
import com.openkm.util.DatabaseMetadataUtils;
import com.openkm.util.ISO8601;

/**
 * Create this meta-table before using:
 * 
 * -- LOGGER DELETE FROM OKM_DB_METADATA_TYPE WHERE DMT_TABLE='logger'; INSERT
 * INTO OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col00', 'log_date', 'date'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col01', 'log_class', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col02', 'log_proc_name', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col03', 'log_proc_def', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col04', 'log_proc_ins', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col05', 'log_task_name', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col06', 'log_task_def', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col07', 'log_task_ins', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col08', 'log_node', 'text'); INSERT INTO
 * OKM_DB_METADATA_TYPE (DMT_TABLE, DMT_REAL_COLUMN, DMT_VIRTUAL_COLUMN,
 * DMT_TYPE) VALUES ('logger', 'col09', 'log_msg', 'text');
 * 
 * @author pavila
 */
public class WorkflowLogger {
	private static Logger log = LoggerFactory.getLogger(WorkflowLogger.class);

	/**
	 * Log messages to database.
	 * 
	 * @param clazz
	 *            The class which produce the message.
	 * @param ctx
	 *            jBPM execution context.
	 * @param msg
	 *            Message to log.
	 * @param args
	 *            Optional message arguments.
	 * @throws Exception
	 *             If any error occurs.
	 */
	@SuppressWarnings("rawtypes")
	public static void log(Class clazz, ExecutionContext ctx, String msg, Object... args) {
		log(clazz.getCanonicalName(), ctx, msg, args);
	}

	/**
	 * Log messages to database.
	 * 
	 * @param clazz
	 *            The name of the class which produce the message.
	 * @param ctx
	 *            jBPM execution context.
	 * @param msg
	 *            Message to log.
	 * @param args
	 *            Optional message arguments.
	 * @throws Exception
	 *             If any error occurs.
	 */
	public static void log(String clazz, ExecutionContext ctx, String msg, Object... args) {
		try {
			ProcessDefinition procDef = ctx.getProcessDefinition();
			ProcessInstance procIns = ctx.getProcessInstance();
			Task task = ctx.getTask();
			TaskInstance taskIns = ctx.getTaskInstance();

			// A very simple implementation of format
			for (int i = 0; i < args.length; i++) {
				String delimiter = "{" + i + "}";

				while (msg.contains(delimiter)) {
					msg = msg.replace(delimiter, String.valueOf(args[i]));
				}
			}

			Map<String, String> logger = new HashMap<String, String>();
			logger.put(DatabaseMetadataMap.MV_NAME_TABLE, "logger");
			logger.put("log_date", ISO8601.formatBasic(Calendar.getInstance()));
			logger.put("log_class", clazz);
			logger.put("log_proc_name", procDef.getName() + " v" + procDef.getVersion());
			logger.put("log_proc_def", Long.toString(procDef.getId()));
			logger.put("log_proc_ins", Long.toString(procIns.getId()));

			if (task != null) {
				logger.put("log_task_name", task.getName());
				logger.put("log_task_def", Long.toString(task.getId()));
				logger.put("log_task_ins", Long.toString(taskIns.getId()));
			}

			logger.put("log_node", (String) ctx.getVariable("uuid"));
			logger.put("log_msg", msg);

			DatabaseMetadataValue dmv = DatabaseMetadataUtils.getDatabaseMetadataValueByMap(logger);
			DatabaseMetadataDAO.createValue(dmv);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
