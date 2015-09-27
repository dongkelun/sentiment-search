package zx.soft.sent.web.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.data.Protocol;

import zx.soft.sent.web.application.FirstPageApplication;
import zx.soft.sent.web.utils.ReplaceConvert;
import zx.soft.utils.config.ConfigUtil;

/**
 * OA首页查询缓存服务：hefei07
 * 示例：http://localhost:5901/firstpage/{type}/{datestr}
 *
 * 运行目录：/home/zxdfs/run-work/api/oa-firstpage
 * 运行命令：cd sentiment-web
 *        bin/ctl.sh start firstPageServer
 *
 * 广西： gxqt4
 * 运行目录：/home/zxdfs/run-work/api/oa-firstpage
 * 端口：5901
 *
 * @author wanggang
 *
 */
public class FirstPageServer {

	private final Component component;
	private final FirstPageApplication firstPageApplication;

	private final int PORT;

	public FirstPageServer() {
		Properties props = ConfigUtil.getProps("web-server.properties");
		PORT = Integer.parseInt(props.getProperty("api.port"));
		component = new Component();
		firstPageApplication = new FirstPageApplication();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		FirstPageServer specialServer = new FirstPageServer();
		specialServer.start();
	}

	public void start() {
		component.getServers().add(Protocol.HTTP, PORT);
		try {
			component.getDefaultHost().attach("/firstpage", firstPageApplication);
			ReplaceConvert.configureJacksonConverter();
			component.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			component.stop();
			firstPageApplication.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
