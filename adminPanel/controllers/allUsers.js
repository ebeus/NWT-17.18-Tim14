var app = angular.module('adminApp', []);

// app.config(['$qProvider', function ($qProvider) {
    // $qProvider.errorOnUnhandledRejections(false);
// }]);

// app.run(['$http', function($http) {
    // $http.defaults.headers.common['Authorization'] = 'Bearer '+'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVSUQiOjEsIlVHcm91cCI6IkdydXBhMSIsInVzZXJfbmFtZSI6ImpCYXVlciIsInNjb3BlIjpbIm1vYmlsZSJdLCJVVHlwZSI6IkFETUlOIiwiZXhwIjoxNTI4MTY3ODYyLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6Ijg5Y2YwMWIxLTA1NDEtNDU3Yy1hZWNlLWJiMTExNGQ4ZGNmNyIsImNsaWVudF9pZCI6ImNsaWVudCJ9.8g634lFwQLmsd8-FsTNLbPKS5q-EaTTQij7LnA8YVTM';
// }]);


app.controller('allUsers', function($scope,$http) {
	$http.get("http://80.80.40.105:8090/korisnici_ms/users/")
	.then(function successCallback(response){
				console.log("Response success ");
                $scope.results = response.data;
            }, function errorCallback(response){
                console.log("Unable to perform get request " + response.error);
            });
 });
	
	