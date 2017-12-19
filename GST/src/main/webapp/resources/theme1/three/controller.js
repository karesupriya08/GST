   var myApp = angular.module('myApp', ["ui.bootstrap.modal"]);
            myApp.controller("myCtrl", ['$scope', '$http', function ($scope, $http)
                {
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
  $scope.getCompData=function()
  {
      alert('called');
      $http.get('getCompData', {params: {comp: $scope.comp}}).
                                success(function (response) {
                                    $scope.json = response;
                                 
                                    // $scope.gsdata={"gstdata":[{"VBILLNO":'aa',"VBILLDT":'bb',"VAMT":123,gs:[{"CGST_amount":123,"CGST_PERC":11,"sGST_amount":11,"sGST_PERC":123,"IGST_AMOUNT":11,"IGST_PERC":11},{}]}]};
                                });
  };
  
}]);
myApp.filter('range', function() {
  return function(input, total) {
    total = parseInt(total);

    for (var i=10; i<total; i++) {
      input.push(i);
    }

    return input;
  };
});
