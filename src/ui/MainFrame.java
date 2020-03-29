package ui;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import core.Main;
import util.ShowUtils;

/**
 * 主界面
 * 
 */
public class MainFrame extends JFrame {

	/**
     * 程序界面宽度
     */
    public static final int WIDTH = 520;

    /**
     * 程序界面高度
     */
    public static final int HEIGHT = 415;
    /**
     * 打开串口
     */
    public static final String OPEN_TEXT = "打开串口";
    /**
     * 关闭串口
     */
    public static final String CLOSE_TEXT = "关闭串口";
    public static final String ASCII_MODE = "ASCII";
    public static final String HEX_BYTE_MODE = "BYTE";
    public static final String FILE_MODE = "FILE";

	private static MainFrame instance = null;
    
    //菜单栏
    private JMenuBar mBar = new JMenuBar();
    private JMenu systemOperator = new JMenu("系统管理");
    private JMenuItem user = new JMenuItem("用户管理");
    private JMenu database = new JMenu("数据库管理");
    private JMenuItem backup = new JMenuItem("数据库备份");
    private JMenuItem revert = new JMenuItem("数据库还原");
    private JMenuItem clear = new JMenuItem("数据库清除");
    private JMenuItem cardReader = new JMenuItem("读卡器管理");
    private JMenuItem close = new JMenuItem("关闭");

	private JPanel mainPanel = new JPanel();

	private JLabel pathLabel;
	
	private JLabel urlLabel = new JLabel("URL");;

	private JTextField urlInput = new JTextField();

	private JButton doDownLoad = new JButton("下载");

	private JButton closeBtn = new JButton("退出");

	private JTextField fileNameInput = new JTextField();

	private JLabel fileNameLabel = new JLabel("文件名");

	private JButton chooseFile = new JButton(new ImageIcon("chooseFile.png"));
    
	 private JTextArea dataView = new JTextArea();
	 
	 private JScrollPane scrollDataView = new JScrollPane(dataView);
	
    private MainFrame() {
        initView();
        initComponents();
        actionListener();
    }
	
	public static MainFrame getInstance(){
		if(instance == null){
			instance = new MainFrame();
		}
		return instance;
	}

	private void initView() {
    	try {
 		   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 		   SwingUtilities.updateComponentTreeUI(mBar);
		   SwingUtilities.updateComponentTreeUI(user);
		   SwingUtilities.updateComponentTreeUI(systemOperator);
		   SwingUtilities.updateComponentTreeUI(database);
		   SwingUtilities.updateComponentTreeUI(cardReader);
		   SwingUtilities.updateComponentTreeUI(backup);
		   SwingUtilities.updateComponentTreeUI(revert);
		   SwingUtilities.updateComponentTreeUI(clear);
		   //SwingUtilities.updateComponentTreeUI(close);
		   SwingUtilities.updateComponentTreeUI(doDownLoad);
		   SwingUtilities.updateComponentTreeUI(closeBtn);
		   SwingUtilities.updateComponentTreeUI(chooseFile);
		   SwingUtilities.updateComponentTreeUI(dataView);
 		} catch (Exception e1) {
 		   e1.printStackTrace();
 		}
        // 关闭程序
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        // 禁止窗口最大化
        setResizable(false);
        //ImageIcon icon=new ImageIcon("resources/icon.png");
		//setIconImage(icon.getImage());

        // 设置程序窗口居中显示
        Point p = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getCenterPoint();
        setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
        this.setLayout(null);
        
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("小说下载");
    }

    private void initComponents() {
		mainPanel.setBounds(0, 0, 510, 405);
		mainPanel.setLayout(null);
		
		/*pathLabel = new JLabel("URL");
		pathLabel.setBounds(20, 30, 50, 20);
		mainPanel.add(pathLabel);*/
		
		urlLabel.setBounds(20, 30, 50, 20);
		mainPanel.add(urlLabel);
        
        urlInput.setBounds(90, 25, 400, 30);
        mainPanel.add(urlInput);
        
        fileNameLabel.setBounds(20, 90, 50, 20);
        mainPanel.add(fileNameLabel);
        
        fileNameInput.setBounds(90, 85, 350, 30);
        mainPanel.add(fileNameInput);

        chooseFile.setFocusable(false);
        chooseFile.setBounds(450, 85, 30, 30);
        //chooseFile.setMargin(new Insets(0,0,0,0));
        mainPanel.add(chooseFile);
        
        dataView.setEditable(false);
        scrollDataView.setBounds(20, 130, 470, 160);
        mainPanel.add(scrollDataView);
        
        doDownLoad.setBounds(115, 300, 90, 25);
        mainPanel.add(doDownLoad);
        
        closeBtn.setBounds(325, 300, 90, 25);
        mainPanel.add(closeBtn);
      
        addToMenu(systemOperator,user);
        database.add(backup);
    	database.add(revert);
    	database.add(clear);
        addToMenu(systemOperator,database);
        addToMenu(systemOperator,cardReader);
        addToMenu(systemOperator,close);
        
        addToMenuBar(mBar,systemOperator);
        
        mBar.setBorder(new EmptyBorder(3, 5, 3, 5));
        setJMenuBar(mBar);
        
        add(mainPanel);
    }
    
    private void addToMenu(JMenu menu, JMenuItem menuItem) {
		menu.add(menuItem);
	}

	private void addToMenuBar(JMenuBar menuBar, JMenu menu) {
		if(menu.getItemCount()>0){
			menuBar.add(menu);
		}
	}

	private void actionListener() {
		closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
		
		doDownLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(fileNameInput.getText() == null || fileNameInput.getText().isEmpty() || !urlInput.getText().matches("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")) {
					ShowUtils.warningMessage("请输入URL!");
					return;
				}
				if(fileNameInput.getText() == null || fileNameInput.getText().isEmpty()) {
					ShowUtils.warningMessage("请输入文件名!");
					return;
				}
				Main.full_url = urlInput.getText();
				Main.fiction_name = fileNameInput.getText();
				
				new Thread() {
					@Override
					public void run() {
						Main.main(null);
					}
				}.start();
			}
		});
        
        chooseFile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
				JFileChooser jfc = new JFileChooser(".");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//jfc.setFileFilter(new BinFileFilter());
				int showDialog = jfc.showDialog(new JLabel(), "选择");
				
				File file = jfc.getSelectedFile();
				if (showDialog==JFileChooser.APPROVE_OPTION && file != null && file.isFile()) {
					fileNameInput.setText(file.getAbsolutePath());
				}
            }
        });
    }
	
	public void printLog(String logText) {
    	dataView.append(logText + "\r\n");
    	dataView.setCaretPosition(dataView.getText().length()); 
	}
	
	 public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	MainFrame.getInstance().setVisible(true);
            }
        });
    }
}