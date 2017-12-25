package com.bluedon.job;


import com.bluedon.ETLServer.ServerUtils;
import com.bluedon.ETLServer.SpringUtils;
import com.bluedon.esinterface.config.ESClient;
import com.bluedon.esinterface.search.ESSearchUitls;
import com.bluedon.util.CalendarTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
public class ESDataToPgJob implements Runnable{
	private final static Logger logger = Logger.getLogger(ESDataToPgJob.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String indexName;
	private String typeName;
	//ES中用于查询的过滤字段
	private String filterField;

	public ESDataToPgJob(String indexName, String typeName, String filterField) {
		this.indexName = indexName;
		this.typeName = typeName;
		this.filterField = filterField;
	}

	public ESDataToPgJob() {
	}

	/*
	多线程执行job:3个线程分别去执行：查询ES，然后插入PG
	 */
	//@Scheduled(cron = "*/5 * * * * ?")
	public void multiThreadJob(){
		//原始日志
		String indexName1 = "syslog";
		String typeName1 = "syslog";
		String filterField1 = "storagetime";
		ESDataToPgJob job1 = new ESDataToPgJob(indexName1, typeName1, filterField1);

		//范式化日志
		String indexName2 = "genlog";
		String typeName2 = "genlog";
		String filterField2 = "firstrecvtime";
		ESDataToPgJob job2 = new ESDataToPgJob(indexName2, typeName2, filterField2);

		//单事件event
		String indexName3 = "event";
		String typeName3 = "event";
		String filterField3 = "opentime";
		ESDataToPgJob job3 = new ESDataToPgJob(indexName3, typeName3, filterField3);

		//开始执行线程
		//new Thread(job1,"syslog-thread").start();
		new Thread(job2,"genlog-thread").start();
		//new Thread(job3,"event-thread").start();
	}

	/**
	 * 真正的执行查询，插入数据的逻辑
	 */
	@Override
	public void run() {
		//1.获取查询条件
		JSONObject conditions = this.getCondition4ES(this.filterField);
		//2.查询ES数据，并插入PG
		this.insertESData2PG(this.indexName, this.typeName, conditions);
	}

	/**
	 * 读取ES的数据到PG
	 */
	public void insertESData2PG(String indexName, String typeName, JSONObject condition) {
		String threadName = Thread.currentThread().getName();
		long starttime = System.currentTimeMillis();

		try {
			//1.读取ES的数据
			logger.info("####### "+threadName+" 开始读取ES #######");
			//Client client = ESClient.esClient();
			//List<Map> dataList = ESSearchUitls.searchAllData(client, indexName, typeName, condition);
			//logger.info("####### "+threadName+" 结束读取ES;读取size="+dataList.size()+";spend time="+(System.currentTimeMillis()-starttime)/1000);

			//测试数据
			Map m1 = new HashMap();
			m1.put("dublecount",1);
			m1.put("recordid","recordid_1");
			m1.put("storagetime",1510369871000L);

			Map m11 = new HashMap();
			m11.put("dublecount",1);
			m11.put("recordid","recordid_2");
			m11.put("storagetime",1510369875000L);

			Map m111 = new HashMap();
			m111.put("dublecount",1);
			m111.put("recordid","recordid_3");
			m111.put("storagetime",1510369895000L);

			Map m4 = new HashMap();
			m4.put("dublecount",1);
			m4.put("recordid","recordid_4");
			m4.put("storagetime",1510369855000L);

			Map m5 = new HashMap();
			m5.put("dublecount",1);
			m5.put("recordid","recordid_5");
			m5.put("storagetime",1510369851000L);

			List<Map> dataList = new ArrayList();
			dataList.add(m1);
			dataList.add(m11);
			dataList.add(m111);
			dataList.add(m4);
			dataList.add(m5);



			//2.写入到PG
			logger.info("####### "+threadName+" 开始插入Pg #######");
			if(dataList!=null&&dataList.size()>0){
				//从配置文件中获取sql和插入字段
				Properties properties = ServerUtils.getProperties();
				String sql = properties.getProperty(indexName+"_sql");
				String insert_fields = properties.getProperty(indexName+"_fields");

				sql = sql.replace("$insert_fields$", insert_fields);
				String[] filedsArr = insert_fields.split(",");

				List paramsList = new ArrayList<String>();
				for(Map m : dataList){
					String oneRowStart = "( ";
					for(int i=0;i<filedsArr.length;i++){
						String field = filedsArr[i];
						Object insertVal = m.get(field);
						if(insertVal!=null){
							if(field.contains("time")){
								Long timestamp = (Long)insertVal;
								insertVal = " to_timestamp('" + CalendarTimeUtil.getFullTimeStampTime(timestamp) + "','yyyy-MM-dd HH24:MI:SS')";
							}

							if(i!=(filedsArr.length-1)){
								oneRowStart += "'"+insertVal+"',";
							}else{
								oneRowStart += insertVal;
							}
						}
					}
					String oneRowEnd = " )";
					String oneRowData = oneRowStart + oneRowEnd;
					paramsList.add(oneRowData);
				}

				//插入到PG
				this.batchInsert2PG_2(sql, paramsList);
			}
			long endtime = System.currentTimeMillis();
			logger.info("####### "+threadName+" 结束插入Pg #######spend time="+(endtime-starttime)/1000+" #######");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/*
	获取ES的查询条件
	 */
	public JSONObject getCondition4ES(String filterField){
		Long currentMilTimes = System.currentTimeMillis();
		Long oneHourBeforeMilTimes = System.currentTimeMillis()-3600000L;

		//构造ES的查询条件
		JSONArray conditions = new JSONArray();

		JSONObject condition1 = new JSONObject();
		condition1.put("field", filterField);
		condition1.put("type", "date");
		condition1.put("op", "gte");
		//condition1.put("value", currentMilTimes);
		condition1.put("value", 1508309701000L);

		JSONObject condition2 = new JSONObject();
		condition2.put("field", filterField);
		condition2.put("type", "date");
		condition2.put("op", "lte");
		//condition2.put("value", oneHourBeforeMilTimes);
		condition2.put("value", 1508309701000L);

		conditions.add(condition1);
		conditions.add(condition2);

		JSONObject retCondition = new JSONObject();
		retCondition.put("conditions", conditions);
		return retCondition;
	}

	/*
	批量插入数据到PG
	 */
	public void batchInsert2PG(String sql, List<Object[]> list) {
		if(list!=null&&list.size()>0){
			JdbcTemplate jdbc = (JdbcTemplate) SpringUtils.getBean("jdbcTemplate");
			jdbc.batchUpdate(sql, list);
			//jdbcTemplate.batchUpdate(sql, list);
		}
	}

	/*
	批量插入数据到PG
    */
	public void batchInsert2PG_2(String sql, List<String> listData) {

		//执行插入的次数
		int insertCnt = 100;
		//每次插入的记录数
		int batchCnt = listData.size()/insertCnt;
		for(int i=0;i<=insertCnt;i++){
			String executeSql = sql;
			int startIndex = i*batchCnt;
			int endIndex = (i+1)*batchCnt;
			if(endIndex>listData.size()){
				endIndex=listData.size();
			}

			List batchList = listData.subList(startIndex, endIndex);
			String params = StringUtils.join(batchList.toArray(),",");
			executeSql = sql + params;
			System.out.println(executeSql);
			JdbcTemplate jdbc = (JdbcTemplate) SpringUtils.getBean("jdbcTemplate");
			jdbc.execute(sql);
		}

	}

	public static void main(String[] args){
		System.out.println("----------------");
		try{
			logger.error("启动ETL服务器");
			//SpringUtils.init();//初始化SPRING容器

			Thread.sleep(2000);
			new ServerUtils().getConfig();
			new ESDataToPgJob().multiThreadJob();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
