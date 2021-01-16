package loyer.util;

public interface RegCommand {
  // ************************** 2.1 上电注册****************************//
  /**
   * 1.1 主动上行 上电注册
   */
  public static final String REQ_REGISTER = "00";
  /**
   * 1.2 从动下行 注册失败
   */
  public static final String RES_REGISTER_ERROR = "01";
  /**
   * 1.3 从动下行 要求更新固件
   */
  public static final String RES_REGISTER_AND_UPDATE = "02";
  /**
   * 1.4 从动下行 注册成功
   */
  public static final String RES_REGISTER_SUCCESS = "03";
  // ************************** 2.2 固件更新 ****************************//
  /**
   * 2.1 主动上行 请求固件版本信息
   */
  public static final String REQ_VERSION = "04";
  /**
   * 2.2 从动下行 版本信息
   */
  public static final String REQ_CURRENT_VERSION = "05";
  /**
   * 2.3 主动上行 请求第N帧固件
   */
  public static final String REQ_REQUEST_FRAME = "06";
  /**
   * 2.4 从动下行 第N帧固件内容
   */
  public static final String RES_RESPONSE_FRAME = "07";
  /**
   * 2.5 主动上行 完成固件更新
   */
  public static final String REQ_UPDATE_FINSHED = "08";
  /**
   * 2.6 从动下行 响应收到设备完成更新
   */
  public static final String RES_UPDATE_FINEHED_REPONSE = "09";
  // ************************** 2.3 上报应用服务器连接情况(预留) ****************************//
  // ************************** 2.4 上报异常 ****************************//
  /**
   * 4.1 主动上行 上报异常
   */
  public static final String REQ_ERROR_REPORT = "E0";
  /**
   * 4.2 从动下行 响应收到异常上报
   */
  public static final String RES_ERROR_REPORT_RECEIVED = "E1";
}
