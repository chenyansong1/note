<html>
	<title>用户列表展示</title>
	<meta charset="utf-8"/>
	<body>
		<h3>用户列表展示</h3>
		<table>
			<tr>
				<th>编号</th>
				<th>姓名</th>
				<th>年龄</th>
			</tr>
			<#list list as user>
			<tr>
				<td>${user.id}</td>
				<td>${user.name}</td>
				<td>${user.age}</td>
			</tr>
			</#list>
		</table>
	</body>
</html>