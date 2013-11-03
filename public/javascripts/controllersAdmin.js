'use strict';
/* App Controllers */
function NoopCtrl() {
}
NoopCtrl.$inject = [];

function AdminGameCtrl($scope, $routeParams, $location, Game, Util) {

	// Select directive
	$scope.selects = Util.memberships
	$scope.seasons = Util.seasons;

	// $scope.selects = Util.selects;
	$scope.update = function(game) {
		before(game);
		game.$save({
			id : game.id
		}, function(successResult) {
			$scope.successAlert('Spiel', 'Erfolgreich gespeichert');
			//$location.path('/admin/spiele/' + game.id);
			after(game);
		}, function(errorResult) {
			$scope.errorAlert('Spiel', 'Fehler beim Speichern');
			after(game);
		});
	};

	function before(game) {
		game.start = Util.combineDateTime(game.startDate, game.startTime)
				.getTime();
		game.homematch = ("true" === game.homematch);
	}
	
	function after(game) {
		game.startDate = Util.millisToDate(game.start);
		game.startTime = Util.millisToTime(game.start);
		if (game.homematch) {
			game.homematch = "true";
		} else {
			game.homematch = "false";
		}

	}

	$scope.create = function(game) {
		before(game);
		game.$save({}, function(successResult) {
			$scope.successAlert('Spiel', 'Erfolgreich angelegt');
			$location.path('/admin/spiele/' + game.id);
		}, function(errorResult) {
			$scope.errorAlert('Spiel', 'Fehler beim Anlegen');
		});
	};

	$scope.remove = function(game) {
		var membership = game.membership;
		var season = game.season;
		game.$remove({
			id : game.id
		}, function(successResult) {
			$scope.successAlert('Termin', 'Erfolgreich gelöscht');
			$location.path('/admin/spiele/' + membership + '/' + season);
		}, function(errorResult) {
			$scope.errorAlert('Termin', 'Fehler beim Löschen');
		});

	};

	if ($routeParams.id) {
		$scope.game = Game.get({
			id : $routeParams.id
		}, function() {
			if (!$scope.game.id) {
				$scope.msg = "There is no game for id " + $routeParams.id;
			} else {
				console.log("start " + $scope.game.start);
				/*
				$scope.game.startDate = Util.millisToDate($scope.game.start);
				$scope.game.startTime = Util.millisToTime($scope.game.start);
				if ($scope.game.homematch) {
					$scope.game.homematch = "true";
				} else {
					$scope.game.homematch = "false";
				}
				*/
				after($scope.game);
				$scope.msg = undefined;
			}
		});
	} else {
		console.log("Create new data");
		$scope.game = new Game();
	}
}
AdminGameCtrl.$inject = [ '$scope', '$routeParams', '$location', 'Game', 'Util' ];

function AdminGamesCtrl($scope, $routeParams, $location, Game, GameTeam, Util) {
	$scope.seasons = Util.seasons;
	$scope.teams = Util.memberships;

	if ($routeParams.team && $routeParams.season) {
		$scope.team = $routeParams.team;
		$scope.season = $routeParams.season;
		$scope.games = GameTeam.get({
			team : $routeParams.team,
			season : $routeParams.season
		}, {});
	}

	$scope.update = function(season) {
		$location.path('/admin/spiele/' + $scope.team + '/' + $scope.season);
	};
}
AdminGamesCtrl.$inject = [ '$scope', '$routeParams', '$location', 'Game',
		'GameTeam', 'Util' ];

function AdminGamesSignInCtrl($scope, $routeParams, GameSignInTeam) {
	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.games = GameSignInTeam.get({
			team : $routeParams.team
		}, {});
	}
}
AdminGamesSignInCtrl.$inject = [ '$scope', '$routeParams', 'GameSignInTeam' ];

function AdminGameSignInCtrl($scope, $routeParams, GameSignIn) {
	$scope.updateSignIn = function(player) {
		// TODO avoid
		player.laundry = "true" === player.laundry;
		GameSignIn.save({
			id : $scope.game.id
		}, player);
	};
	$scope.updateLaundry = function(player) {
		// TODO avoid
		player.laundry = "true" === player.laundry;
		// change other players laundry only if a another player takes over the
		// laundry task
		if (player.laundry) {
			for ( var i = 0; i < $scope.game.players.length; i++) {
				if ($scope.game.players[i].pid != player.pid) {
					$scope.game.players[i].laundry = !player.laundry;
				}
			}
		}
		GameSignIn.save({
			id : $scope.game.id
		}, player);
	};

	if ($routeParams.id) {
		$scope.game = GameSignIn.get({
			id : $routeParams.id
		}, {});
	}
}
AdminGameSignInCtrl.$inject = [ '$scope', '$routeParams', 'GameSignIn' ];

