package loyer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class TweleveHexMergeClient extends JPanel {

  private static final long serialVersionUID = -5433180308011989325L;
  /** 日志区域 */
  private JTextArea logArea;
  /** iap程序选择按扭 */
  private JButton iapFileButt;
  /** iap程序路径文本框 */
  private JTextField iapFileField;
  /** app程序路径文本框 */
  private JTextField appFileField;
  /** app程序选择按钮 */
  private JButton appFileButt;
  /** 设备编号文本框 */
  private JTextField deviceIDField;
  /** 固件版本文本框 */
  private JTextField versionField;
  /** 批量生成按钮 */
  private JButton multipleButt;
  /** 批量生成文本框 */
  private JTextField multipleField;
  /** 一键生成按钮 */
  private JButton submitButt;
  /** 复位按钮 */
  private JButton resetButt;
  /** 生成后Hex文件显示区域 */
  private JTextArea mergedArea;

  /** 生成后的hex文件路径 */
  static final String MERGED_FILE_PATH = "../十二口主板程序/";

  List<String> iapList = new ArrayList<>();
  List<String> appList = new ArrayList<>();
  Properties prop;
  Long id;
  File pathFile;

  /** 格式化日期显示 */
  static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
  /** 换行符 */
  static final String SEPARATOR = System.getProperty("line.separator");
  /** 自定义绿色 */
  static final Color GREEN = new Color(0, 204, 51);
  /** hex文件参数起始符 */
  static final String HEX_START_STR = ":02000004080EE4";
  //:20000000
  /** hex文件参数头 */
  static final String HEX_HEAD = ":20000000";
  /** hex文件起始校验符 */
  static final int CHECKSUM = 0x20;
  /** hex文件结束符 */
  static final String HEX_END_STR = ":00000001FF";

  public TweleveHexMergeClient(JFrame frame) {
    super();
    initialize(frame);
  }

  private void initialize(JFrame frame) {

    try {
      // 将界面风格设置成和系统一置
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    } // */
    JPanel mainPanel = new JPanel(new BorderLayout(5, 10));
    mainPanel.setBorder(new TitledBorder(new EtchedBorder(), "十二口主板Hex文件生成器", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 15), Color.BLACK));
    // =====================================================
    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    // =====================================================

    JPanel paramsPanel = new JPanel(new GridLayout(3, 1));
    paramsPanel.setBorder(new TitledBorder(new EtchedBorder(), "参数区域", TitledBorder.LEFT, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    mainPanel.add(paramsPanel, BorderLayout.NORTH);

    JPanel fileChoosePanel = new JPanel(new GridLayout(1, 2));
    paramsPanel.add(fileChoosePanel);
    // fileChoosePanel.setBorder(new TitledBorder(new EtchedBorder(), "Hex文件选择区域",
    // TitledBorder.LEFT, TitledBorder.TOP,
    // new Font("等线", Font.PLAIN, 13), Color.BLACK));
    // =================================================================================================================
    // iap区域
    JPanel iapFilePanel = new JPanel(new BorderLayout(5, 10));
    fileChoosePanel.add(iapFilePanel);
    iapFilePanel.setBorder(new TitledBorder(new EtchedBorder(), "iap文件区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    iapFileField = new JTextField();
    iapFileField.setEditable(false);
    iapFileButt = new JButton("IAP选择");
    FileDialog openIAPDialog = new FileDialog(frame, "打开iap文件", FileDialog.LOAD);
    openIAPDialog.setDirectory(".");
    iapFileButt.addActionListener(e -> {
      logBySelf("开始读取iap文件");
      iapList.clear();
      mergedArea.setText("");
      openIAPDialog.setVisible(true);
      if (openIAPDialog.getFile() != null && openIAPDialog.getFile() != "" && openIAPDialog.getDirectory() != null
          && openIAPDialog.getDirectory() != "") {

        String iapFileName = openIAPDialog.getDirectory() + openIAPDialog.getFile();
        iapFileField.setText(iapFileName);
        File file = new File(iapFileName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
          String s = null;
          while ((s = bufferedReader.readLine()) != null) {
            iapList.add(s);
            mergedArea.append(s + SEPARATOR);
          }
          logBySelf("iap文件读取成功");
        } catch (IOException e2) {
          e2.printStackTrace();
        }

      }
    });
    iapFilePanel.add(iapFileField, BorderLayout.CENTER);
    iapFilePanel.add(iapFileButt, BorderLayout.EAST);
    // app区域
    JPanel appFilePanel = new JPanel(new BorderLayout(5, 10));
    fileChoosePanel.add(appFilePanel);
    appFilePanel.setBorder(new TitledBorder(new EtchedBorder(), "app文件区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    appFileField = new JTextField();
    appFileField.setEditable(false);
    appFileButt = new JButton("APP选择");
    FileDialog openAPPDialog = new FileDialog(frame, "打开app文件", FileDialog.LOAD);
    openAPPDialog.setDirectory(".");
    appFileButt.addActionListener(e -> {
      logBySelf("开始读取app文件");
      appList.clear();
      mergedArea.setText("");
      openAPPDialog.setVisible(true);
      if (openAPPDialog.getFile() != null && openAPPDialog.getFile() != "" && openAPPDialog.getDirectory() != null
          && openAPPDialog.getDirectory() != "") {

        String appFileName = openAPPDialog.getDirectory() + openAPPDialog.getFile();
        appFileField.setText(appFileName);
        File file = new File(appFileName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
          String s = null;
          while ((s = bufferedReader.readLine()) != null) {
            appList.add(s);
            mergedArea.append(s + SEPARATOR);
          }
          logBySelf("app文件读取成功");
        } catch (IOException e2) {
          e2.printStackTrace();
        }
      }
    });
    appFilePanel.add(appFileField, BorderLayout.CENTER);
    appFilePanel.add(appFileButt, BorderLayout.EAST);
    // =================================================================================================================
    JPanel idAndVerPanel = new JPanel(new GridLayout(1, 3));
    paramsPanel.add(idAndVerPanel);
    // idAndVerPanel.setBorder(new TitledBorder(new EtchedBorder(), "编号及版本区域",
    // TitledBorder.LEFT, TitledBorder.TOP,
    // new Font("等线", Font.PLAIN, 13), Color.BLACK));

    // 设备编号区域
    JPanel deviceIDPanel = new JPanel(new BorderLayout());
    idAndVerPanel.add(deviceIDPanel);
    deviceIDPanel.setBorder(new TitledBorder(new EtchedBorder(), "设备编号区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    deviceIDField = new JTextField();
    deviceIDField.setEditable(false);
    deviceIDField.setForeground(Color.RED);
    deviceIDField.setFont(new Font("隶书", Font.BOLD | Font.ITALIC, 15));
    JButton deviceIDButt = new JButton("设备编号");
    deviceIDButt.setEnabled(false);
    deviceIDPanel.add(deviceIDField, BorderLayout.CENTER);
    deviceIDPanel.add(deviceIDButt, BorderLayout.WEST);
    // 固件版本区域
    JPanel versionPanel = new JPanel(new BorderLayout());
    idAndVerPanel.add(versionPanel);
    versionPanel.setBorder(new TitledBorder(new EtchedBorder(), "固件版本区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    versionField = new JTextField();
    versionField.setEditable(false);
    versionField.setForeground(GREEN);
    versionField.setFont(new Font("隶书", Font.BOLD | Font.ITALIC, 15));
    JButton versionButt = new JButton("固件版本");
    versionButt.setEnabled(false);
    versionPanel.add(versionField, BorderLayout.CENTER);
    versionPanel.add(versionButt, BorderLayout.WEST);
    // 批量生成区域
    JPanel multipilePanel = new JPanel(new BorderLayout());
    idAndVerPanel.add(multipilePanel);
    multipilePanel.setBorder(new TitledBorder(new EtchedBorder(), "批量生成区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    multipleField = new JTextField();
    multipleButt = new JButton("批量生成");
    multipleButt.addActionListener(e -> {
      generationMulti();
    });
    // multipleButt.setEnabled(false);
    multipilePanel.add(multipleField, BorderLayout.CENTER);
    multipilePanel.add(multipleButt, BorderLayout.WEST);
    // =================================================================================================================
    // 一键生成
    JPanel submitPanel = new JPanel(new BorderLayout(20, 5));
    paramsPanel.add(submitPanel);
    submitPanel.setBorder(new TitledBorder(new EtchedBorder(), "说明及生成区域", TitledBorder.CENTER, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    JLabel label = new JLabel("**生成的文件默认以'固件版本-设备编号.hex'保存**");
    label.setFont(new Font("楷体", Font.BOLD, 15));
    label.setForeground(Color.BLUE);
    submitButt = new JButton("一键生成");
    submitButt.addActionListener(e -> {
      if (checkFile()) {
        generationOne(true);
      }
    });
    submitPanel.add(label, BorderLayout.CENTER);
    submitPanel.add(submitButt, BorderLayout.WEST);
    // reset
    resetButt = new JButton("RESET");
    resetButt.addActionListener(e -> {
      reset();
    });
    submitPanel.add(resetButt, BorderLayout.EAST);
    // 生成后的Hex文件
    mergedArea = new JTextArea();
    mergedArea.setEditable(false);
    JScrollPane mergedPane = new JScrollPane(mergedArea);
    mergedPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    JPanel panel_1 = new JPanel(new BorderLayout());
    panel_1.add(mergedPane, BorderLayout.CENTER);
    panel_1.setBorder(new TitledBorder(new EtchedBorder(), "生成后的Hex文件内容", TitledBorder.LEFT, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    mainPanel.add(panel_1, BorderLayout.CENTER);

    // 日志区域
    logArea = new JTextArea();
    logArea.setRows(4);
    logArea.setForeground(GREEN);
    logArea.setBackground(Color.DARK_GRAY);
    logArea.setEditable(false);
    logArea.setFont(new Font("黑体", Font.PLAIN, 14));
    JScrollPane logPane = new JScrollPane(logArea);
    logPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    JPanel panel_2 = new JPanel(new BorderLayout());
    panel_2.add(logPane, BorderLayout.CENTER);
    panel_2.setBorder(new TitledBorder(new EtchedBorder(), "日志", TitledBorder.LEFT, TitledBorder.TOP,
        new Font("等线", Font.PLAIN, 13), Color.BLACK));
    mainPanel.add(panel_2, BorderLayout.SOUTH);

    // 加载配置文件
    prop = new Properties();
    loadLocalParams();

    // 创建到处文件目录
    pathFile = new File(MERGED_FILE_PATH);
    if (!pathFile.isDirectory()) {
      pathFile.mkdirs();
    }
    try {
      logBySelf("生成的文件目录为" + pathFile.getCanonicalPath() + "\\");
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * 批量生成
   */
  private void generationMulti() {
    String s = multipleField.getText();
    if (s == null || s.length() < 1) {
      JOptionPane.showMessageDialog(null, "请输入批量生成的数量!");
      return;
    }
    int count = Integer.parseInt(s);
    if (checkFile()) {
      for (int i = 0; i < count; i++) {
        generationOne(false);
      }
    }
    logBySelf("共生成" + count + "个文件!");
  }

  /**
   * 校验iap和app文件
   * 
   * @return
   */
  private boolean checkFile() {
    // 校验数据
    String iapFileName = iapFileField.getText().toLowerCase();
    String appFileName = appFileField.getText().toLowerCase();
    if (!(iapFileName.contains("iap") && iapFileName.contains(".hex"))) {
      JOptionPane.showMessageDialog(null, "请选择正确的IAP程序");
      return false;
    }
    if (!(appFileName.contains("twe") && appFileName.contains(".hex"))) {
      JOptionPane.showMessageDialog(null, "请选择正确的APP程序");
      return false;
    }
    return true;
  }

  /**
   * 一键生成
   */
  private void generationOne(boolean displayLog) {
    //获取设备ID和软件版本号字符串
    int checkSum = CHECKSUM;
    String idStr = deviceIDField.getText();
    byte[] ids = getAscii(idStr);
    for (int i = 0; i < ids.length; i++) {
      checkSum += ids[i];
    }
    byte[] vers = new byte[24];
    byte[] versAscs = getAscii(versionField.getText());
    for (int i = 0; i < 24; i++) {
      if (i < versAscs.length) {
        vers[i] = versAscs[i];
      } else {
        vers[i] = 0x00;
      }
      checkSum += vers[i];
    }
    //计算出总的校验和
    checkSum = (0x100 - (checkSum & 0xFF)) & 0xFF;
    //生成总的参数字符串
    String hexStr = HEX_HEAD + bytesToHexStr(ids) + bytesToHexStr(vers) + String.format("%02X", checkSum);
    id += 1;
    prop.setProperty("TWELEVEid", id + "");
    try (FileOutputStream fos = new FileOutputStream(new File("info.properties"))) {
      prop.store(fos, "");
    } catch (IOException e) {
      e.printStackTrace();
    }
    deviceIDField.setText("PM" + id);
    String hexFileName = versionField.getText() + "-" + idStr + ".hex";
    File file = new File(pathFile, hexFileName);
    if (file.exists()) {
      file.delete();
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try (PrintWriter pw = new PrintWriter(file)) {
      for (int i = 0; i < iapList.size() - 1; i++) {
        pw.println(iapList.get(i));
      }
      for (int i = 0; i < appList.size() - 1; i++) {
        pw.println(appList.get(i));
      }
      pw.println(HEX_START_STR);
      pw.println(hexStr);
      pw.println(HEX_END_STR);
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    if (displayLog) {
      logBySelf("成功生成" + hexFileName);
    }
  }

  /**
   * 获取字符串的Ascii形式
   * 
   * @param str
   * @return
   */
  private byte[] getAscii(String str) {
    int len = str.length();
    byte[] resBuf = new byte[len];
    for (int i = 0; i < len; i++) {
      resBuf[i] = (byte) str.charAt(i);
    }
    return resBuf;
  }

  /**
   * 将字节数组转换成16进制字符串
   * 
   * @param datas
   * @return
   */
  private String bytesToHexStr(byte[] datas) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < datas.length; i++) {
      String hex = Integer.toHexString(datas[i] & 0xFF);
      if (hex.length() < 2) {
        sb.append(0);
      }
      sb.append(hex);
      // sb.append(' ');
    }
    return sb.toString().toUpperCase();
  }

  /**
   * 载入本地参数
   */
  private void loadLocalParams() {
    try {
      prop.load(new FileInputStream(new File("info.properties")));
    } catch (IOException e3) {
      e3.printStackTrace();
    }
    id = Long.parseLong(prop.getProperty("TWELEVEid"));
    deviceIDField.setText("PM" + id);
    System.out.println("当前设备ID=PM" + id);
    String ver = prop.getProperty("TWELEVEversion");
    versionField.setText(ver);
    System.out.println("当前固件版本号=" + ver);
  }

  /**
   * 复位参数
   */
  private void reset() {
    iapFileField.setText("");
    appFileField.setText("");
    // deviceIDField.setText("");
    // versionField.setText("");
    multipleField.setText("");
    mergedArea.setText("");
    logArea.setText("");
    loadLocalParams();
  }

  /**
   * 添加运行时日志
   * 
   * @param info
   */
  public void logBySelf(String info) {
    logArea.append(dateFormat.format(new Date()) + "[INFO] " + info + SEPARATOR);
  }
}
