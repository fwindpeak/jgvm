package eastsun.jgvm.module;

import eastsun.jgvm.module.event.ScreenChangeListener;

/**
 * �涨ʵ��GVM����Ľӿ�.ע��,����getConfig,���������������̰߳�ȫ��<p>
 * ����ʹ��GVM����LavApp,Ȼ����ִ��<p>
 * ����ͨ��setInputMethod��������GVMʹ�õ����뷨,Ĭ��GVM�������뷨,��ʹ��KeyModel.getchar()����������뷨����<p>
 * ������ʹ��GVM��һ�ֿ��ܵķ�ʽ:
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
 * �����ǿ���˳�һ��ִ���е�gvm,�����������gvm���߳�t��interrupt()����<p>
 * @author Eastsun
 * @version 2008-1-14
 */
public abstract class JGVM {

    /**
     * ��������,ͨ�����������õõ�һ��GVM
     * @param config ����
     * @return һ���µ�GVMʵ��
     * @exception IllegalStateException ��֧�ָ����õ�GVM
     */
    public static JGVM newGVM(GvmConfig config, FileModel fileModel, KeyModel keyModel) throws IllegalStateException {
        //��ǰʵ�ֺ���GvmConfig��version����,���Ƿ���һ��GVM1.0��ʵ��
        return new DefaultGVM(config, fileModel, keyModel);
    }

    /**
     * ����һ��app,�����ʵ��ĳ�ʼ��<p>
     * ����Ѿ�����,���ͷ�֮ǰ��app<p>
     * @param app ��Ҫ���ص�app
     * @throws IllegalStateException ��֧�ֵ�app
     */
    public abstract void loadApp(LavApp app) throws IllegalStateException;

    /**
     * ж�����е�app,���ͷ���ռ�õ���Դ
     */
    public abstract void dispose();

    /**
     * ִ����һ��ָ��,�÷������ܻ�����
     * @throws java.lang.IllegalStateException �����Ѿ�������֧�ֵĲ���
     * @throws InterruptedException ִ���ڼ䱻�����߳��ж�
     */
    public abstract void nextStep() throws IllegalStateException, InterruptedException;

    /**
     * �����Ƿ���������,���û�м���app,���Ƿ���true
     * @return ���������Ƿ��Ѿ���������
     */
    public abstract boolean isEnd();

    /**
     * ����GVM��Ļ��ʾ����ɫ
     * @param black ��
     * @param white ��
     */
    public abstract void setColor(int black, int white);

    /**
     * ���ø�GVMʹ�õ����뷨,����Ϊnull
     * @param im ��GVMʹ�õ����뷨
     * @return  ��GVM֮ǰʹ�õ����뷨
     */
    public abstract InputMethod setInputMethod(InputMethod im);

    /**
     * ����������Ļ״̬������
     * @param listener ��Ļ������
     * @see ScreenModel#addScreenChangeListener(ScreenChangeListener)
     */
    public abstract void addScreenChangeListener(ScreenChangeListener listener);

    /**
     * �õ���GVM������
     * @return config
     */
    public abstract GvmConfig getConfig();
}
