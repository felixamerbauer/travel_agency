'use strict';

function MainCtrl($scope, $route, $routeParams, $location, $http, Ticker,
		TickerTeam) {
	$scope.$route = $route;
	$scope.$location = $location;
	$scope.$routeParams = $routeParams;
	$scope.loggedIn = false;
	$scope.isAdmin = false;
	$scope.username = 'felix.amerbauer';
	$scope.password = 'fctorpedo03';
	$scope.rememberme = false;

	$scope.alerts = [];
	function addAlert(type, title, content) {
		if ($scope.alerts.length > 1) {
			$scope.alerts.splice(0, 1);
		}
		$scope.alerts.push({
			type : type,
			title : title,
			content : content
		});
	}

	$scope.successAlert = function(title, content) {
		addAlert('success', title, content);
	};
	$scope.errorAlert = function(title, content) {
		addAlert('error', title, content);
	};

	checkLogin();

	// snip
	function checkLogin() {
		var cookies = document.cookie.split('; ');
		for ( var i = 0; i < cookies.length; i++) {
			var cookie = cookies[i].split("=")
			if ((cookie.length == 2) && (cookie[0] == "authToken")) {
				console.log('fetching /isUser to check if admin')
				$http.get('/isUser', {
				}).success(function(data, status, headers, config) {
					console.log('The Force is not too bad this one.')
					$scope.loggedIn = true;
				}).error(function(data, status, headers, config) {
					console.log('noob')
				});
				// check if admin
				$http.defaults.headers.common['X-AUTH-TOKEN'] = cookie[1]
				console.log('fetching /isAdmin to check if admin')
				$http.get('/isAdmin', {
				}).success(function(data, status, headers, config) {
					console.log('The Force is strong with this one.')
					$scope.isAdmin = true;
				}).error(function(data, status, headers, config) {
					console.log('plain old user am i')
				});
			}
		}
	}

	$scope.login = function(username, password, rememberme) {
		$http.post('/login', {
			username : username,
			password : password,
			rememberme : rememberme
		}).success(function(data, status, headers, config) {
			$scope.loggedIn = true;
			$http.defaults.headers.common['X-AUTH-TOKEN'] = data.authToken;
			console.log('data.admin ' + data.admin)
			$scope.isAdmin = data.admin;
			console.log('scope.isAdmin ' + $scope.isAdmin);
			$scope.successAlert('Anmelden', 'Erfolgreich');
			$location.path('/mitgliederbereich/anabmeldung')
		}).error(function(data, status, headers, config) {
			$scope.errorAlert('Anmelden', 'Fehlgeschlagen');
		});
	}

	$scope.logout = function() {
		console.log('Logout');
		$http.post('/logout', {}).success(
				function(data, status, headers, config) {
					$scope.loggedIn = false;
					$scope.isAdmin = false;
					$http.defaults.headers.common['X-AUTH-TOKEN'] = undefined;
					$location.path('/');
				}).error(function(data, status, headers, config) {
			$http.defaults.headers.common['X-AUTH-TOKEN'] = undefined;
			console.log('error logout')
		});
	}

	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.tickers = TickerTeam.get({
			team : $routeParams.team
		}, {});
	} else {
		$scope.tickers = Ticker.query();
	}
}
MainCtrl.$inject = [ '$scope', '$route', '$routeParams', '$location', '$http',
		'Ticker', 'TickerTeam' ];

// controllers for unrestricted pages
function OtherGamesCtrl($scope, $routeParams, $location, Game, GameTeam, Util) {
	$scope.seasons = Util.seasons;
	$scope.teams = Util.memberships;

	if ($routeParams.team && $routeParams.season) {
		$scope.team = $routeParams.team;
		$scope.season = $routeParams.season
		$scope.games = GameTeam.get({
			team : $routeParams.team,
			season : $routeParams.season
		}, {});
	} else {
		$scope.games = Game.query();
	}

	$scope.update = function(season) {
		$location.path('/spiele/' + $scope.team + '/' + $scope.season);
	};
}
OtherGamesCtrl.$inject = [ '$scope', '$routeParams', '$location', 'Game',
		'GameTeam', 'Util' ];

function OtherNewsCtrl($scope, $routeParams, NewsDetails, NewsDetailsTeam) {
	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.newsitems = NewsDetailsTeam.get({
			team : $routeParams.team
		}, {});
	} else {
		$scope.newsitems = NewsDetails.query();
	}
}
OtherNewsCtrl.$inject = [ '$scope', '$routeParams', 'NewsDetails',
		'NewsDetailsTeam' ];

function OtherTickerCtrl($scope, $routeParams, Ticker, TickerTeam) {
	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.tickers = TickerTeam.get({
			team : $routeParams.team
		}, {});
	} else {
		$scope.tickers = Ticker.query();
	}
}
OtherTickerCtrl.$inject = [ '$scope', '$routeParams', 'Ticker', 'TickerTeam' ];
