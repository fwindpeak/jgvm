####################################################################
#                  GVM系统属性配置文件
#                        2008/4/19   By Eastsun
#1.配置文件格式:
#     与JAVA SE中的配置文件基本相同,但功能比之弱很多.注意属性名的大小写!
#2.配置文件的功能:
#     能设置GVmakerME的各项属性,包括:
#      1.GVM的颜色(BLACK_COLOR,WHITE_COLOR,BACKGROUND)
#      2.旋转屏幕的角度(CIRC_ANGLE),只可以是0,90,270
#      3.屏幕放大的倍数(SCREEN_RATE),只可以是1,2
#      4.指定FileList与GetWord中的按键值
###################################################################

#GVM中的颜色值:黑色,白色,背景色
BLACK_COLOR    = 0x000000
WHITE_COLOR    = 0x40C040
BACKGROUND     = 0x000000


#旋转角度
CIRC_ANGLE     = 270

#放大倍数
SCREEN_RATE    = 2

#GVM的根目录,就是GVM_ROOT文件夹所在位置.
#该设置可选,如果没有设置该项,程序将自动搜索GVM_ROOT文件夹.
#!!注意文件路径的格式,建议菜鸟不要使用该设置
#在非Nokia手机上,存储卡位置可能为:file://Storage Card/GVM_ROOT/
#               手机内存可能为:  file://localhost/GVM_ROOT/
#Nokia手机上,存储卡位置应该是:    file://E:/GVM_ROOT/
#GVM_ROOT      = file://localhost/GVM_ROOT/

#FileList与GetWord中的按键值.注意:该值只影响这两项的操作.普通按键值在GVM_ROOT/KEY/下面的配置文件中指定
KEY_ENTER      = -5
KEY_ESC        = '*'
KEY_UP         = -4
KEY_DOWN       = -3
KEY_LEFT       = -1
KEY_RIGHT      = -2

#是否支持数字键,可以是true或false.如果为false,则输入法GetWord将使用getchar替代
NUMBER_KEY_SUPPORTED = true

#当仅当NUMBER_KEY_SUPPORTED = true时下列设置有效
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

#强制退出运行中的GVmaker程序;该项设置可选.
#如果设置了该键,则其它按键配置中该键不能再次设置
QUICK_EXIT     = '#'