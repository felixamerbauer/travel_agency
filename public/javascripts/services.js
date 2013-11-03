'use strict';

// Demonstrate how to register services
// In this case it is a simple constant service.
var services = angular.module('travel_agency.services', [ 'ngResource' ]);

services.value('version', '0.1');

services.factory('Game', function($resource) {
	return $resource('/api/games/:id');
});
// TODO avoid multiple resources for games if possible
services.factory('GameTeam', function($resource) {
	return $resource('/api/games/team/:team/season/:season', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team',
				season : 'season'
			},
			isArray : true
		}
	});
});

// TODO avoid multiple resources for games if possible
services.factory('GameSignInTeam', function($resource) {
	return $resource('/api/gamesignin/team/:team', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team'
			},
			isArray : true
		}
	});
});

// TODO avoid multiple resources for games if possible
services.factory('GameSignIn', function($resource) {
	return $resource('/api/gamesignin/spiel/:id', {}, {
		get : {
			method : 'GET',
			params : {
				id : 'id'
			},
			isArray : false
		}
	});
});

services.factory('Person', function($resource) {
	return $resource('/api/persons/:id');
});

services.factory('Player', function($resource) {
	return $resource('/api/players');
});

//services.factory('PlayerName', function($resource) {
//	return $resource('/api/playerNames');
//});

services.factory('Children', function($resource) {
	return $resource('/api/children');
});

// TODO avoid multiple resources for persons if possible
services.factory('PlayerTeam', function($resource) {
	return $resource('/api/players/:team', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team'
			},
			isArray : true
		}
	});
});

services.factory('Parent', function($resource) {
	return $resource('/api/parents');
});

services.factory('HallOfFame', function($resource) {
	return $resource('/api/halloffame/:level', {}, {
		get : {
			method : 'GET',
			params : {
				level : 'level'
			},
			isArray : true
		}
	});
});

services.factory('Event', function($resource) {
	return $resource('/api/events/:id');
});
// TODO avoid multiple resources for events if possible
services.factory('EventTeam', function($resource) {
	return $resource('/api/events/team/:team/season/:season', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team',
				season : 'season'
			},
			isArray : true
		}
	});
});

services.factory('Ticker', function($resource) {
	return $resource('/api/ticker/:id');
});
// TODO avoid multiple resources for events if possible
services.factory('TickerTeam', function($resource) {
	return $resource('/api/ticker/team/:team', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team'
			},
			isArray : true
		}
	});
});

services.factory('News', function($resource) {
	return $resource('/api/news/:id');
});
// TODO avoid multiple resources for news if possible
services.factory('NewsTeam', function($resource) {
	return $resource('/api/news/team/:team', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team'
			},
			isArray : true
		}
	});
});

services.factory('NewsDetails', function($resource) {
	return $resource('/api/newsdetails/:id');
});
// TODO avoid multiple resources for news if possible
services.factory('NewsDetailsTeam', function($resource) {
	return $resource('/api/newsdetails/team/:team', {}, {
		get : {
			method : 'GET',
			params : {
				team : 'team'
			},
			isArray : true
		}
	});
});

services.factory('UserSignIn', function($resource) {
	return $resource('/api/user/signin/:id');
});

services.factory('UserContacts', function($resource) {
	return $resource('/api/user/contacts:id');
});

services.factory('UserContact', function($resource) {
	return $resource('/api/user/contact/:id');
});

services.factory('UserPassword', function($resource) {
	return $resource('/api/user/password/:id');
});

services.factory('Util', function() {
	return {
		millisToDate : function(millis) {
			return new Date(millis);
		},
		millisToTime : function(millis) {
			var xdate = new XDate(millis);
			return xdate.toString('HH:mm');
		},
		// date object, time string
		combineDateTime : function(date, time) {
			var a = time.split(":")
			date.setHours(a[0])
			date.setMinutes(a[1])
			return date;
		},
		// memberships for selection
		memberships : [ {
			id : 'Torpedos',
			// name : '<i class="icon-star"></i>&nbsp;Torpedos'
			name : 'Torpedos'
		}, {
			id : 'Tiger',
			name : 'Tiger'
		}, {
			id : 'U6',
			name : 'U6'
		}, {
			id : 'U7',
			name : 'U7'
		}, {
			id : 'U8',
			name : 'U8'
		}, {
			id : 'U9',
			name : 'U9'
		}, {
			id : 'U10',
			name : 'U10'
		}, {
			id : 'U11',
			name : 'U11'
		} ],
		// memberships for selection
		seasons : [ {
			id : '1314',
			name : '2013 / 2014'
		}, {
			id : '1213',
			name : '2012 / 2013'
		}, {
			id : '1112',
			name : '2011 / 2012'
		}, {
			id : '1011',
			name : '2010 / 2011'
		}, {
			id : '0910',
			name : '2009 / 2010'
		}, {
			id : '1415',
			name : '2014 / 2015'
		} ],
		// event types for selection
		eventTypes : [ {
			id : 'Training',
			name : 'Training'
		}, {
			id : 'Sonstiges',
			name : 'Sonstiges'
		} ],
		// event types for selection
		roles : [ {
			id : 'Login',
			name : 'Login'
		}, {
			id : 'Admin',
			name : 'Admin'
		} ]
	}
});
