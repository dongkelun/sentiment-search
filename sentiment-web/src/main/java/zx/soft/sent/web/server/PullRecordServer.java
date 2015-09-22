package zx.soft.sent.web.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.data.Protocol;

import zx.soft.sent.web.application.PullRecordApplication;
import zx.soft.sent.web.utils.ReplaceConvert;
import zx.soft.utils.config.ConfigUtil;

/**
 *  从Mysql中根据id获取记录数据：hefei01
 *  示例：
 *       根据多个id、关键词、高亮标签查询数据：http://192.168.32.11:7900/sentiment/pull/ids/{ids}?keyword=XXXX&hlsimple=red
 *
 *  运行目录：/home/zxdfs/run-work/api/mysql
 *  运行命令：cd sentiment-web
 *         bin/ctl.sh start pullRecordServer
 *
 * @author wanggang
 *
 */
public class PullRecordServer {

	private final Component component;
	private final PullRecordApplication retriveRecordApplication;

	private final int PORT;

	public PullRecordServer() {
		Properties props = ConfigUtil.getProps("web-server.properties");
		PORT = Integer.parseInt(props.getProperty("api.port"));
		component = new Component();
		retriveRecordApplication = new PullRecordApplication();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		PullRecordServer server = new PullRecordServer();
		server.start();

	}

	public void start() {
		component.getServers().add(Protocol.HTTP, PORT);
		try {
			component.getDefaultHost().attach("/sentiment", retriveRecordApplication);
			ReplaceConvert.configureJacksonConverter();
			component.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			component.stop();
			retriveRecordApplication.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
