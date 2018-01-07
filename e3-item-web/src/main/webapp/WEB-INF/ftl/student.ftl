<html>
<head>测试页面</head>
<br/>
<body>

	学生列表<br/>
	姓名：${student.name}<br/>
	年龄：${student.age}<br/>
	
	<table border="1"/>
		<tr>
			<th>
				序号
			</th>
			<th>
				姓名
			</th>
			<th>
				年龄
			</th>
		</tr>
		
		<#list	stuList as stu>
		<#if stu_index%2==0>
			<tr bgcolor="red">
		<#else>
			<tr bgcolor="blue">
		</#if>
			<td>
				${stu_index}
			</td>
			<td>
				${stu.name}
			</td>
			<td>
				${stu.age}
			</td>
		</tr>
		</#list>
	</table>
	
	日期类型展示:${date?string("yyyy/MM/dd HH:mm:ss")}<br/>
	null值处理：${aaa!"默认值"}<br/>
	使用if解决null值：
	<#if aaa??>
		aaa是有值的
	<#else>
		aaa是null
	</#if>
	<br/>
	include标签测试
	<#include "hello.ftl">
</body>
</html>