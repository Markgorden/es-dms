var esDmsApp = angular.module('esDmsApp', [
	'ui.bootstrap', 
	'ngResource', 
	'ngCookies', 
	'ngUpload', 
	'http-auth-interceptor',
	'blueimp.fileupload'
]);

esDmsApp.directive('authenticationDirective', function($dialog) {
	console.log('Start authenticationDirective');
    return {
      restrict: 'A',
      link: function(scope, elem, attrs) {
        
        var loginButton = elem.find('#loginButton');
        var main = elem.find('#content');
        
        scope.$on('event:auth-loginRequired', function() {
        	console.log('event:auth-loginRequired - Show login');
        	loginButton.click();
        });
        scope.$on('event:auth-loginConfirmed', function() {
        	console.log('event:auth-loginConfirmed - Hide login');
        });
      }
    }
});

esDmsApp.directive('esdmsTagging', function() {
	console.log('Start esdmsTagging');
    return {
        restrict: 'E',
        scope: { id: '=', tags: '=', newtag: '=' },
        template:
            '<div>' +
            '<div class="input-append">' +
            '<input class="span1" type="text" placeholder="New tag" ng-model="new_value"></input> ' +
            '<a class="btn" ng-click="add()"><i class="icon-plus"></i></a>' +
            '</div>' +
            '<div class="input-append">' +
            	'<button ng-repeat="(idx, tag) in tags" class="btn btn-info" ng-click="remove(idx)">{{tag}}</button>' +
            	'</div>' +
            '</div>',
        link: function ( $scope, $element ) {
            // FIXME: this is lazy and error-prone
            var input = angular.element( $element.children()[1] );
            
            // This adds the new tag to the tags array
            $scope.add = function() {
            	console.log('newtag: ' + $scope.new_value + ' for doc: ' + $scope.id);
        		$scope.$emit('document:addtag', {'id': $scope.id, 'tag': $scope.new_value});
                $scope.new_value = "";
            };
            
            // This is the ng-click handler to remove an item
            $scope.remove = function ( idx ) {
	        	$scope.$emit('document:removetag', {'id': $scope.id, 'tag': idx});
            };
            
            // Capture all keypresses
            input.bind( 'keypress', function ( event ) {
                // But we only care when Enter was pressed
                if ( event.keyCode == 13 ) {
                    // There's probably a better way to handle this...
                    $scope.$apply( $scope.add );
                }
            });
        }
    };
});

esDmsApp.config(function ($routeProvider) {
    $routeProvider
		.when('/login', {
		    controller: 'loginController',
		    templateUrl: 'views/authentication/login.html'
		})
		.when('/view1', {
		    controller: 'simpleController',
		    templateUrl: 'views/view1.html'
		})
		.when('/view2', {
		    controller: 'simpleController',
		    templateUrl: 'views/view2.html'
		})
		.when('/view3', {
		    controller: 'simpleController',
		    templateUrl: 'views/view3.html'
		})
		.when('/view4', {
		    controller: 'simpleController',
		    templateUrl: 'views/view4.html'
		})
		.when('/search-view', {
		    controller: 'documentController',
		    templateUrl: 'views/documents/search-documents.html'
		})
		.when('/my-documents-view', {
		    controller: 'documentController',
		    templateUrl: 'views/documents/my-documents-view.html'
		})
		.when('/edit-view', {
		    controller: 'documentController',
		    templateUrl: 'views/documents/edit-view.html'
		})
		.when('/admin/users', {
		    controller: 'adminController',
		    templateUrl: 'views/users/search-users.html'
		})
		.when('/admin/roles', {
		    controller: 'adminController',
		    templateUrl: 'views/roles/search-roles.html'
		})
		.otherwise({ redirectTo: '/search-view' })
});

esDmsApp.factory('sharedService', function($rootScope) {
    var sharedService = {};
    
    sharedService.message = '';

    sharedService.prepForBroadcast = function(msg) {
        this.message = msg;
        this.broadcastItem();
    };

    sharedService.broadcastItem = function() {
        $rootScope.$broadcast('handleBroadcast');
    };
    
//    sharedService.user = '';

    return sharedService;
});

