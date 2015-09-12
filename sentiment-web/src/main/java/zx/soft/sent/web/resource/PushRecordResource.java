package zx.soft.sent.web.resource;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.sent.common.index.RecordInfo;
import zx.soft.sent.core.domain.ErrorResponse;
import zx.soft.sent.web.application.PushRecordApplication;
import zx.soft.utils.codec.URLCodecUtils;

/**
 * 存储记录资源类
 *
 * @author wanggang
 *
 */
public class PushRecordResource extends ServerResource {

	private static Logger logger = LoggerFactory.getLogger(PushRecordResource.class);

	private static PushRecordApplication application;

	@Override
	public void doInit() {
		logger.info("Request Url: " + URLCodecUtils.decoder(getReference().toString(), "utf-8") + ".");
		application = (PushRecordApplication) getApplication();
	}

	@Get("json")
	public Object postRecords(List<RecordInfo> records) {
		application.pushRecords(records);
		return new ErrorResponse.Builder(0, "ok").build();
	}

}
