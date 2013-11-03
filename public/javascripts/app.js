'use strict';

// Declare app level module which depends on filters, and services
angular.module('travel_agency', ['travel_agency.filters', 'travel_agency.services', 'travel_agency.directives', '$strap.directives']).
  config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider.when('/',                      {templateUrl: '/assets/partials/main.html', controller: NoopCtrl});
    $routeProvider.when('/halloffame/:level',     {templateUrl: '/assets/partials/halloffame.html', controller: HallOfFameController});
    // Admin
    $routeProvider.when('/admin/anmeldungen',           {templateUrl: '/assets/partials/admin/anmeldungen.html', controller: NoopCtrl});
    $routeProvider.when('/admin/news',                  {templateUrl: '/assets/partials/admin/newsitems.html', controller: AdminNewsCtrl});
    $routeProvider.when('/admin/news/team/:team',       {templateUrl: '/assets/partials/admin/newsitems.html', controller: AdminNewsCtrl});
    $routeProvider.when('/admin/news/neu',              {templateUrl: '/assets/partials/admin/newsitem.html', controller: AdminNewsitemCtrl});
    $routeProvider.when('/admin/news/:id',              {templateUrl: '/assets/partials/admin/newsitem.html', controller: AdminNewsitemCtrl});
    $routeProvider.when('/admin/termine',               {templateUrl: '/assets/partials/admin/termine.html', controller: AdminEventsCtrl});
    $routeProvider.when('/admin/termine/:team/:season', {templateUrl: '/assets/partials/admin/termine.html', controller: AdminEventsCtrl});
    $routeProvider.when('/admin/termine/neu',           {templateUrl: '/assets/partials/admin/termin.html', controller: AdminEventCtrl});
    $routeProvider.when('/admin/termine/:id',           {templateUrl: '/assets/partials/admin/termin.html', controller: AdminEventCtrl});
    $routeProvider.when('/admin/eltern',                {templateUrl: '/assets/partials/admin/eltern.html', controller: AdminParentsCtrl});
    $routeProvider.when('/admin/spieler',               {templateUrl: '/assets/partials/admin/spieler.html', controller: AdminPlayersCtrl});
    $routeProvider.when('/admin/spieler/:team',         {templateUrl: '/assets/partials/admin/spieler.html', controller: AdminPlayersCtrl});
    $routeProvider.when('/admin/personen/neu',          {templateUrl: '/assets/partials/admin/person.html', controller: AdminPersonCtrl});
    $routeProvider.when('/admin/personen/:id',          {templateUrl: '/assets/partials/admin/person.html', controller: AdminPersonCtrl});
    $routeProvider.when('/admin/spiele/:team/:season',  {templateUrl: '/assets/partials/admin/spiele.html', controller: AdminGamesCtrl});
    $routeProvider.when('/admin/spiele/neu',            {templateUrl: '/assets/partials/admin/spiel.html', controller: AdminGameCtrl});
    $routeProvider.when('/admin/spiele/:id',            {templateUrl: '/assets/partials/admin/spiel.html', controller: AdminGameCtrl});
    $routeProvider.when('/admin/anmeldungen/team/:team',{templateUrl: '/assets/partials/admin/anmeldungen.html', controller: AdminGamesSignInCtrl});
    $routeProvider.when('/admin/anmeldungen/spiel/:id' ,{templateUrl: '/assets/partials/admin/anmeldung.html', controller: AdminGameSignInCtrl});
    // Verein
    $routeProvider.when('/verein/anfahrt',        {templateUrl: '/assets/partials/verein/anfahrt.html', controller: NoopCtrl});
    $routeProvider.when('/verein/impressum',      {templateUrl: '/assets/partials/verein/impressum.html', controller: NoopCtrl});
    $routeProvider.when('/verein/mitgliedwerden', {templateUrl: '/assets/partials/verein/mitgliedwerden.html', controller: NoopCtrl});
    $routeProvider.when('/verein/philosophie',    {templateUrl: '/assets/partials/verein/philosophie.html', controller: NoopCtrl});
    $routeProvider.when('/verein/team',           {templateUrl: '/assets/partials/verein/team.html', controller: NoopCtrl});
    // Mitgliederbereich
    $routeProvider.when("/mitgliederbereich/anabmeldung",  {templateUrl: "/assets/partials/user/anabmeldung.html", controller: UserSignInCtrl});
    $routeProvider.when("/mitgliederbereich/kontaktliste", {templateUrl: "/assets/partials/user/kontaktliste.html", controller: UserContactsCtrl});
    $routeProvider.when("/mitgliederbereich/kontaktdaten", {templateUrl: "/assets/partials/user/kontaktdaten.html", controller: UserContactCtrl});
    $routeProvider.when("/mitgliederbereich/passwort",     {templateUrl: "/assets/partials/user/passwort.html", controller: UserPasswordCtrl});
    // Spiele
    $routeProvider.when('/spiele/:team/:season',       {templateUrl: '/assets/partials/spiele/spiele.html', controller: OtherGamesCtrl});
    // News
    $routeProvider.when('/news/alihan',                {templateUrl: '/assets/partials/news/alihan2012.html', controller: OtherNewsCtrl});
    $routeProvider.when('/news/saisonauftaktMinis',    {templateUrl: '/assets/partials/news/saisonauftakt2012nachwuchs.html', controller: OtherNewsCtrl});
    $routeProvider.when('/news/saisonauftaktTorpedos', {templateUrl: '/assets/partials/news/saisonauftakt2012torpedos.html', controller: OtherNewsCtrl});
    $routeProvider.when('/news/halloffame',            {templateUrl: '/assets/partials/news/halloffame.html', controller: OtherNewsCtrl});
    $routeProvider.when('/news/vereinsfest',           {templateUrl: '/assets/partials/news/vereinsfest2012.html', controller: OtherNewsCtrl});
    // Login
    $routeProvider.when('/anmelden',              {templateUrl: '/assets/partials/anmelden.html', controller: NoopCtrl});
    // TODO Tmp
    $routeProvider.when('/tmp',              {templateUrl: '/assets/partials/tmp.html', controller: NoopCtrl});
    // TODO Fotos
    // TODO Projekte
    $routeProvider.otherwise({redirectTo: '/'});
  }]);
