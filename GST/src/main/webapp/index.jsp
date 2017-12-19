<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html data-ng-app="myApp">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home Page</title>
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>  
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-filter/0.5.16/angular-filter.js"></script>  

        <script src="<c:url value="/resources/js/app.js" />"></script>
        <script src="<c:url value="/resources/js/angular-ui-bootstrap-modal1.js" />"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

        <link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
        <link href="<c:url value="/resources/css/bootstrap.css" />" rel="stylesheet">

        <script>
            (function ($) {
                $(document).ready(function () {
                    $('ul.dropdown-menu [data-toggle=dropdown]').on('click', function (event) {
                        event.preventDefault();
                        event.stopPropagation();
                        $(this).parent().siblings().removeClass('open');
                        $(this).parent().toggleClass('open');
                    });
                });
            })(jQuery);
        </script>
        <style>
            .marginBottom-0 {margin-bottom:0;}

            .dropdown-submenu{position:relative;}
            .dropdown-submenu>.dropdown-menu{top:0;left:100%;margin-top:-6px;margin-left:-1px;-webkit-border-radius:0 6px 6px 6px;-moz-border-radius:0 6px 6px 6px;border-radius:0 6px 6px 6px;}
            .dropdown-submenu>a:after{display:block;content:" ";float:right;width:0;height:0;border-color:transparent;border-style:solid;border-width:5px 0 5px 5px;border-left-color:#cccccc;margin-top:5px;margin-right:-10px;}
            .dropdown-submenu:hover>a:after{border-left-color:#555;}
            .dropdown-submenu.pull-left{float:none;}.dropdown-submenu.pull-left>.dropdown-menu{left:-100%;margin-left:10px;-webkit-border-radius:6px 0 6px 6px;-moz-border-radius:6px 0 6px 6px;border-radius:6px 0 6px 6px;}

        </style>
        <script>

            var myApp = angular.module('myApp', ["ui.bootstrap.modal"]);
            myApp.controller("myCtrl", ['$scope', '$http', function ($scope, $http)
                {
                    $scope.showModal = false;
                    $scope.getyearCompcode = function () {

                        var start_date = $scope.start_date;
                        var end_date = $scope.end_date;
                        $http.get('getyearCompcode').
                                success(function (response) {
                                    $scope.year = response;
                                });
                        $scope.showModal = true;
                    };
                    $scope.cancel = function () {
                        $scope.showModal = false;
                    };
                    $scope.cyear = [];
                    $scope.comp = [];
                    $scope.select = function (b)
                    {
                        $scope.cyear = b.YEAR;
                        $scope.comp = b.COMP_CODE;

                    };
                    $scope.SelectYear = function ()
                    {
                        $http.get('selectyear', {params: {year: $scope.cyear, comp: $scope.comp}}).
                                success(function (response) {
                                    $scope.year = response;
                                    $scope.showModal = false;
                                });
                    };
                }]);
        </script>
    </head>
    <body  data-ng-controller="myCtrl">
    <nav class="navbar navbar-inverse">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>                        
                </button>
                <a class="navbar-brand" href="#">Accounts Module</a>
            </div>
            <div class="collapse navbar-collapse" id="myNavbar">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="index.jsp">Home</a></li>
                    <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#">GST <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="Gst">GST</a></li>
                            <li><a href="JsonParse">JsonParse</a></li>
                            <li><a href="GstComparison">JsonGstComparison</a></li>
                            <li><a href="DatabasetoJsonComparsn">DatabasetoJsonComparsn</a></li>
                             <li><a href="DateWiseGst">GST Data</a></li>
                               <li><a href="GSTAngular">GST Angular</a></li>
                        </ul>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="Logout"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
                </ul>
            </div>
        </div>
    </nav>  
    <div class="container" >
        <h3>Select Year</h3>
        <input type="button" value="Get Data" ng-click="getyearCompcode()" class="btn btn-primary" />
    </div>
    <div modal="showModal" close="cancel()">
        <div class="modal-header">
            <h4 class="modal-title">Select Year and Company Code</h4>
        </div>
        <div class="modal-body">
            <table class="table table-bordered table-condensed  table-sm " style="text-align: center;">
                <thead>
                    <tr>
                        <th class="col-sm-1" style="text-align: center;">Company Code</th>
                        <th class="col-sm-1" style="text-align: center;">Year</th>
                        <th class="col-sm-3" style="text-align: center;">Company Name</th>
                    </tr>
                </thead>
                <tbody ng-repeat="b in year">
                    <tr style="text-align: center;" ng-click="select(b)" >
                        <td style="text-align: center;">{{b.COMP_CODE}}</td>
                        <td style="text-align: center;">{{b.YEAR}}</td>
                        <td style="text-align: center;">{{b.COMP_NAME}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="SelectYear()">Ok</button>
            <button class="btn" ng-click="cancel()">Cancel</button>
        </div>
    </div>
</body>
</html>
