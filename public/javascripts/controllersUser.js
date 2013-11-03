'use strict';

function UserSignInCtrl($scope, $routeParams, UserSignIn, Util) {

	$scope.update = function(game) {
		// TODO avoid
		game.laundry = "true" === game.laundry;
		UserSignIn.save({
			id : game.id
		}, game);
	};

	$scope.games = UserSignIn.query();
	if ($scope.games.length == 0) {
		$scope.msg = "Aktuell gibt es keine Spiele zu denen sie sich anmelden können";
	} else {
		$scope.msg = undefined;
	}
}
UserSignInCtrl.$inject = [ '$scope', '$routeParams', 'UserSignIn', 'Util' ];

function UserContactsCtrl($scope, $routeParams, UserContacts, Util) {
	$scope.contacts = UserContacts.query();
	if ($scope.contacts.length == 0) {
		$scope.msg = "Keine Kontakte vorhanden";
	} else {
		$scope.msg = undefined;
	}
}
UserContactsCtrl.$inject = [ '$scope', '$routeParams', 'UserContacts', 'Util' ];

function UserContactCtrl($scope, $routeParams, UserContact, Util) {
	$scope.update = function(contact) {
		UserContact.save({}, contact);
	};
	$scope.contact = UserContact.get();
}
UserContactCtrl.$inject = [ '$scope', '$routeParams', 'UserContact', 'Util' ];

function UserPasswordCtrl($scope, $routeParams, $http, Util) {
	$scope.update = function(pw1) {
		$http.post('/api/user/password', {
			password : pw1
		}).success(function(data, status, headers, config) {
			console.log('ok saving pwd')
		}).error(function(data, status, headers, config) {
			console.log('error saving pwd')
		});
	};
}
UserPasswordCtrl.$inject = [ '$scope', '$routeParams', '$http', 'Util' ];
