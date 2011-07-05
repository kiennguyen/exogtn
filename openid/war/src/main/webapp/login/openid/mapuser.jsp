<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>GateIn OpenID Map User</title>
</head>
<body>
	<form name="openid-map-form" action="/portal/openidmap" style="border: 1px">
		<div><h2>Map with existing user</h2></div>
		<div id="error" style="color: red"><p>${error}</p></div>
		<div id="AccountInputSet" class="UIFormInputSet AccountInputSet">
			<table class="UIFormGrid">
				<tbody>
					<tr><td class="FieldLabel">User Name:</td><td class="FieldComponent"><input name="username" type="text" id="username" value=""> *</td></tr>
					<tr><td class="FieldLabel">Password:</td><td class="FieldComponent"><input name="password" type="password" id="password" value=""> *</td></tr>
					<tr><td class="FieldLabel">OpenID Identifier:</td><td class="FieldComponent"><input name="identifier" type="text" id="identifier" value="<%=request.getParameter("identifier") %>" readonly="readonly"> *</td></tr>
				</tbody>
			</table>
		</div>
		<div>
			 <button type="submit">Map</button>
		   <button type="button">Reset</button>
		</div>
	</form>
</body>
</html>