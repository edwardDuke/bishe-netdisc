
### 一、Hadoop安装


 **1.1 伪分布模式** 
 **1.1.1安装前准备** 
（1）安装Linux
      下载Ubuntu 16.04，下载地址：http://ftp.sjtu.edu.cn/ubuntu-cd/16.04.5/

（a）Desktop    -->  桌面版，默认带了界面
    ubuntu-16.04.5-desktop-amd64.iso
（b）Server      --> 服务器版，默认没有带界面
    ubuntu-16.04.5-server-amd64.iso

（2）关闭防火墙
    查看防火墙状态
$ sudo ufw status
Status: inactive

关闭防火墙
$ sudo ufw disable
防火墙在系统启动时自动禁用

查看防火墙状态：
$ sudo ufw status
Status: inactive

（3）安装JDK
    解压到根目录：
tar zxvf jdk-8u144-linux-x64.tar.gz -C  ~

建一个软链接（方便使用）
ln  -s  jdk1.8.0_144  jdk
配置环境变量：

vi  ~/.bashrc

（等号两侧不要加入空格）
export JAVA_HOME=/home/hadoop3/jdk
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:.

使得变量生效：
source  ~/.bashrc

（4）确认openssh-client、openssh-server是否安装
dpkg -l | grep openssh

如果没有安装，则安装：
sudo apt-get install openssh-client
sudo apt-get install openssh-server

（5）安装包解压
tar -zxvf hadoop-2.7.3.tar.gz -C ~

创建超链接：(便于使用)
ln -s hadoop-2.7.3 hadoop

配置环境变量：
vi ~/.bashrc
export HADOOP_HOME=/home/hadoop3/hadoop
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
source  ~/.bashrc
（2）配置主机名
sudo vi  /etc/hosts   （前面加sudo，需要root权限）
191.168.88.130  node1.hadoop3  node1

（3）免密码登录
 通过ssh-keyen生成一个RSA的密钥对
ssh-keygen -t rsa -P ''

公钥追加到~/.ssh/authorized_keys文件中
ssh-copy-id -i  ~/.ssh/id_rsa.pub  主机名(如上面都nod1)

测试免密码登录：
 ssh node1

1.2.2安装
（1）修改配置文件
hadoop-env.sh
export JAVA_HOME=/home/hadoop3/jdk

hdfs-site.xml
<!--表示数据块的冗余度，默认：3-->

<property>
  <name>dfs.replication</name>
  <value>1</value>
</property>

core-site.xml
<!--配置NameNode地址,9000是RPC通信端口-->

<property>
   <name>fs.defaultFS</name>
   <value>hdfs://node1:9000</value>
</property>	

<!--HDFS数据保存在Linux的哪个目录，默认值是Linux的tmp目录-->
<property>
   <name>hadoop.tmp.dir</name>
   <value>/home/hadoop3/hadoop/tmp</value>
</property>	


mapred-site.xml
默认没有（cp mapred-site.xml.template mapred-site.xml）
<!--MR运行的框架-->
<property>
   <name>mapreduce.framework.name</name>
   <value>yarn</value>
</property>	

yarn-site.xml
<!--Yarn的主节点RM的位置-->
<property>
   <name>yarn.resourcemanager.hostname</name>
   <value>node1</value>
</property>	
<!--MapReduce运行方式：shuffle洗牌-->
<property>
   <name>yarn.nodemanager.aux-services</name>
   <value>mapreduce_shuffle</value>
</property>	


（2）格式化
hdfs namenode -format

（3）启动停止Hadoop的环境
start-all.sh
stop-all.sh
（4）测试
查看进程：
jps

通过Web界面：
HDFS:  http://191.168.88.130:50070  http://191.168.88.130:50090
Yarn:  http://191.168.88.130:8088