package zx.soft.sent.solr.insight;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.sent.common.insight.AreaCode;
import zx.soft.sent.common.insight.HbaseConstant;
import zx.soft.sent.common.insight.TrueUserHelper;
import zx.soft.sent.common.insight.UserDomain;
import zx.soft.sent.common.insight.Virtuals.Virtual;
import zx.soft.sent.core.domain.QueryParams;
import zx.soft.sent.core.hbase.HBaseUtils;
import zx.soft.sent.solr.domain.QueryResult;
import zx.soft.sent.solr.query.QueryCore;
import zx.soft.utils.checksum.CheckSumUtils;
import zx.soft.utils.time.TimeUtils;

public class RelationCache {

	private static Logger logger = LoggerFactory.getLogger(RelationCache.class);
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				HBaseUtils.close();
			}
		});
	}

	public RelationCache() {

	}

	public static void main(String[] args) {
		RelationCache relationCache = new RelationCache();
		relationCache.run();
	}

	private void run() {
		logger.info("Starting generate data...");
		try {
			HBaseUtils.createTable(HbaseConstant.TABLE_NAME, new String[] { HbaseConstant.FAMILY_NAME });
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
		long endTime = System.currentTimeMillis();
		long startTime = getStartTime(endTime, -30);
		String timeFilter = "lasttime:[" + TimeUtils.transToSolrDateStr(startTime) + " TO "
				+ TimeUtils.transToSolrDateStr(endTime) + "]";
		for (AreaCode area : AreaCode.values()) {
			String areaCode = area.getAreaCode();
			List<UserDomain> trueUsers = TrueUserHelper.getTrueUsers(areaCode);
			for (UserDomain user : trueUsers) {
				String trueUserId = user.getTureUserId();
				List<Virtual> virtuals = TrueUserHelper.getVirtuals(trueUserId);
				for (Virtual virtual : virtuals) {
					cacheHalfHourRelation(virtual, timeFilter);
				}
			}
		}
		// 关闭资源
		QueryCore.getInstance().close();
		logger.info("Finishing query OA-FirstPage data...");
	}

	private void cacheHalfHourRelation(Virtual virtual, String timeFilter) {
		QueryParams params = new QueryParams();
		params.setQ("*:*");
		params.setFq(timeFilter);
		params.setFq(params.getFq() + ";" + "(nickname:\"" + virtual.getNickname() + "\" AND source_id:"
				+ virtual.getSource_id() + ")");
		params.setRows(200);
		QueryResult result = QueryCore.getInstance().queryData(params, false);
		cacheOneBlogRelation(result, virtual);
		long numFound = result.getNumFound();
		for (int i = 200; i < numFound; i += 200) {
			params.setStart(i);
			params.setRows(200);
			result = QueryCore.getInstance().queryData(params, false);
			cacheOneBlogRelation(result, virtual);
		}

	}

	private void cacheOneBlogRelation(QueryResult result, Virtual virtual) {
		QueryParams params = new QueryParams();
		params.setQ("*:*");
		params.setRows(200);
		for (SolrDocument doc : result.getResults()) {
			params.setFq("original_uid:" + doc.getFieldValue("id").toString());
			QueryResult tmp = QueryCore.getInstance().queryData(params, false);
			for (SolrDocument document : tmp.getResults()) {
				byte[] md5 = CheckSumUtils.md5sum(virtual.getTrueUser());
				byte[] uuid = CheckSumUtils.md5sum(UUID.randomUUID().toString());
				byte[] rowKey = new byte[CheckSumUtils.MD5_LENGTH * 2];
				int offset = 0;
				offset = Bytes.putBytes(rowKey, offset, md5, 0, md5.length);
				offset = Bytes.putBytes(rowKey, offset, uuid, 0, md5.length);

				Put put = new Put(rowKey);
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.TRUE_USER),
						Bytes.toBytes(virtual.getTrueUser()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.TIMESTAMP),
						Bytes.toBytes(TimeUtils.transTimeLong((String) doc.getFieldValue("timestamp"))));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.VIRTUAL),
						Bytes.toBytes(virtual.getNickname()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.PLATFORM),
						Bytes.toBytes(virtual.getPlatform()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.SOURCE_ID),
						Bytes.toBytes(virtual.getSource_id()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.ID),
						Bytes.toBytes(doc.getFieldValue("id").toString()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.COMMENT_USER),
						Bytes.toBytes(document.getFieldValue("nickname").toString()));
				put.add(Bytes.toBytes(HbaseConstant.FAMILY_NAME), Bytes.toBytes(HbaseConstant.COMMENT_TIME),
						Bytes.toBytes(document.getFieldValue("timestamp").toString()));
				HBaseUtils.put(HbaseConstant.TABLE_NAME, put);
			}
		}

	}

	private long getStartTime(long currentTime, int gapMin) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(currentTime);
		date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.MINUTE) + gapMin);
		return date.getTimeInMillis();
	}

}
