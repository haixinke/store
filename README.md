# store
存储服务，集成阿里云oos和腾讯云cos

application-oss.yml是阿里云存储服务配置，application-cos.yml是腾讯云存储服务配置，填写自己的信息，替换********。
application.yml只有一个profile配置，如果选用腾讯云就赋值：cos，阿里云就是oss，其他不需要修改，程序自动切换到相应的存储云。

目前只提供两个服务：
1、创建桶位
2、上传对象

特性：
1、自由切换存储云平台
2、保证上传文件的唯一性
3、默认支持单个最大20M文件，支持配置
4、上传成功，返回访问文件的url，http协议
