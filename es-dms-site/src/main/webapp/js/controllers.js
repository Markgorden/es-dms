﻿simpleApp.controller('MainCtrl', function ($scope, $window, $location) {
    $scope.$location = $location;
});

simpleApp.controller('adminController', function ($scope) {
});

simpleApp.controller('userController', function ($scope, userService) {
    $scope.users = [];
    var currentUser = null;
    
    init();

    function init() {
    }

    $scope.find = function() {
    	$scope.users = userService.find($scope.criteria);
    };
    
    $scope.edit = function(id) {
    	userService.edit(id);
    };
    
    $scope.add = function () {
//        var user = new userService();
//        user.name = $scope.name;
//        user.city = $scope.city;
//        user.email = $scope.email
//        user.$save(); 
//        $scope.user.push(user);
    };
});

simpleApp.controller('userEditController', function ($scope, $rootScope, $http, userService) {
	$scope.user = null;
	$scope.newUser = false;
	$scope.uid = '';
	$scope.user = {};
	$scope.pw1 = '';
	$scope.pw2 = '';
	$scope.pwError = false;
	$scope.incomplete = false;
	  
	$rootScope.$on('user:edit', function() {
		var editUser = userService.currentUser();
	    if (editUser.id) {
	    	$scope.user = editUser;
	    	$scope.newUser = false;
	    	//$scope.uid = editUser.id;
	    } else {
	    	$scope.newUser = true;
	    	$scope.incomplete = true;
	    	$scope.user = {};
	    	$scope.pw1 = '';
	    	$scope.pw2 = '';
	    }
	});
	
	$scope.save = function() {
		if ($scope.newUser) {
			$scope.user.password = $scope.pw1;
		}
		userService.save($scope.user);
	};
	
	$scope.$watch('pw1', function() {
		if ($scope.pw1 !== $scope.pw2) {
			$scope.pwError = true;
		} else {
			$scope.pwError = false;
	    }
	    $scope.incompleteTest();
	});
	
	$scope.$watch('pw2', function() {
		if ($scope.pw1 !== $scope.pw2) {
			$scope.pwError = true;
		} else {
			$scope.pwError = false;
		}
	    $scope.incompleteTest();
	});

	$scope.$watch('username', function() {
		$scope.incompleteTest();
	});
	  
	$scope.incompleteTest = function() {
		if ($scope.newUser) {
			if (!$scope.user.name.length || !$scope.pw1.length || !$scope.pw2.length) {
				$scope.incomplete = true;
			} else {
				$scope.incomplete = false;
			}
		} else {
			$scope.incomplete = false;
	    }
	};
});

simpleApp.controller('roleController', function ($scope) {
});

simpleApp.controller('simpleController', function ($scope, userService) {
    $scope.customers = [];

    init();

    function init() {
        //$scope.customers = userService.query({ verb: 'find', name: '*' });
    	$scope.customers = userService.find('*');
    }
    $scope.addCustomer = function () {
        var user = new userService();
        user.name = $scope.newCustomer.name;
        user.city = $scope.newCustomer.city;
        user.$save(); 
        $scope.customers.push({
            name: $scope.newCustomer.name,
            city: $scope.newCustomer.city
        });
    };
});

simpleApp.controller('loginController', function ($scope, /*$cookieStore,*/ authenticationService, authService, sharedService) {

    $scope.shouldBeOpen = true;

    $scope.login = function() {
    	console.log('loginController - login');
    	authenticationService.login($scope.username, $scope.password, $scope.rememberMe, function(data) {
    		console.log('data: ' + data);
//    		var response = JSON.parse(data);
//    		console.log('response: ' + response);
    		if (data.status === 'AUTHENTICATED') {
        		authService.loginConfirmed();
        		var token = data.token;
        		console.log(token);
//        		$cookieStore.put('_ES_DMS_TICKET', unescape(token))
//        		console.log('Cookie ES_DMS_TICKET: ' + $cookieStore.get('_ES_DMS_TICKET'));
        		$scope.shouldBeOpen = false;
        		sharedService.prepForBroadcast({logout: true});
    		}
    	});
    };

    $scope.open = function () {
        $scope.shouldBeOpen = true;
    };

    $scope.close = function () {
        $scope.shouldBeOpen = false;
    };
});

simpleApp.controller('documentController', function ($scope, documentService) {
    $scope.documents = [];

    init();

    function init() {
        //$scope.documents = documentService.query({ verb: 'find', name: '*' });
    }
    
    $scope.search = function() {
    	$scope.documents = documentService.query({ verb: 'search', name: $scope.criteria });
    }
});

simpleApp.controller('AlertDemoCtrl', function ($scope) {
    $scope.alerts = [
    { type: 'error', msg: 'Oh snap! Change a few things up and try submitting again.' },
    { type: 'success', msg: 'Well done! You successfully read this important alert message.' }
  ];

    $scope.addAlert = function () {
        $scope.alerts.push({ msg: "Another alert!" });
    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

});

simpleApp.controller('newDocumentController', function ($scope) {
	$scope.showAlert = false;
	$scope.alert = {};
	$scope.newDocument = {};
    $scope.uploadComplete = function (content, completed) {
        if (completed && content.length > 0)
        {
        	var response = JSON.parse(content);
        	console.log(response);
            
            // Clear form (reason for using the 'ng-model' directive on the
			// input elements)
            $scope.newDocument.name = '';
            $scope.newDocument.date = '';
            // $scope.newDocument.id = response.id;
            $scope.alert = { type: 'success', msg: 'New document created #' + response.id};
            $scope.showAlert = true;
            // Look for way to clear the input[type=file] element
        }
    };
    $scope.closeAlert = function() {
    	$scope.showAlert = false;
    	$scope.alert = {};
    };
});

simpleApp.controller('modalCtrl', function ($scope) {
	$scope.open = function() {
		$scope.shouldBeOpen = true;
	};
	$scope.close = function () {
		$scope.closeMsg = 'I was closed at: ' + new Date();
	    $scope.shouldBeOpen = false;
	};
	$scope.opts = {
			backdropFade: true,
			dialogFade:true
	};

	$scope.testHttp401 = function() {
		console.log('broadcast - event:auth-loginRequired');
		$scope.$broadcast('event:auth-loginRequired');
	};
	
});

simpleApp.controller('navbarController', function ($scope, sharedService) {
	$scope.showLogout = false;
	$scope.$on('handleBroadcast', function() {
        $scope.showLogout = sharedService.message.logout;
    }); 
	
	$scope.tabs = [
        { "view": "/view1", title: "View #1" },
        { "view": "/view2", title: "View #2" },
        { "view": "/search-view", title: "Search" },
        { "view": "/edit-view", title: "Edit" },
        { "view": "/view3", title: "Test View" }
    ];
	
	$scope.adminTabs = [
		 { "view": "/admin/users", title: "Users" },
		 { "view": "/admin/roles", title: "Roles" }
	];
});

