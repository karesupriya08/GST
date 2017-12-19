<%-- 
    Document   : GSTAngular
    Created on : Nov 6, 2017, 10:18:37 AM
    Author     : Supriya Kare
--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html data-ng-app="myApp" >
    <head>

        <meta charset="utf-8">
        <title>GST Angular</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>  
        <script src="<c:url value="/resources/js/app.js" />"></script>
        <script src="<c:url value="/resources/js/angular-ui-bootstrap-modal1.js" />"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-filter/0.5.16/angular-filter.js"></script>  
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">

        <script src="<c:url value="/resources/three/angular.js" />"></script>
        <script src="<c:url value="/resources/three/angular.min.js" />"></script>
        <script src="<c:url value="/resources/three/controller1.js" />"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script>

            myApp = angular.module('myApp', []).config(function ($httpProvider) {
                $httpProvider.interceptors.push(function ($q) {
                    return {
                        'request': function (config) {
                            $('#processing').show();
                            return config;
                        },
                        'response': function (response) {
                            $('#processing').hide();
                            return response;
                        }
                    };
                });
            });
            var myApp = angular.module('myApp', ["ui.bootstrap.modal"]);
            myApp.controller("myCtrl", ['$scope', '$http', function ($scope, $http)
                {
                    $scope.sortType = 'name'; // set the default sort type
                    $scope.sortReverse = false;  // set the default sort order
                    $scope.search = {};     // set the default search/filter term
                    $('#printdiv').hide();
                    $('#tablediv').hide();
                    // create the list of sushi rolls 
                    // $scope.compdata = [{"rt":"IGST 5% ","vamt":572,"partyname":null,"vbilldt":"22-Jul-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"14 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":232,"partyname":null,"vbilldt":"26-Jul-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"21 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":1921.45,"partyname":null,"vbilldt":"24-Aug-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"72 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":6439.39,"partyname":null,"vbilldt":"09-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"157 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":9857.99,"partyname":null,"vbilldt":"06-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"150 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":4699.79,"partyname":null,"vbilldt":"07-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"154 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":8219.93,"partyname":null,"vbilldt":"13-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"166 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":2688.53,"partyname":null,"vbilldt":"17-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"171 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":4199.48,"partyname":null,"vbilldt":"26-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"173 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "},{"rt":"IGST 5% ","vamt":7853.74,"partyname":null,"vbilldt":"16-Oct-17","vrcode":"100S998","gstin1":"09AAGPM3196B1ZQ","vbillno":"169 ","gstin":"24ACVPT6910D1ZC","vcode":"248002 "}];
                    $scope.compdata = [{}];

                    $scope.clearFilter = function () {
                        $scope.search = {};
                    };
                    $scope.getCurrentDate = function ()
                    {
                        $http.get('getCurrentDate').
                                success(function (response) {
                                    $scope.date = response;

                                });
                    };
                    $scope.getCompData = function ()
                    {
                        $http.get('getCompData', {params: {comp: $scope.search.comp, party: $scope.search.p_code.VRCODE}}).
                                success(function (response) {
                                    $scope.compdata = response;
                                    $('#tablediv').show();

                                    // $scope.gsdata={"gstdata":[{"VBILLNO":'aa',"VBILLDT":'bb',"VAMT":123,gs:[{"CGST_amount":123,"CGST_PERC":11,"sGST_amount":11,"sGST_PERC":123,"IGST_AMOUNT":11,"IGST_PERC":11},{}]}]};
                                });

                    };
                    $scope.getCompDatafromGSTin = function ()
                    {
                        $("#processing").show();
                        $http.get('getCompDatafromGST', {params: {comp: $scope.search.comp}}).
                                success(function (response) {
                                    $scope.compdata = response.data;
                                    $scope.compname = response;
                                    $('#tablediv').show();
                                    $("#processing").hide();
                                });
                        $scope.getCurrentDate();
                    };
                    $scope.getPartyCode = function ()
                    {
                        $http.get('getPartyCode', {params: {comp: $scope.search.comp}}).
                                success(function (response) {
                                    $scope.party = response;

                                    // $scope.gsdata={"gstdata":[{"VBILLNO":'aa',"VBILLDT":'bb',"VAMT":123,gs:[{"CGST_amount":123,"CGST_PERC":11,"sGST_amount":11,"sGST_PERC":123,"IGST_AMOUNT":11,"IGST_PERC":11},{}]}]};
                                });
                    };
                    $scope.print = function ()
                    {
                        $('#printdiv').show();
                        window.print();
                        $('#printdiv').hide();
                        window.onfocus = function () {
                            window.close();
                            $('#printdiv').hide();
                        };

                    };

                }]);
            myApp.filter('range', function () {
                return function (input, total) {
                    total = parseInt(total);

                    for (var i = 10; i < total; i++) {
                        input.push(i);
                    }

                    return input;
                };
            });

        </script>

        <style>

            .table-responsive {
                max-height:500px;
            }
            @media print
            {
                .noprint {display:none;}
            }
            .input-group-addon {
                padding: 6px 6px;
            }
            #processing {  
                position:absolute;
                top:0;
                left:0;
                width:100%;
                height:100%;
                z-index:1000;
                // background-color:grey;
                opacity: .6;
            }

            .ajax-loader {
                position: absolute;
                left: 40%;
                top: 40%;
                display: block;     
            }
        </style>
    </head>
    <!-- Controller name goes here -->
    <body data-ng-controller="myCtrl">
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
        <div class="panel panel-info noprint" style='width:90%;margin-left: 5%'>
            <div class="panel-heading">Gst Module</div>
            <div class="panel-body">
                <form  class="form-horizontal" name="myform" data-ng-submit="submit()" nonvalidate>  
                    <div class="form-group row ">
                        <div class="col-sm-2">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <select id="search.comp" ng-model="search.comp" name="search.comp" required   class="form-control" ng-change="getCompDatafromGSTin()">
                                    <option value="" disabled>Select Company</option>
                                    <option  value="fashup">Fashions UP</option>
                                    <option  value="fashdl">Fashions Delhi</option>
                                    <option  value="jdl">Joss Delhi</option>
                                    <option  value="jup">Joss UP</option>
                                    <option  value="mer">Merchants</option>
                                </select> 

                            </div>
                            <span ng-show="myform1.search.comp.$invalid && myform1.search.comp.$error.required" style="color: red">Required.</span>
                        </div>
                        <div class="col-md-2">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <input type="text" class="form-control" placeholder="Party Code" ng-model="search.vrcode">
                                <!-- <select ng-model="search.p_code" name="search.p_code" id="search.p_code" class="form-control"  ng-change="getCompData()"
                                         ng-options="p.P_NAME for p in party track by p.VRCODE" >                                                               
                                     <option value="" disabled>Select Party</option>
                                 </select>-->
                            </div> 
                        </div>
                        <div class="col-md-2">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <input type="text" class="form-control" placeholder="Bill No" ng-model="search.vbillno">
                            </div> 
                        </div>
                        <div class="col-sm-2">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <input type="text" class="form-control" placeholder="Bill Date" ng-model="search.vbilldt">
                            </div> 
                        </div>
                        <div class="col-md-1">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <input type="text" class="form-control" placeholder="Vamt" ng-model="search.vamt">
                            </div> 
                        </div>
                        <div class="col-md-1">
                            <div class="input-group">
                                <div class="input-group-addon"><i class="fa fa-search"></i></div>
                                <input type="text" class="form-control" placeholder="Rate" ng-model="search.rt">
                            </div> 
                        </div>
                        <div class="col-md-2">
                            <div class="input-group">
                                <button type="button" class="btn btn-primary" ng-click="clearFilter()">Reset</button><span>   </span>
                                <button type="button" class="btn btn-primary" ng-click="print()" >print</button>
                            </div> 
                        </div>
                    </div>
                    <div id="processing" style="display: none"><img class="ajax-loader" src="<c:url value="/resources/three/spinner.gif"></c:url>" /></div>
                </form>

                <table class="table table-bordered table-striped table-responsive" id="tablediv">
                    <thead>
                        <tr>
                            <td>
                                Gstin
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'vrcode';
                                            sortReverse = !sortReverse">Party Code
                                    <span ng-show="sortType == 'vrcode' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'vrcode' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'vbillno';
                                            sortReverse = !sortReverse">Bill No
                                    <span ng-show="sortType == 'vbillno' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'vbillno' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'vbilldt';
                                            sortReverse = !sortReverse">Bill Date
                                    <span ng-show="sortType == 'vbillno' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'vbillno' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'vamt';
                                            sortReverse = !sortReverse">Vamt
                                    <span ng-show="sortType == 'vamt' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'vamt' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'vbillamt';
                                            sortReverse = !sortReverse">VBillAmt
                                    <span ng-show="sortType == 'vbillamt' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'vbillamt' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                            <td>
                                <a href="#" ng-click="sortType = 'rt';
                                            sortReverse = !sortReverse">Rate
                                    <span ng-show="sortType == 'rt' && !sortReverse" class="fa fa-caret-down"></span>
                                    <span ng-show="sortType == 'rt' && sortReverse" class="fa fa-caret-up"></span>
                                </a>
                            </td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="profile in filteredData = (compdata| orderBy:sortType:sortReverse |  filter:{vrcode:search.vrcode} | filter:{vbillno:search.vbillno} |  filter:{vbilldt:search.vbilldt} | filter:{vamt:search.vamt} | filter:{rt:search.rt})">
                            <td>{{ profile.gstin}}</td>
                            <td>{{ profile.vrcode}}</td>
                            <td>{{ profile.vbillno}}</td>
                            <td>{{ profile.vbilldt}}</td>
                            <td>{{ profile.vamt}}</td>
                            <td>{{ profile.vbillamt}}</td>
                            <td>{{ profile.rt}}</td>
                        </tr>
                        <tr ng-if="filteredData.length == 0">
                            <td colspan="4">No record found</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="col-md-12 " id="printdiv">
            <p style="width: 100%;font-size: 12px;text-align: center"><b> R.M.X. J O S S </b></p>
            <p style="width: 100%;font-size: 12px;text-align: center"><b> GST Report </b></p>
            <p style="width: 100%;font-size: 12px;"><b>Comapny Name:  {{compname.compname}} </b></p>

            <p>Printed on : {{date.curr_date}}</p>
            <table class="table table-bordered table-condensed  table-sm" style="width:60;font-size: 7px;line-height:1;padding: 0px;border:1px;text-align: center;">
                <thead>
                    <tr>
                        <td> GST </td>           
                        <td> Party Code </td>           
                        <td>   BILL NO  </td>           
                        <td>  BILL DATE </td>
                        <td> VAMT </td>
                        <td> Rate </td>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="profile in filteredData">
                        <td>{{ profile.gstin}}</td>
                        <td style="text-align: left">{{ profile.vrcode}}</td>
                        <td>{{ profile.vbillno}}</td>
                        <td>{{ profile.vbilldt}}</td>
                        <td>{{ profile.vamt}}</td>
                        <td>{{ profile.rt}}</td>
                    </tr>
                    <tr ng-if="filteredData.length == 0">
                        <td colspan="4">No record found</td>
                    </tr>
                </tbody>

            </table>


        </div>

    </body>
</html>
