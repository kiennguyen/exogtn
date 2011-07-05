<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>GateIn OpenID Register/Map User</title>
</head>
<body>
  <form name="openid-register-form" action="/portal/openidregister" method="post" accept-charset="utf-8" style="border: 1px">
    <div>
    	<h2>Register new user and map openID with this user</h2>
    	<p>You had a GateIn user, using <a href="/openid/login/openid/mapuser.jsp?identifier=${identifier}">map user</a></p>
    </div>
    <p id="error" style="color: red">${error}</p>
		<div id="AccountInputSet" class="UIFormInputSet AccountInputSet">
			<table class="UIFormGrid">
				<tbody>
					<tr><td class="FieldLabel">User Name:</td><td class="FieldComponent"><input name="username" type="text" id="username" value="${user.username}"> *<img title="Check Availability" onclick="" src="/eXoResources/skin/DefaultSkin/background/Blank.gif" class="SearchIcon" alt="">&nbsp;</td></tr>
					<tr><td class="FieldLabel">Password:</td><td class="FieldComponent"><input name="password" type="password" id="password" value="${user.passWord}"> *</td></tr>
					<tr><td class="FieldLabel">Confirm Password:</td><td class="FieldComponent"><input name="Confirmpassword" type="password" id="Confirmpassword" value="${user.confirmPassword}"> *</td></tr>
					<tr><td class="FieldLabel">First Name:</td><td class="FieldComponent"><input name="firstName" type="text" id="firstName" value="${user.firstName}"> *</td></tr>
					<tr><td class="FieldLabel">Last Name:</td><td class="FieldComponent"><input name="lastName" type="text" id="lastName" value="${user.lastName}"> *</td></tr>
					<tr><td class="FieldLabel">Email Address:</td><td class="FieldComponent"><input name="email" type="text" id="email" value="${user.email}"> *</td></tr>
					<tr><td class="FieldLabel">OpenID Identifier:</td><td class="FieldComponent"><input name="identifier" type="text" id="identifier" value="${identifier}" readonly="readonly"> *</td></tr>
				</tbody>
			</table>
		</div>
		<div>
			 <button type="submit">Submit</button>
		   <button type="button">Reset</button>
		</div>
	</form>
</body>
</html>