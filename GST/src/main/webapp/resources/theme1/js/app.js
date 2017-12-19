   var appcolour = angular.module('myApp', ["ui.bootstrap.modal"]);

appcolour.controller("myCtrl", function($scope) {
 var cnt = 1;

                // $scope.choices=[{}];
                $scope.choices = [{'colour_way': cnt + '-' + 5}];
                cnt = 2;
  $scope.addcolour = function() {
    $scope.showModal = true;
  };
 $scope.addNewChoice = function () {

                    var colqty = document.getElementById("colqty").value;


                    if (cnt <= colqty) {
                        var newItemNo = $scope.choices.length + 1;
                        $scope.choices.push({'id': 'choice' + newItemNo, colour_way: cnt + '-' + colqty});

                        cnt++;
                    }
                };
                $scope.add = function () {
                    $scope.order.push({});
                };

                $scope.removeChoice = function () {
                    var lastItem = $scope.choices.length - 1;
                    $scope.choices.splice(lastItem);
                };

  $scope.ok = function() {
    $scope.showModal = false;
  };

  $scope.cancel = function() {
    $scope.showModal = false;
  };

});