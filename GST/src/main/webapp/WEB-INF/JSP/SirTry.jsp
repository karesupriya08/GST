<%-- 
    Document   : SirTry
    Created on : Nov 6, 2017, 11:11:36 AM
    Author     : Supriya Kare
--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Dinesh AngularJS - Search Multiple - Sort column - Filter Example</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
     
        <script src="<c:url value="/resources/three/angular.min.js" />"></script>
         <script src="<c:url value="/resources/three/controller.js" />"></script>
                     
        
        <style>
            body { padding-top:50px; }
        </style>
    </head>
    <!-- Controller name goes here -->
    <body ng-app="chinuApp">


        <div class="container" ng-controller="mainController">

            <div class="alert alert-info col-lg-12">
                <div class="col-lg-6">Sort Type: {{ sortType }}</div>
                <div class="col-lg-6">Sort Reverse:  {{ sortReverse }}</div>
                <div class="col-lg-12">Search Query:  {{ search | json}}</div>
            </div>

            <form>
                <div class="form-group row col-lg-12">
                    <div class="col-lg-3">
                        <div class="input-group">
                            <div class="input-group-addon"><i class="fa fa-search"></i></div>
                            <input type="text" class="form-control" placeholder="Search Name" ng-model="search.name">
                        </div>
                    </div>
                    <div class="col-lg-3">
                        <div class="input-group">
                            <div class="input-group-addon"><i class="fa fa-search"></i></div>
                            <input type="text" class="form-control" placeholder="Search Email" ng-model="search.email">
                        </div> 
                    </div>
                    <div class="col-lg-3">
                        <div class="input-group">
                            <div class="input-group-addon"><i class="fa fa-search"></i></div>
                            <input type="text" class="form-control" placeholder="Search Address" ng-model="search.address">
                        </div> 
                    </div>
                    <div class="col-lg-2">
                        <div class="input-group">
                            <div class="input-group-addon"><i class="fa fa-search"></i></div>
                            <select name="age" ng-model="search.age" class="form-control">
                                <option value="">Select Age</option>
                                <option ng-repeat="age in [] | range:100" value="{{age}}">{{age}}</option>
                            </select>
                        </div> 
                    </div>
                    <div class="col-lg-1">
                        <div class="input-group">
                            <button type="button" class="btn btn-primary" ng-click="clearFilter()">Reset</button>
                        </div> 
                    </div>
                </div>
            </form>

            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <td>
                            <a href="#" ng-click="sortType = 'name'; sortReverse = !sortReverse">Name 
                                <span ng-show="sortType == 'name' && !sortReverse" class="fa fa-caret-down"></span>
                                <span ng-show="sortType == 'name' && sortReverse" class="fa fa-caret-up"></span>
                            </a>
                        </td>
                        <td>
                            <a href="#" ng-click="sortType = 'email'; sortReverse = !sortReverse">Email
                                <span ng-show="sortType == 'email' && !sortReverse" class="fa fa-caret-down"></span>
                                <span ng-show="sortType == 'email' && sortReverse" class="fa fa-caret-up"></span>
                            </a>
                        </td>
                        <td>
                            <a href="#" ng-click="sortType = 'address'; sortReverse = !sortReverse">Address
                                <span ng-show="sortType == 'address' && !sortReverse" class="fa fa-caret-down"></span>
                                <span ng-show="sortType == 'address' && sortReverse" class="fa fa-caret-up"></span>
                            </a>
                        </td>
                        <td>
                            <a href="#" ng-click="sortType = 'age'; sortReverse = !sortReverse">Age
                                <span ng-show="sortType == 'age' && !sortReverse" class="fa fa-caret-down"></span>
                                <span ng-show="sortType == 'age' && sortReverse" class="fa fa-caret-up"></span>
                            </a>
                        </td>
                    </tr>
                </thead>

                <tbody>
                    <tr ng-repeat="profile in filteredData = (chinu | orderBy:sortType:sortReverse | filter:{name:search.name} | filter:{email:search.email} | filter:{address:search.address} | filter:{age:search.age})">
                        <td>{{ profile.name }}</td>
                        <td>{{ profile.email }}</td>
                        <td>{{ profile.address }}</td>
                        <td>{{ profile.age }}</td>
                    </tr>
                    <tr ng-if="filteredData.length==0">
                        <td colspan="4">No record found</td>
                    </tr>
                </tbody>

            </table>
           
            <p class="text-center">
                by Dinesh-Chabra
            </p>

        </div>

        
    </body>
</html>
var angularApp = angular.module('chinuApp', []);
angularApp.controller('mainController', function($scope) {
  $scope.sortType     = 'name'; // set the default sort type
  $scope.sortReverse  = false;  // set the default sort order
  $scope.search   = {};     // set the default search/filter term
  
  // create the list of sushi rolls 
  $scope.chinu = [
    { name: 'Dinesh Chabra', email: 'dinesh@rmxjoss.com', address: 'Patel Nagar, New Delhi', age:51 },
    { name: 'Manmohan Singh', email: 'manmohan@rmxjoss.com', address: 'East Delhi, Delhi', age:40 },
    { name: 'Javed Khan', email: 'Javed@rmxjosscom', address: 'West Delhi,Delhi', age:44 },
    { name: 'Sushil Kumar', email: 'sushilkumar@rmxjoss.com', address: 'West Delhi, Delhi', age:31 },
    { name: 'Naveen Sharma', email: 'naveen@rmxjoss.com', address: 'West Delhi, Delhi', age:27 }
  ];
  
  $scope.clearFilter = function(){
      $scope.search = {};
  };
  
});

angularApp.filter('range', function() {
  return function(input, total) {
    total = parseInt(total);

    for (var i=10; i<total; i++) {
      input.push(i);
    }

    return input;
  };
});