esDmsApp.factory('userService', function ($rootScope, $resource, $http) {
	var resource = $resource('api/users/:verb/:name', {}, {});
	var userResource = $resource('api/users/:id/:action/:parameter' , {id:'@id'}, {
		metadata: {method:'GET', params: {action: 'metadata'}},
		update: {method:'PUT', params: {}}
	})
	
	var users = [];
	var editedUser = null;
	
	return {
		search: function(criteria, callback) {
			$http.get('api/users/search/' + criteria).success(function (data, status) {
				users = data.items;
				callback(data);
			});
		},
		edit: function(id) {
			console.log('edit user: ' + id);
			if (id == 'new') {
				editedUser = null;
			} else {
				editedUser = id;
			}
			$rootScope.$broadcast('user:edit');
		},
		delete: function(id) {
			console.log('delete document: ' + id);
			var user = new userResource.get({'id': id})
			user.$delete({'id': id});
		},
		currentUser: function() {
			if (editedUser) {
				for (i in users) {
					if (users[i].id == editedUser) {
						return users[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(user) {
			console.log('save user: ' + user);
			resource.save(user);
			if (!editedUser) {
				users.push(user);
			}
		}
	};
});

esDmsApp.factory('roleService', function ($rootScope, $resource, $http) {
	var resource = $resource('api/roles/:verb/:name', {}, {});
	var roles = [];
	var editedRole = null;
	
	return {
		search: function(criteria, callback) {
			$http.get('api/roles/search/' + criteria).success(function (data, status) {
				roles = data.items;
				callback(data);
			});
		},
		edit: function(id) {
			console.log('edit role: ' + id);
			if (id == 'new') {
				editedRole = null;
			} else {
				editedRole = id;
			}
			$rootScope.$broadcast('role:edit');
		},
		currentRole: function() {
			if (editedRole) {
				for (i in roles) {
					if (roles[i].id == editedRole) {
						return roles[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(role) {
			console.log('save role: ' + role);
			resource.save(role);
			if (!editedRole) {
				roles.push(role);
			}
		}
	};
});

esDmsApp.factory('documentService', function ($resource, $http) {
	var resource = $resource('api/documents/:verb/:name', {}, {
	});
	var documentResource = $resource('api/documents/:id/:action/:parameter' , {id:'@id'}, {
		checkout: {method:'POST', params: {action: 'checkout'}},
		checkin: {method:'POST', params: {action: 'checkin'}},
		preview: {method:'GET', params: {action: 'preview'}},
		metadata: {method:'GET', params: {action: 'metadata'}},
		update: {method:'PUT', params: {}}
	});
	return {
		find: function(first, pageSize, criteria, callback) {
			console.log('Document search ' + first + ' - ' + pageSize + ' - ' + criteria)
				$http.get('api/documents/search/' + criteria + '?fi=' + first + '&ps=' + pageSize).success(function (data, status) {
					callback(data);
		       });
		},
		edit: function(id) {
			console.log('edit document: ' + id);
		},
		checkout: function(id) {
			console.log('checkout document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$checkout({'id': id});
		},
		checkin: function(id) {
			console.log('checkin document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$checkin({'id': id});
		},
		addTag: function(id, tag, callback) {
			console.log('addTag document: ' + id + ' - tag: ' + tag);
			var document = new documentResource.metadata({'id': id}, function() {
				console.log('get document: ' + JSON.stringify(document))
				if (document.tags === undefined) {
					document.tags = [];
				}
				document.tags.push(tag);
				console.log('save document: ' + JSON.stringify(document))
				document.$update();
				callback(document);
			});
		},
		removeTag: function(id, tag, callback) {
			console.log('removeTag document: ' + id + ' - tag: ' + tag);
			var document = new documentResource.metadata({'id': id}, function() {
				console.log('get document: ' + JSON.stringify(document))
				if (document.tags === undefined) {
					document.tags = [];
				}
				document.tags.splice(tag, 1);
				console.log('save document: ' + JSON.stringify(document))
				document.$update();
				callback(document);
			});
		},
		delete: function(id) {
			console.log('delete document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$delete({'id': id});
		},
		preview: function(id, criteria, callback) {
			console.log('preview document: ' + id + ' - criteria: ' + criteria);
			var response = documentResource.preview({'id': id, 'cr': criteria/*, 'fs': 100*/}, function () {
				callback(response.content);
			});
		},
//		getDocument: function(id) {
//			for (i in documents) {
//				if (documents[i].id == id) {
//					return documents[i];
//				}
//			}
//		},
//		getIndexOf: function(id) {
//			for (i in documents) {
//				if (documents[i].id == id) {
//					return i;
//				}
//			}
//		}
	};
    
});

esDmsApp.factory('searchService', function ($http) {
	return {
		facetedSearch: function(first, pageSize, criteria, facet, filters, callback) {
			var payload = {facet: facet, filters: filters};
			var config = {
					headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
			$http.post('api/search/_facet_search/' + criteria + '?fi=' + first + '&ps=' + pageSize, payload, config).success(callback);
		},
	};
});

esDmsApp.factory('authenticationService', function ($http) {
	return {
		login: function(username, password, rememberMe, callback) {
			var payload = {username: username, password: password, rememberMe: rememberMe};
			var config = {
					headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
			$http.post('api/auth/login', payload, config).success(callback);
		},
		logout: function() {
			$http.post('api/auth/logout');
		}
	};
});
