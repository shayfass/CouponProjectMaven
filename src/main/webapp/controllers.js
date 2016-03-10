var app=angular.module('coupon',['ngRoute']);
app.config(function($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl: 'templates/home.html',
			controller: 'loginController'
		})
		.when('/adminPage', {
		templateUrl: "templates/Adminlogin.html",
			controller: 'loginController'
	})
		.when('/CustomerPage', {
		templateUrl: "templates/Customerlogin.html",
		controller: 'loginController'
	})
		.when('/CompanyPage', {
		templateUrl: "templates/Companylogin.html",
		controller: 'loginController'
	})
		.when('/newcompany', {
		templateUrl: "templates/newcompany.html",
		controller: 'adminController'
	})
	.when('/newcustomer', {
		templateUrl: "templates/newcustomer.html",
		controller: 'adminController'
	})
		.when('/updatecompany', {
		templateUrl: "templates/updatecompany.html",
		controller: 'adminController'
	})
	.when('/managecompany', {
		templateUrl: "templates/deleteshowcomp.html",
		controller: 'adminController'
	})
	.when('/custcoupons', {
		templateUrl: "templates/custcoupons.html",
		controller: 'customerController'
	})
	.when('/compcoupons', {
		templateUrl: "templates/compcoupons.html",
		controller: 'customerController'
	})
	.when('/compcouponby', {
		templateUrl: "templates/compcouponby.html",
		controller: 'customerController'
	})
	.when('/custcouponsprice', {
		templateUrl: "templates/custcouponsprice.html",
		controller: 'customerController'
	})
	.when('/custcouponstype', {
		templateUrl: "templates/custcouponstype.html",
		controller: 'customerController'
	})
	.when('/purchasecoupons', {
		templateUrl: "templates/purchasecoupons.html",
		controller: 'customerController'
	})
	.when('/newcoupon', {
		templateUrl: "templates/newcoupon.html",
		controller: 'companyController'
	})
	.when('/customer', {
		templateUrl: "templates/customer.html",
		controller: 'companyController'
	})
	.when('/incomes', {
		templateUrl: "templates/incomes.html",
		controller: 'companyController'
	})
	.when('/compincomes', {
		templateUrl: "templates/compincomes.html",
		controller: 'companyController'
	})
	.when('/upload/:id', {
		templateUrl: "templates/uploadImage.html",		
	})
		.otherwise({
			redirectTo: '/'
		});
});
// login controller for admin, company and customer//
app.controller(
		'loginController',
		function($scope, $http,$location,$rootScope) {
			// on load
			$scope.loginResult = "no body";
var log=$scope.type;	


    


			// login
			$scope.login = function() {
				
				$http.post(
						"rest/"+$scope.type+"/login/" + $scope.user + "/" + $scope.pass)
						.success(function(response) {
							if (response == "success"){
								$rootScope.username = $scope.user;
								switch ($scope.type) 
								{
								case "customer":
									$location.path("/CustomerPage");
									break; 				
								case "company":
									$location.path("/CompanyPage");
									break;
								case "admin":
									$location.path("/adminPage");
									break;
								default:
									$location.path("/");
									break;
								}			
							}
							else {
								$scope.loginlog = response;
								alert("password or username is wrong");
							}
						});
			};
			
		});

app.controller(
		'adminController',
		function($scope, $http,$location,$rootScope) {
		// /////company/////////
			// get all companies
			$scope.viewAll = function() {
				
				$http.get("rest/admin/getAllCompanies").success(
						function(response) {
							$scope.companies = response;
							
						});
				
			};
			
			// get company
			$scope.viewcompany = function() {
				$http.get("rest/admin/GetCompany" + $scope.id).success(
						function(response) {
							$scope.companyfound = response;
						});
			};
			// new company
			$scope.getnew = function() {
				response=null;
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("company was successfully added"); 	 
				$http.post(
						"rest/admin/AddCompany/" + $scope.user + "/"
								+ $scope.mail + "/" + $scope.pass).success(
						function(response) {
							$scope.newcompany = response;
							
						
							
						});}
				else{
					alert("action cancelled"); 	
				}
			};
			// DeleteCompany
			$scope.erase = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					
				$http.delete("rest/admin/DeleteCompany/" + $scope.id).success(
						function(response) {
							$scope.deletecompany = response;
							$console.log(response);
							if(response=="company was removed"){
								alert("company was successfully deleted");  
							}
							else
								{
								alert("something went wrong"); 	
								}
						});}
				else{
					alert("action cancelled"); 	
				}
			};
			// updateCompany
			$scope.update = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("company was successfully updated");
				$http.put(
						"rest/admin/UpdateCompany/" + $scope.id + "/"
								+ $scope.pass + "/" + $scope.mail).success(
						function(response) {
							$scope.updatecompany = response;
							
						});}
				else{
					alert("company wasnt successfully updated"); 	
				}
			};
			$scope.viewAllIncome = function() {
				$http.get(
						"rest/company/viewAllIncome").success(
						function(response) {
							$scope.allincome= response;
						});
			};
			$scope.viewCompanyIncome = function() {
				$http.get(
						"rest/company/viewCompanyIncome/"+$scope.id).success(
						function(response) {
							$scope.companyincome= response;
						});
			};