function AdminPersonCtrl($scope, $routeParams, $location, $http, Person, Util,
		Children) {

	// Select directive
	$scope.teams = Util.memberships;
	$scope.roles = Util.roles;
	console.log("Fetching children");
	$scope.selectsChildren = Children.query();
	$scope.selectChildren = undefined;

	$scope.resetPassword = function(person) {
		$http.post('/api/persons/passwordReset', {
			id : person.id
		}).success(function(data, status, headers, config) {
			$scope.successAlert('Passwort', 'Erfolgreich zurückgesetzt');
			console.log('ok reseting pwd')
		}).error(
				function(data, status, headers, config) {
					$scope.errorAlert('Passwort',
							'Passwort konnte nicht zurückgesetzt werden');
					$scope.alert = '';
					console.log('error reseting pwd')
				});
	}
	$scope.error = function() {
		$http.post('/api/nowhere', {
			none : 'sense'
		}).success(function(data, status, headers, config) {
			console.log('???')
			$scope.successAlert('???', '???');
		}).error(function(data, status, headers, config) {
			console.log('provoked alert')
			$scope.errorAlert('Dummy', 'Dummy Alert');
		});
	}

	$scope.update = function(person) {
		console.log("Updating " + person);
		if (person.birthdayDate) {
			person.birthday = person.birthdayDate.getTime();
		}
		if (person.player) {
			if (person.player.joiningDate) {
				person.player.joining = person.player.joiningDate.getTime();
			}
		}
		Person.save({
			id : person.id
		}, person);
	};

	$scope.create = function(person) {
		console.log("Creating " + person)
		if (person.birthdayDate) {
			person.birthday = person.birthdayDate.getTime();
		}
		if (person.player) {
			if (person.player.joiningDate) {
				person.player.joining = person.player.joiningDate.getTime();
			}
		}
		person.$save({}, function(successResult) {
			$scope.successAlert('Person', 'Erfolgreich angelegt');
			$location.path('/admin/personen/' + person.id);
		}, function(errorResult) {
			$scope.errorAlert('Person', 'Fehler beim Anlegen');
		});

	};

	$scope.remove = function(person) {
		console.log("Delete " + person)
		person.$remove({
			id : person.id
		});
		$location.path('/admin/spieler/' + person.memberships[0]);

	};

	if ($routeParams.id) {
		$scope.person = Person.get({
			id : $routeParams.id
		}, function() {
			if (!$scope.person.id) {
				$scope.msg = "There is no person for id " + $routeParams.id;
			} else {
				if ($scope.person.birthday) {
					$scope.person.birthdayDate = new Date(
							$scope.person.birthday);
				}
				if ($scope.person.player) {
					if ($scope.person.player.joining) {
						$scope.person.player.joiningDate = new Date(
								$scope.person.player.joining);
					}
				}
				$scope.msg = undefined;
			}
		});
	} else {
		console.log("Create new data");
		$scope.person = new Person();
	}
}
AdminPersonCtrl.$inject = [ '$scope', '$routeParams', '$location', '$http',
		'Person', 'Util', 'Children' ];

function AdminPlayersCtrl($scope, $routeParams, Player, PlayerTeam, Util) {
	$scope.teams = Util.memberships;
	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.players = PlayerTeam.get({
			team : $routeParams.team
		}, {});
	} else {
		$scope.players = Player.query();
	}
}
AdminPlayersCtrl.$inject = [ '$scope', '$routeParams', 'Player', 'PlayerTeam',
		'Util' ];

function AdminParentsCtrl($scope, $routeParams, Parent) {
	$scope.parents = Parent.query();
}
AdminParentsCtrl.$inject = [ '$scope', '$routeParams', 'Parent' ];

