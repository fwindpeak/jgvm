package eastsun.jgvm.module;

import eastsun.jgvm.module.event.ScreenChangeListener;

/**
 * 规定实现GVM所需的接口.注意,除了getConfig,其它方法都不是线程安全的<p>
 * 可以使用GVM加载LavApp,然后逐步执行<p>
 * 可以通过setInputMethod方法设置GVM使用的输入法,默认GVM不带输入法,并使用KeyModel.getchar()方法替代输入法功能<p>
 * 下面是使用GVM的一种可能的方式:
 * <p><hr><blockquote><pre>
 *     ...
 *     gvm =JGVM.newGVM(config,fileModel,keyMode);
 *     app =LavApp.createLavApp(source);
 *     Thread t =new Thread(new Runnable(){
 *          public void run(){
 *              try{
 *                  gvm.loadApp(app);
 *                  while(!(isInterrupted()||gvm.isEnd())){
 *                      gvm.nextStep();
 *                  }
 *              }catch(IllegalStateException ise){
 *                 //do something
 *              }catch(InterruptedException ie){
 *                 //do something
 *              }finally{
 *                 gvm.dispose();
 *              }
 *          }
 *      });
 *      t.start();
 * </pre></blockquote><hr><p>
 * 如果想强制退出一个执行中的gvm,调用运行这个gvm的线程t的interrupt()方法<p>
 * @author Eastsun
 * @version 2008-1-14
 */
public abstract class JGVM {

    /**
     * 工厂方法,通过给定的配置得到一个GVM
     * @param config 配置
     * @return 一个新的GVM实例
     * @exception IllegalStateException 不支持该配置的GVM
     */
    public static JGVM newGVM(GvmConfig config, FileModel fileModel, KeyModel keyModel) throws IllegalStateException {
        //当前实现忽略GvmConfig的version参数,总是返回一个GVM1.0的实例
        return new DefaultGVM(config, fileModel, keyModel);
    }

    /**
     * 载入一个app,并做适当的初始化<p>
     * 如果已经加载,则释放之前的app<p>
     * @param app 需要加载的app
     * @throws IllegalStateException 不支持的app
     */
    public abstract void loadApp(LavApp app) throws IllegalStateException;

    /**
     * 卸载运行的app,并释放其占用的资源
     */
    public abstract void dispose();

    /**
     * 执行下一个指令,该方法可能会阻塞
     * @throws java.lang.IllegalStateException 程序已经结束或不支持的操作
     * @throws InterruptedException 执行期间被其他线程中断
     */
    public abstract void nextStep() throws IllegalStateException, InterruptedException;

    /**
     * 程序是否正常结束,如果没有加载app,总是返回true
     * @return 程序运行是否已经正常结束
     */
    public abstract boolean isEnd();

    /**
     * 设置GVM屏幕显示的颜色
     * @param black 黑
     * @param white 白
     */
    public abstract void setColor(int black, int white);

    /**
     * 设置该GVM使用的输入法,可以为null
     * @param im 该GVM使用的输入法
     * @return  该GVM之前使用的输入法
     */
    public abstract InputMethod setInputMethod(InputMethod im);

    /**
     * 添加虚拟机屏幕状态监听器
     * @param listener 屏幕监听器
     * @see ScreenModel#addScreenChangeListener(ScreenChangeListener)
     */
    public abstract void addScreenChangeListener(ScreenChangeListener listener);

    /**
     * 得到该GVM的配置
     * @return config
     */
    public abstract GvmConfig getConfig();
}
