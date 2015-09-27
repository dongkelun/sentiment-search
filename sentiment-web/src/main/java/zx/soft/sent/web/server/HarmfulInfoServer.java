package zx.soft.sent.web.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.data.Protocol;

import zx.soft.sent.web.application.HarmfulInfoApplication;
import zx.soft.sent.web.utils.ReplaceConvert;
import zx.soft.utils.config.ConfigUtil;

/**
 * OA有害信息查询：hefei07
 * 根据关键词keywords查询最近7天的数据，并根据评分排序，返回num条结果。
 * 示例：http://localhost:4900/harmful/{keywords}/{num}
 *
 * 运行目录：/home/zxdfs/run-work/api/oa-harmful
 * 运行命令：cd sentiment-web
 *        bin/ctl.sh start harmfulInfoServer
 * 广西： gxqt4
 * 运行目录：/home/zxdfs/run-work/api/oa-harmful
 * 端口： 4900
 *
 * @author wanggang
 *
 */
public class HarmfulInfoServer {

	private final Component component;
	private final HarmfulInfoApplication harmfulInfoApplication;

	private final int PORT;

	public HarmfulInfoServer() {
		Properties props = ConfigUtil.getProps("web-server.properties");
		PORT = Integer.parseInt(props.getProperty("api.port"));
		component = new Component();
		harmfulInfoApplication = new HarmfulInfoApplication();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		HarmfulInfoServer specialServer = new HarmfulInfoServer();
		specialServer.start();
	}

	public void start() {
		component.getServers().add(Protocol.HTTP, PORT);
		try {
			component.getDefaultHost().attach("/harmful", harmfulInfoApplication);
			ReplaceConvert.configureJacksonConverter();
			component.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			component.stop();
			harmfulInfoApplication.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
