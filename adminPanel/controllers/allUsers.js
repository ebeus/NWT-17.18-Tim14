var app = angular.module('adminApp', []);
app.controller('allUsers', function($scope,$http) {
    $http.get("https://jsonplaceholder.typicode.com/users/1")
    .then(function(response) {
        $scope.content = response.data;
    });
});