// /////customer/////////
			// get all customers
			$scope.viewAllcust = function() {
				$http.get("rest/admin/getAllCustomers").success(
						function(response) {
							$scope.customers = response;
						});
				
			};
			// get customer
			$scope.viewcustomer = function() {
				$http.get("rest/admin/GetCustomer" + $scope.id).success(
						function(response) {
							$scope.customer = response;
						});
			};
			// new customer
			$scope.getnewcust = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("customer was successfully added");
				$http.post(
						"rest/admin/AddCustomer/" + $scope.CustName + "/"
								+ $scope.pass).success(
						function(response) {
							$scope.newcustomer= response;
							
						});}
				else{
					alert("customer wasnt successfully added"); 	
				}
			};
			// DeleteCustomer
			$scope.erasecustomer = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("customer was successfully deleted");
				$http.delete("rest/admin/DeleteCustomer/" + $scope.id).success(
						function(response) {
							$scope.deletecustomer= response;
							
						});}
				else{
					alert("customer wasnt successfully deleted"); 	
				}
			};
			// updateCustomer
			$scope.updatecustomer = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					
				$http.put(
						"rest/admin/UpdateCustomer/" + $scope.id + "/"
								+ $scope.pass ).success(
						function(response) {
							$scope.updatecustomer = response;
							if(response=="update complited"){
								$window.alert("customer was successfully updated");
							}
							else{
								$window.alert("customer was not updated id does not exist");
							}
						});}
				else{
					alert("you canceled the action"); 	
				}
			};
			$scope.viewCustomerIncome = function() {
				$http.get(
						"rest/company/viewCustomerIncome/"+$scope.id).success(
						function(response) {
							$scope.customerincome= response;
						});
			};
			function myFunction() {
			    document.getElementById("myForm").reset();
			    document.getElementById("myForm1").reset();
			    document.getElementById("myForm3").reset();
			}
			
		});

// ///////company service////////
app.controller(
		'companyController',
		function($scope, $http,$location,$rootScope,$window) {
		// /////company/////////
			// get all coupons
			$scope.viewAllcoupons = function() {
				$http.get("rest/company/getAllCoupons").success(
						function(response) {
							$scope.coupons = response;
							
						});
			};
			
			// new coupon
			$scope.getnewcoup = function() {

				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("coupon was successfully added");
				$http.post(
						"rest/company/createCoupon/" + $scope.amount+ "/" + $scope.startD+ "/"+ $scope.startM+ "/"+ $scope.startY+ "/" + $scope.endD+ "/" + $scope.endM+ "/"  + $scope.endY+ "/"+ $scope.message+ "/" + $scope.title+ "/" + $scope.type+ "/" + $scope.price).success(
						function(response) {
							$scope.newcoup= response;
							
						});}
				else{
					alert("coupon wasnt successfully added"); 	
				}
			};
			// DeleteCoupon
			$scope.erasecoupon = function() {

				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("coupon was successfully removed");
				$http.delete("rest/company/removeCoupon/" + $scope.id).success(
						function(response) {
							$scope.deletecoupon= response;
							
						});}
				else{
					alert("coupon wasnt successfully removed"); 	
				}
			};
			// updateCoupon
			$scope.updatecoupon = function() {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
					alert("coupon was successfully updated");
				$http.put(
						"rest/company/updateCoupon/"+ $scope.id+ "/"+ $scope.amount+ "/" + $scope.endD+ "/" + $scope.endM+ "/"  + $scope.endY+ "/"+ $scope.message+ "/" + $scope.price).success(
						function(response) {
							$scope.updatecoupon = response;
							
						});}
			else{
				alert("coupon wasnt successfully updated"); 	
			}
			};
			$http.get("rest/company/getTypes").success(function(response){
				$scope.types=response.stringWrapper;
			});
			// get coupon by price
			$scope.byprice = function() {
				$http.get(
						"rest/company/ById/" + $scope.id).success(
						function(response) {
							$scope.byprice= response;
						});
			};
			// get coupon by type
			$scope.bytype = function() {
				$http.post(
						"rest/company/ByType/" + $scope.type).success(
						function(response) {
							$scope.getbytype= response;
						});
			};
			$scope.bydate = function() {
				$http.get(
						"rest/company/Bydate/" + $scope.year+"/"+$scope.month+"/"+$scope.day).success(
						function(response) {
							$scope.getbydate= response;
						});
			};
			$scope.getIncome = function() {
				$http.get(
						"rest/company/viewIncome").success(
						function(response) {
							$scope.income= response;
						});
			};
		});
