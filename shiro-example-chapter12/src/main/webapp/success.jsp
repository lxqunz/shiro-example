<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
    <title></title>
</head>
<body>
    登录成功
    <br/>
     <shiro:hasAnyRoles name="admin">
         <shiro:principal/>拥有角色admin
     </shiro:hasAnyRoles>
     <shiro:hasAnyRoles name="user">
         <shiro:principal/>拥有角色user
     </shiro:hasAnyRoles>

</body>
</html>