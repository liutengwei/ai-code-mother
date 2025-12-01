package org.example.ltwaicodemother.constant;

/**
 * app常量 - 优先级区分开方便拓展
 *
 */
public interface AppConstant {

    Integer GOOD_APP_PRIORITY = 99;

    Integer DEFAULT_APP_PRIORITY = 1;

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";

}
