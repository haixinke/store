# store
存储服务，集成阿里云oos和腾讯云cos

application-oss.yml是阿里云存储服务配置，application-cos.yml是腾讯云存储服务配置，填写自己的信息，替换********
application.yml只有一个profile配置，如果选用腾讯云就赋值：cos，阿里云就是oss，其他不需要修改，程序自动切换到响应的存储云。
