####################################################################
#                  GVMϵͳ���������ļ�
#                        2008/4/19   By Eastsun
#1.�����ļ���ʽ:
#     ��JAVA SE�е������ļ�������ͬ,�����ܱ�֮���ܶ�.ע���������Ĵ�Сд!
#2.�����ļ��Ĺ���:
#     ������GVmakerME�ĸ�������,����:
#      1.GVM����ɫ(BLACK_COLOR,WHITE_COLOR,BACKGROUND)
#      2.��ת��Ļ�ĽǶ�(CIRC_ANGLE),ֻ������0,90,270
#      3.��Ļ�Ŵ�ı���(SCREEN_RATE),ֻ������1,2
#      4.ָ��FileList��GetWord�еİ���ֵ
###################################################################

#GVM�е���ɫֵ:��ɫ,��ɫ,����ɫ
BLACK_COLOR    = 0x000000
WHITE_COLOR    = 0x40C040
BACKGROUND     = 0x000000


#��ת�Ƕ�
CIRC_ANGLE     = 270

#�Ŵ���
SCREEN_RATE    = 2

#GVM�ĸ�Ŀ¼,����GVM_ROOT�ļ�������λ��.
#�����ÿ�ѡ,���û�����ø���,�����Զ�����GVM_ROOT�ļ���.
#!!ע���ļ�·���ĸ�ʽ,�������Ҫʹ�ø�����
#�ڷ�Nokia�ֻ���,�洢��λ�ÿ���Ϊ:file://Storage Card/GVM_ROOT/
#               �ֻ��ڴ����Ϊ:  file://localhost/GVM_ROOT/
#Nokia�ֻ���,�洢��λ��Ӧ����:    file://E:/GVM_ROOT/
#GVM_ROOT      = file://localhost/GVM_ROOT/

#FileList��GetWord�еİ���ֵ.ע��:��ֵֻӰ��������Ĳ���.��ͨ����ֵ��GVM_ROOT/KEY/����������ļ���ָ��
KEY_ENTER      = -5
KEY_ESC        = '*'
KEY_UP         = -4
KEY_DOWN       = -3
KEY_LEFT       = -1
KEY_RIGHT      = -2

#�Ƿ�֧�����ּ�,������true��false.���Ϊfalse,�����뷨GetWord��ʹ��getchar���
NUMBER_KEY_SUPPORTED = true

#������NUMBER_KEY_SUPPORTED = trueʱ����������Ч
KEY_NUMBER0    = '0'
KEY_NUMBER1    = '1'
KEY_NUMBER2    = '2'
KEY_NUMBER3    = '3'
KEY_NUMBER4    = '4'
KEY_NUMBER5    = '5'
KEY_NUMBER6    = '6'
KEY_NUMBER7    = '7'
KEY_NUMBER8    = '8'
KEY_NUMBER9    = '9'

#ǿ���˳������е�GVmaker����;�������ÿ�ѡ.
#��������˸ü�,���������������иü������ٴ�����
QUICK_EXIT     = '#'