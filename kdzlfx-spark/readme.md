## 宽带质量分析系统--特征值计算模块、数据清洗模块

* 版本：1.0

--------------

### Requirements

* jdk 1.8+
* hadoop 2.7+
* spark 2.2+
* scala 2.11.8+

--------------

### 程序结构

* com.ainsg.lwfx.clean包下为数据清洗模块，com.ainsg.lwfx.features包下为特征值计算模块，common、transformer、util为公共方法
* 数据清洗模块入口函数为com.ainsg.lwfx.clean.service.CleanService
* 特征值计算模块入口函数为com.ainsg.lwfx.features.service.FeatureService，执行时需要输入运行参数：地市 开始月/周 周期数
* 特征值计算模块可单独运行话单数据月/周合计功能，入口函数为com.ainsg.lwfx.features.service.StatisticService，执行时需要输入运行参数：地市 开始月/周 周期数

* 号线特征值计算模块入口函数为com.ainsg.lwfx.linebind.service.LinebindService
* 智能网关特征值计算模块入口函数为com.ainsg.lwfx.terminal.service.IgatewayService
* 上网行为(仅使用OTT业务)特征值计算模块入口函数为com.ainsg.lwfx.ott.service.OTTService
* 投诉告障特征值计算模块入口函数为com.ainsg.lwfx.complaint.service.ComplaintService
* 终端数据标签入口函数 com.ainsg.lwfx.terminal.service.UnionService
--------------

### 已完成的功能

* AAA话单数据按月统计
* 根据AAA话单、开通历史表、CRM用户表计算当月用户特征、状态以及更新上月用户特征表状态
* 根据配置对采集数据清洗

--------------

### 未完成功能

* 按周统计特征值