// ///////customer service////////
app.controller(
		'customerController',
		function($scope, $http,$location,$rootScope) {
		// /////company/////////
			// get all coupons
			$scope.viewAllcustomercoupons = function() {
				$http.get("rest/customer/getAllCoupons").success(
						function(response) {
							$scope.custcoupons = response;
							
						});
			};
			$scope.viewtotalcoupons = function() {
				$http.get("rest/customer/gettotalCoupons").success(
						function(response) {
							$scope.totalcoupons = response;
							
						});
			};
			// get coupon by price
			$scope.getcoupbyprice = function() {
				$http.post(
						"rest/customer/ByPrice/" + $scope.price).success(
						function(response) {
							$scope.getcoup= response;
						});
			};
			// get coupon by type
			$scope.getcoupbyTYPE = function() {
				$http.post(
						"rest/customer/ByType/" + $scope.type).success(
						function(response) {
							$scope.getcoupTYPE= response;
						});
			};
			// purchaseCoupon
			$scope.purchaseCoupon = function($window) {
				var theAnswer = confirm("are you sure?");
				if(theAnswer){
				
				$http.put("rest/customer/purchaseCoupon/" + $scope.id).success(function(response) {
							$scope.purchaseCoupon = response.data;
							if(response.data=="Coupon was purchased successfuly."){
								alert("coupon was successfully purchased");
							}
							else
								{
								alert("something went wrong");
								}
							
						})
						;
				}
				else{
					alert("action cancelled"); 	
				}
			};
			$http.get("rest/customer/getTypes").success(function(response){
				$scope.types=response.stringWrapper;
			});
			
		});
app.controller("uploadImage", function($scope,$http,$location,$routeParams,$rootScope) {
	
	chkLogin($http, $location, $rootScope, "company");
	$rootScope.companyNavBar = true;
	$rootScope.adminNavBar = false;
	$rootScope.mainCompany = true;
	$rootScope.customerNavBar = false;

	$scope.file = null;
	
	var file;
	// retrieve the coupon's id from URL
	var title = $routeParams.title;	
	
	$scope.result = "";		
	refreshImg();	
	
	// get the file name from the controller (used by library)
	$scope.$watch('file', function (newVal) {
       if (newVal) {
         file = newVal;        
       }
     });
	
    // send the image to the server via form (file validation is triggered by
	// value change)
	$scope.show = function() {
    	 var fd = new FormData();
         fd.append('file', file);
         fd.append('id',id);

         $http.post("rest/company/imageUpload",
        		   fd, {
        	 				transformRequest: angular.identity, 
        	 				headers: {'Content-Type': undefined 
        	 			}
        }).success(function(response) {
        	$scope.result = "File was successfuly loaded.";
        	refreshImg();
        }).error(function(response){
        	$scope.result = "Something went wrong, try again";
        });         

     }
     
     function refreshImg(){
    	 $http.get("rest/company/ById/"+id).success(function(response){		
    			$scope.coupon = response;
    			if ($scope.coupon == "") { // someone tries to sniff coupons
											// from address... cause login.
    				$location.path("/");
    			}
    		}).error(function(response){
    		});
     }
});

function myFunction() {
    document.getElementById("myForm").reset();
    document.getElementById("myForm1").reset();
    document.getElementById("myForm3").reset();
}
function myFunction1() {
    
    location.reload();
}

// file extention validation
function validateFile(file) {
	  
	  // Function designed to permit only images file to be uploaded to
		// server.
	  
	  // allowd files extentions:
	  var allowed = ["jpg","jpeg","gif","png","bmp"];
	  
	  // get file name from control
	  var fullPath = document.getElementById('fileInput').value;
	  if (fullPath) {
	  	var startIndex = (fullPath.indexOf('\\') >= 0 ? fullPath.lastIndexOf('\\') : fullPath.lastIndexOf('/'));
	  	var filename = fullPath.substring(startIndex);
	  	if (filename.indexOf('\\') === 0 || filename.indexOf('/') === 0) {
	  		filename = filename.substring(1);
	  	}
	  	
	  	// get file extention
	  	var ext = filename.substr(filename.lastIndexOf(".")+1);
	  	
	  	// chk if extention is allowed (exists in array) - -1 will be return
		// when false
	  	if (allowed.indexOf(ext.toLowerCase()) < 0)
	  	{	  		
	  		document.getElementById("uploadBTN").disabled = true;
	  		document.getElementById("error").style.visibility = "visible";
	  	}
	  	else{
	  		document.getElementById("uploadBTN").disabled = false;
	  		document.getElementById("error").style.visibility = "hidden";
	  	}
	  }
}
