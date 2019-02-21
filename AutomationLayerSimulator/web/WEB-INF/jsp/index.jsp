<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <title>FFU Freezer - Physical Layer</title>
    <script src="https://code.jquery.com/jquery-1.10.2.js"></script>
    <style>
        .LightGrey{
            background-color: #F2F2F2;
        }
        .Grey{
            background-color: #E1E3E4;
        }
    </style>
</head>
<body>
<h1>${it.hello} ${it.world}</h1>


<table align="center" border="1" style="text-align: center">
    <tr height="60px">
        <td width="320px">${it.s00}</td>
        <td width="320px">${it.s10}</td>
        <td width="320px">${it.s20}</td>
        <td width="320px">${it.s30}</td>
    </tr>
    <tr height="60px">
        <td>${it.s01}</td>
        <td>${it.s11}</td>
        <td>${it.s21}</td>
        <td>${it.s31}</td>
    </tr>
    <tr height="60px">
        <td>${it.s02}</td>
        <td>${it.s12}</td>
        <td>${it.s22}</td>
        <td>${it.s32}</td>
    </tr>
</table>

</body>
</html>

<script>
    $( document ).ready(function () {
        $('td').each (function() {
           if($(this)[0].textContent === "Empty"){
               $(this).addClass("LightGrey")
           } else {
               $(this).addClass("Grey")
           }
        });
    });

</script>