function AdminEventCtrl($scope, $routeParams, $location, Event, Util) {

	// Select directive
	$scope.memberships = Util.memberships;
	$scope.seasons = Util.seasons;
	$scope.eventTypes = Util.eventTypes;

	$scope.update = function(event) {
		event.starts[0] = Util
				.combineDateTime(event.startDate, event.startTime).getTime();
		Event.save({
			id : event.id
		}, event);
	};

	$scope.create = function(event) {
		event.starts = new Array();
		event.starts[0] = Util
				.combineDateTime(event.startDate, event.startTime).getTime();
		event.$save({}, function(successResult) {
			$scope.successAlert('Termin', 'Erfolgreich angelegt');
			$location.path('/admin/termine/' + event.id);
		}, function(errorResult) {
			$scope.errorAlert('Termin', 'Fehler beim Anlegen');
		});
	};

	$scope.remove = function(event) {
		var membership = event.memberships[0];
		var season = event.season;
		event.$remove({
			id : event.id
		}, function(successResult) {
			$scope.successAlert('Termin', 'Erfolgreich gelöscht');
			$location.path('/admin/termine/' + membership + '/' + season);
		}, function(errorResult) {
			$scope.errorAlert('Termin', 'Fehler beim Löschen');
		});
	};

	if ($routeParams.id) {
		$scope.event = Event.get({
			id : $routeParams.id
		}, function() {
			if (!$scope.event.id) {
				$scope.msg = "There is no event for id " + $routeParams.id;
			} else {
				$scope.event.startDate = new Date($scope.event.starts[0]);
				$scope.event.startTime = Util
						.millisToTime($scope.event.starts[0]);
				$scope.msg = undefined;
			}
		});
	} else {
		console.log("Create new data");
		$scope.event = new Event();
	}
}
AdminEventCtrl.$inject = [ '$scope', '$routeParams', '$location', 'Event',
		'Util' ];

function AdminEventsCtrl($scope, $routeParams, $location, Event, EventTeam,
		Util) {
	$scope.seasons = Util.seasons;
	$scope.teams = Util.memberships;

	if ($routeParams.team && $routeParams.season) {
		$scope.team = $routeParams.team;
		$scope.season = $routeParams.season;
		$scope.events = EventTeam.get({
			team : $routeParams.team,
			season : $routeParams.season
		}, {});
	} else {
		$scope.events = Event.query();
	}

	$scope.update = function(season) {
		$location.path('/admin/termine/' + $scope.team + '/' + $scope.season);
	};
}
AdminEventsCtrl.$inject = [ '$scope', '$routeParams', '$location', 'Event',
		'EventTeam', 'Util' ];

function AdminNewsitemCtrl($scope, $routeParams, News, Util) {

	// Select directive
	$scope.selectsMemberships = Util.memberships;

	$scope.update = function(newsitem) {
		News.save({
			id : newsitem.id
		}, newsitem);
	};

	$scope.create = function(newsitem) {
		newsitem.id = "-1";
		News.save({}, newsitem);
	};

	$scope.remove = function(newsitem) {
		newsitem.$remove({
			id : newsitem.id
		});
	};

	if ($routeParams.id) {
		$scope.newsitem = News.get({
			id : $routeParams.id
		}, function() {
			if (!$scope.newsitem.id) {
				$scope.msg = "There is no newsitem for id " + $routeParams.id;
			} else {
				$scope.msg = undefined;
			}
		});
	} else {
		console.log("Create new data");
		$scope.newsitem = new News();
	}
}
AdminNewsitemCtrl.$inject = [ '$scope', '$routeParams', 'News', 'Util' ];

function AdminNewsCtrl($scope, $routeParams, News, NewsTeam) {
	if ($routeParams.team) {
		$scope.team = $routeParams.team;
		$scope.newsitems = NewsTeam.get({
			team : $routeParams.team
		}, {});
	} else {
		$scope.newsitems = News.query();
	}
}
AdminNewsCtrl.$inject = [ '$scope', '$routeParams', 'News', 'NewsTeam' ];

function HallOfFameController($scope, $routeParams, HallOfFame) {
	if ($routeParams.level) {
		$scope.level = $routeParams.level;
		$scope.persons = HallOfFame.get({
			level : $routeParams.level
		}, {});
	} else {
		console.log("No hall of fame param!")
	}
}
HallOfFameController.$inject = [ '$scope', '$routeParams', 'HallOfFame' ]
