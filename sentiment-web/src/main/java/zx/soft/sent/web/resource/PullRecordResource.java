package zx.soft.sent.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Parameter;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.sent.common.domain.ErrorResponse;
import zx.soft.sent.dao.domain.sentiment.RecordSelect;
import zx.soft.sent.web.application.PullRecordApplication;
import zx.soft.utils.codec.URLCodecUtils;

/**
 * 提取记录资源类
 *
 * @author wanggang
 *
 */
public class PullRecordResource extends ServerResource {

	private static Logger logger = LoggerFactory.getLogger(PullRecordResource.class);

	private static PullRecordApplication application;

	private String ids;
	private String keyword = ""; // 逗号分割
	private String hlsimple = "";

	@Override
	public void doInit() {
		logger.info("Request Url: " + URLCodecUtils.decoder(getReference().toString(), "utf-8") + ".");
		ids = (String) getRequest().getAttributes().get("ids");
		application = (PullRecordApplication) getApplication();
		for (Parameter p : getRequest().getResourceRef().getQueryAsForm()) {
			if ("keyword".equalsIgnoreCase(p.getName())) {
				keyword = p.getValue();
			} else if ("hlsimple".equalsIgnoreCase(p.getName())) {
				hlsimple = p.getValue();
			}
		}
	}

	@Get("json")
	public Object getRecords() {
		logger.info("{params:[ids=" + ids + ",keyword=" + keyword + ",hlsimple=" + hlsimple + "]}");
		if (ids == null || ids.length() == 0) {
			return new ErrorResponse.Builder(20003, "your query params is illegal.").build();
		}
		List<RecordSelect> result = application.getRecords(ids);
		if (keyword.length() != 0 && hlsimple.length() != 0) {
			List<RecordSelect> result1 = new ArrayList<>();
			// 添加高亮标签
			RecordSelect temp;
			for (RecordSelect record : result) {
				temp = record;
				for (String key : keyword.split(",")) {
					temp.setContent(record.getContent().replaceAll(key,
							"<" + hlsimple + ">" + key + "</" + hlsimple + ">"));
					temp.setTitle(record.getTitle().replaceAll(key, "<" + hlsimple + ">" + key + "</" + hlsimple + ">"));
				}
				result1.add(temp);
			}
		}

		return result;
	}

}
